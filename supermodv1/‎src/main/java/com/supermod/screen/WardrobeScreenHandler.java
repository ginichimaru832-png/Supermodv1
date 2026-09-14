package com.supermod.screen;

import com.supermod.block.WardrobeBlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class WardrobeScreenHandler extends ScreenHandler {

	private static final EquipmentSlot[] ROW_SLOT_TYPE = {
			EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
	};

	private final Inventory inventory;

	public WardrobeScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, new SimpleInventory(WardrobeBlockEntity.SIZE));
	}

	public WardrobeScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
		super(ModScreenHandlers.WARDROBE, syncId);
		checkSize(inventory, WardrobeBlockEntity.SIZE);
		this.inventory = inventory;
		this.inventory.onOpen(playerInventory.player);

		for (int loadout = 0; loadout < WardrobeBlockEntity.LOADOUTS; loadout++) {
			for (int row = 0; row < WardrobeBlockEntity.SLOTS_PER_LOADOUT; row++) {
				int index = loadout * WardrobeBlockEntity.SLOTS_PER_LOADOUT + row;
				EquipmentSlot expected = ROW_SLOT_TYPE[row];
				int x = 26 + loadout * 36;
				int y = 18 + row * 18;
				this.addSlot(new Slot(inventory, index, x, y) {
					@Override
					public boolean canInsert(ItemStack stack) {
						return stack.getItem() instanceof ArmorItem armor && armor.getSlotType() == expected;
					}
				});
			}
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 106 + i * 18));
			}
		}
		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 164));
		}
	}

	/**
	 * Buttons 0..3, one per loadout column: swap what's stored in that
	 * loadout with whatever the player currently has equipped.
	 */
	@Override
	public boolean onButtonClick(PlayerEntity player, int id) {
		if (id < 0 || id >= WardrobeBlockEntity.LOADOUTS) return false;
		if (player.getWorld().isClient) return true;

		for (int row = 0; row < WardrobeBlockEntity.SLOTS_PER_LOADOUT; row++) {
			int index = id * WardrobeBlockEntity.SLOTS_PER_LOADOUT + row;
			EquipmentSlot slotType = ROW_SLOT_TYPE[row];
			ItemStack stored = inventory.getStack(index).copy();
			ItemStack worn = player.getEquippedStack(slotType).copy();

			inventory.setStack(index, worn);
			player.equipStack(slotType, stored);
		}
		inventory.markDirty();
		return true;
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
			int wardrobeSize = WardrobeBlockEntity.SIZE;
			if (index < wardrobeSize) {
				if (!this.insertItem(original, wardrobeSize, this.slots.size(), true)) return ItemStack.EMPTY;
			} else if (!this.insertItem(original, 0, wardrobeSize, false)) {
				return ItemStack.EMPTY;
			}
			if (original.isEmpty()) slot.setStack(ItemStack.EMPTY); else slot.markDirty();
		}
		return newStack;
	}
    }
