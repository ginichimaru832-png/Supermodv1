package com.supermod.screen;

import com.supermod.block.SuperSmelterBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.FurnaceFuelSlot;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.screen.slot.Slot;

public class SuperSmelterScreenHandler extends ScreenHandler {

	private final Inventory inventory;
	private final PropertyDelegate propertyDelegate;

	// Client-side constructor (used when the server tells the client to open this screen).
	public SuperSmelterScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, new SimpleInventory(3), new ArrayPropertyDelegate(4));
	}

	// Server-side constructor (used by SuperSmelterBlockEntity#createMenu).
	public SuperSmelterScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate delegate) {
		super(com.supermod.screen.ModScreenHandlers.SUPER_SMELTER, syncId);
		checkSize(inventory, 3);
		checkDataCount(delegate, 4);
		this.inventory = inventory;
		this.propertyDelegate = delegate;
		this.inventory.onOpen(playerInventory.player);

		this.addSlot(new Slot(inventory, SuperSmelterBlockEntity.INPUT_SLOT, 56, 17));
		this.addSlot(new FurnaceFuelSlot(this, inventory, SuperSmelterBlockEntity.FUEL_SLOT, 56, 53));
		this.addSlot(new FurnaceOutputSlot(playerInventory.player, inventory, SuperSmelterBlockEntity.OUTPUT_SLOT, 116, 35));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
		}

		this.addProperties(delegate);
	}

	public boolean isBurning() {
		return this.propertyDelegate.get(0) > 0;
	}

	public int getCookProgress() {
		int total = this.propertyDelegate.get(3);
		int time = this.propertyDelegate.get(2);
		return total != 0 && time != 0 ? time * 24 / total : 0;
	}

	public int getFuelProgress() {
		int max = Math.max(this.propertyDelegate.get(1), 1);
		return this.propertyDelegate.get(0) * 13 / max;
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.inventory.canPlayerUse(player);
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int index) {
		ItemStack newStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasStack()) {
			ItemStack original = slot.getStack();
			newStack = original.copy();
			if (index < 3) {
				if (!this.insertItem(original, 3, 39, true)) return ItemStack.EMPTY;
			} else {
				boolean isFuel = isFuelIngredient(original);
				if (index >= 3 && index < 30) {
					if (!this.insertItem(original, 30, 39, false)) return ItemStack.EMPTY;
				} else if (index >= 30 && index < 39) {
					if (!this.insertItem(original, 3, 30, false)) return ItemStack.EMPTY;
				}
				if (isFuel) {
					if (!this.insertItem(original, 1, 2, false)) return ItemStack.EMPTY;
				} else if (!this.insertItem(original, 0, 1, false)) {
					return ItemStack.EMPTY;
				}
			}
			if (original.isEmpty()) slot.setStack(ItemStack.EMPTY); else slot.markDirty();
			if (original.getCount() == newStack.getCount()) return ItemStack.EMPTY;
			slot.onTakeItem(player, original);
		}
		return newStack;
	}

	private static boolean isFuelIngredient(ItemStack stack) {
		return net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.get(stack.getItem()) != null;
	}
}
