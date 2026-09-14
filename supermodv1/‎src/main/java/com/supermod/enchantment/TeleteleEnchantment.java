package com.supermod.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;

/**
 * Feature 5a: "Teletele" - works on any tool/weapon. Block drops and mob drops
 * are teleported straight into the holder's inventory instead of dropping on
 * the ground. See ModEvents for the actual pickup logic.
 */
public class TeleteleEnchantment extends Enchantment {

	public TeleteleEnchantment() {
		super(Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getMinPower(int level) {
		return 20;
	}

	@Override
	public int getMaxPower(int level) {
		return 55;
	}

	@Override
	public boolean isAcceptableItem(ItemStack stack) {
		return stack.getItem() instanceof MiningToolItem || stack.getItem() instanceof SwordItem;
	}

	@Override
	public boolean canAccept(Enchantment other) {
		return super.canAccept(other) && other != ModEnchantments.SMELTING_TOUCH;
	}
}
