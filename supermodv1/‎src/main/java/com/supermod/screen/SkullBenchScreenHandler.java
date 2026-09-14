package com.supermod.screen;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;

public class SkullBenchScreenHandler extends ScreenHandler {

	public static final int HELMET_SLOT = 0;
	public static final int HEAD_SLOT = 1;
	public static final int OUTPUT_SLOT = 2;

	private final Inventory input = new SimpleInventory(2) {
		@Override
		public void markDirty() {
			super.markDirty();
			updateResult();
		}
	};
	private final Inventory output = new SimpleInventory(1);

	public SkullBenchScreenHandler(int syncId, PlayerInventory playerInventory) {
		super(ModScreenHandlers.SKULL_BENCH, syncId);

		this.addSlot(new Slot(input, HELMET_SLOT, 47, 33) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return stack.getItem() instanceof ArmorItem armor && armor.getSlotType() == EquipmentSlot.HEAD;
			}
		});
		this.addSlot(new Slot(input, HEAD_SLOT, 47, 61) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return isHeadItem(stack);
			}
		});
		this.addSlot(new Slot(output, OUTPUT_SLOT, 129, 47) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return false;
			}

			@Override
			public void onTakeItem(PlayerEntity player, ItemStack stack) {
				input.removeStack(HELMET_SLOT, 1);
				input.removeStack(HEAD_SLOT, 1);
				super.onTakeItem(player, stack);
			}
		});

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
		}
	}

	private static boolean isHeadItem(ItemStack stack) {
		Item item = stack.getItem();
		return item == Items.PLAYER_HEAD || item == Items.SKELETON_SKULL || item == Items.WITHER_SKELETON_SKULL
				|| item == Items.ZOMBIE_HEAD || item == Items.CREEPER_HEAD || item == Items.PIGLIN_HEAD
				|| item == Items.DRAGON_HEAD;
	}

	private void updateResult() {
		ItemStack helmet = input.getStack(HELMET_SLOT);
		ItemStack head = input.getStack(HEAD_SLOT);

		if (!helmet.isEmpty() && !head.isEmpty()) {
			ItemStack result = helmet.copy();
			result.setCount(1);
			NbtCompound resultNbt = result.getOrCreateNbt();

			// Carry the head's identity (profile for player heads, or just the
			// head item id for mob skulls) so the client renderer knows what
			// texture to draw on top of the helmet. See SkullHelmetFeatureRenderer.
			NbtCompound skinTag = new NbtCompound();
			skinTag.putString("SourceHead", net.minecraft.registry.Registries.ITEM.getId(head.getItem()).toString());
			if (head.hasNbt() && head.getNbt().contains("SkullOwner")) {
				skinTag.put("Profile", head.getNbt().getCompound("SkullOwner"));
			}
			resultNbt.put("SupermodSkullSkin", skinTag);

			output.setStack(0, result);
		} else {
			output.setStack(0, ItemStack.EMPTY);
		}
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int index) {
		ItemStack newStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasStack()) {
			ItemStack original = slot.getStack();
			newStack = original.copy();
			if (index == OUTPUT_SLOT) {
				if (!this.insertItem(original, 3, 39, true)) return ItemStack.EMPTY;
				slot.onTakeItem(player, original);
			} else if (index == HELMET_SLOT || index == HEAD_SLOT) {
				if (!this.insertItem(original, 3, 39, false)) return ItemStack.EMPTY;
			} else if (!this.insertItem(original, 0, 2, false)) {
				return ItemStack.EMPTY;
			}
			if (original.isEmpty()) slot.setStack(ItemStack.EMPTY); else slot.markDirty();
		}
		return newStack;
	}
}
