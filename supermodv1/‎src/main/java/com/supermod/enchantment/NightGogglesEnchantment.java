package com.supermod.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

/**
 * Feature 5c: "Night Goggles" ("Night Google" in the request) - grants
 * permanent Night Vision while the enchanted helmet is worn.
 * See ModEvents#tickNightGoggles for the effect application.
 */
public class NightGogglesEnchantment extends Enchantment {

	public NightGogglesEnchantment() {
		super(Rarity.RARE, EnchantmentTarget.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD});
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
		return stack.getItem() instanceof ArmorItem armor && armor.getSlotType() == EquipmentSlot.HEAD;
	}
}
