package com.supermod.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import com.supermod.screen.SuperSmelterScreenHandler;

import java.util.Optional;

/**
 * Same idea as a vanilla furnace but the total cook time for any recipe is
 * divided by 10 (minimum 1 tick), so it smelts roughly ten times faster.
 * Fuel is still consumed at the normal rate per item smelted - that's the
 * "cost" of the speed boost.
 */
public class SuperSmelterBlockEntity extends BlockEntity implements Inventory, NamedScreenHandlerFactory {

	public static final int INPUT_SLOT = 0;
	public static final int FUEL_SLOT = 1;
	public static final int OUTPUT_SLOT = 2;
	private static final int SPEED_MULTIPLIER = 10;

	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);

	private int fuelTime = 0;
	private int maxFuelTime = 0;
	private int cookTime = 0;
	private int cookTimeTotal = 20; // vanilla default (200) / 10

	protected final PropertyDelegate propertyDelegate;

	public SuperSmelterBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.SUPER_SMELTER, pos, state);
		this.propertyDelegate = new PropertyDelegate() {
			@Override
			public int get(int index) {
				return switch (index) {
					case 0 -> fuelTime;
					case 1 -> maxFuelTime;
					case 2 -> cookTime;
					case 3 -> cookTimeTotal;
					default -> 0;
				};
			}

			@Override
			public void set(int index, int value) {
				switch (index) {
					case 0 -> fuelTime = value;
					case 1 -> maxFuelTime = value;
					case 2 -> cookTime = value;
					case 3 -> cookTimeTotal = value;
				}
			}

			@Override
			public int size() {
				return 4;
			}
		};
	}

	public static void tick(net.minecraft.world.World world, BlockPos pos, BlockState state, SuperSmelterBlockEntity be) {
		boolean dirty = false;
		ItemStack input = be.inventory.get(INPUT_SLOT);

		Optional<SmeltingRecipe> match = !input.isEmpty()
				? world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, be, world)
				: Optional.empty();

		boolean hasRecipe = match.isPresent() && be.canAcceptRecipeOutput(match.get());

		// Consume fuel if we need to start/continue cooking and nothing is currently burning.
		if (be.fuelTime <= 0 && hasRecipe) {
			ItemStack fuel = be.inventory.get(FUEL_SLOT);
			int burnTime = getFuelBurnTime(fuel);
			if (burnTime > 0) {
				be.fuelTime = burnTime;
				be.maxFuelTime = burnTime;
				if (!fuel.isEmpty()) {
					fuel.decrement(1);
					if (fuel.isEmpty()) {
						be.inventory.set(FUEL_SLOT, fuel.getItem().getRecipeRemainder());
					}
				}
				dirty = true;
			}
		}

		boolean lit = be.fuelTime > 0;
		if (lit) {
			be.fuelTime--;
		}

		if (lit && hasRecipe) {
			be.cookTimeTotal = Math.max(1, match.get().getCookingTime() / SPEED_MULTIPLIER);
			be.cookTime++;
			if (be.cookTime >= be.cookTimeTotal) {
				be.cookTime = 0;
				be.craftRecipe(match.get());
				dirty = true;
			}
		} else {
			be.cookTime = Math.max(0, be.cookTime - 2);
		}

		if (dirty) {
			markDirty(world, pos, state);
		}
	}

	private boolean canAcceptRecipeOutput(Recipe<?> recipe) {
		if (this.inventory.get(INPUT_SLOT).isEmpty()) return false;
		ItemStack result = recipe.getOutput(null);
		if (result.isEmpty()) return false;
		ItemStack output = this.inventory.get(OUTPUT_SLOT);
		if (output.isEmpty()) return true;
		if (!output.isItemEqualIgnoreDamage(result)) return false;
		return output.getCount() + result.getCount() <= output.getMaxCount()
				&& output.getCount() + result.getCount() <= this.getMaxCountPerStack();
	}

	private void craftRecipe(Recipe<?> recipe) {
		ItemStack result = recipe.getOutput(null);
		ItemStack input = this.inventory.get(INPUT_SLOT);
		ItemStack output = this.inventory.get(OUTPUT_SLOT);

		if (output.isEmpty()) {
			this.inventory.set(OUTPUT_SLOT, result.copy());
		} else if (output.isItemEqualIgnoreDamage(result)) {
			output.increment(result.getCount());
		}
		input.decrement(1);
	}

	private static int getFuelBurnTime(ItemStack fuel) {
		if (fuel.isEmpty()) return 0;
		// Fabric API's FuelRegistry already includes all vanilla fuel values (coal, lava bucket, etc).
		Integer fabricFuel = net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.get(fuel.getItem());
		if (fabricFuel != null) return fabricFuel;
		return 0;
	}

	// ---- Inventory implementation ----

	@Override
	public int size() {
		return inventory.size();
	}

	@Override
	public boolean isEmpty() {
		return inventory.stream().allMatch(ItemStack::isEmpty);
	}

	@Override
	public ItemStack getStack(int slot) {
		return inventory.get(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack result = Inventories.splitStack(inventory, slot, amount);
		if (!result.isEmpty()) markDirty();
		return result;
	}

	@Override
	public ItemStack removeStack(int slot) {
		return Inventories.removeStack(inventory, slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		inventory.set(slot, stack);
		if (stack.getCount() > stack.getMaxCount()) stack.setCount(stack.getMaxCount());
		markDirty();
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return this.world != null && this.world.getBlockEntity(pos) == this
				&& player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
	}

	@Override
	public void clear() {
		inventory.clear();
	}

	// ---- Persistence ----

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		Inventories.writeNbt(nbt, inventory);
		nbt.putInt("FuelTime", fuelTime);
		nbt.putInt("MaxFuelTime", maxFuelTime);
		nbt.putInt("CookTime", cookTime);
		nbt.putInt("CookTimeTotal", cookTimeTotal);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
		Inventories.readNbt(nbt, inventory);
		fuelTime = nbt.getInt("FuelTime");
		maxFuelTime = nbt.getInt("MaxFuelTime");
		cookTime = nbt.getInt("CookTime");
		cookTimeTotal = nbt.getInt("CookTimeTotal");
	}

	// ---- Screen handler ----

	@Override
	public Text getDisplayName() {
		return Text.translatable("block.supermod.super_smelter");
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
		return new SuperSmelterScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
	}

	public boolean isBurning() {
		return fuelTime > 0;
	}
}
