package com.supermod.block;

import com.supermod.screen.WardrobeScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class WardrobeBlockEntity extends BlockEntity implements Inventory, NamedScreenHandlerFactory {

	public static final int LOADOUTS = 4;
	public static final int SLOTS_PER_LOADOUT = 4; // helmet, chest, legs, boots
	public static final int SIZE = LOADOUTS * SLOTS_PER_LOADOUT;

	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(SIZE, ItemStack.EMPTY);

	public WardrobeBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.WARDROBE, pos, state);
	}

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

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		Inventories.writeNbt(nbt, inventory);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		inventory = DefaultedList.ofSize(SIZE, ItemStack.EMPTY);
		Inventories.readNbt(nbt, inventory);
	}

	@Override
	public Text getDisplayName() {
		return Text.translatable("block.supermod.wardrobe");
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
		return new WardrobeScreenHandler(syncId, playerInventory, this);
	}
}
