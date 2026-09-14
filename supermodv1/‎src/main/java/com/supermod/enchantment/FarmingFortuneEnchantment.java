package com.supermod.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;

/**
 * Feature 2: "Farming Fortune" I-III.
 * Behaves like Fortune, but only for crops (wheat/carrot/potato/beetroot,
 * plus melon & pumpkin blocks that grew naturally from a stem).
 * It is a treasure enchantment: it never shows up at the enchanting table or
 * in an anvil's random offers - the ONLY source is the Librarian villager
 * trade registered in {@link com.supermod.village.ModVillagerTrades}.
 */
public class FarmingFortuneEnchantment extends Enchantment {

	public FarmingFortuneEnchantment() {
		super(Rarity.RARE, EnchantmentTarget.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public int getMinPower(int level) {
		return 15 + (level - 1) * 9;
	}

	@Override
	public int getMaxPower(int level) {
		return super.getMinPower(level) + 50;
	}

	@Override
	public boolean isTreasure() {
		return true;
	}

	@Override
	public boolean isAvailableForRandomSelection() {
		// Never offered at the enchanting table or in vanilla loot; villager trade only.
		return false;
	}

	@Override
	public boolean isAcceptableItem(ItemStack stack) {
		return stack.getItem() instanceof HoeItem;
	}
}
