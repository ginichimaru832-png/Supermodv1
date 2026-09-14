package com.supermod.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;

/**
 * Feature 5b: "Smelting Touch" - ore/log/etc drops come out already smelted
 * (raw iron -> iron ingot, log -> charcoal is NOT triggered, only blocks that
 * have a matching furnace recipe). See ModEvents for the conversion logic.
 */
public class SmeltingTouchEnchantment extends Enchantment {

	public SmeltingTouchEnchantment() {
		super(Rarity.RARE, EnchantmentTarget.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getMinPower(int level) {
		return 15;
	}

	@Override
	public int getMaxPower(int level) {
		return 50;
	}

	@Override
	public boolean isAcceptableItem(ItemStack stack) {
		return stack.getItem() instanceof MiningToolItem;
	}

	@Override
	public boolean canAccept(Enchantment other) {
		return super.canAccept(other) && !(other instanceof net.minecraft.enchantment.SilkTouchEnchantment)
				&& other != ModEnchantments.TELETELE;
	}
}
