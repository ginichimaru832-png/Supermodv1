package com.supermod.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import com.supermod.item.ModArmorMaterials;

/**
 * Feature 3: exclusive enchantment for the Farming armor set. While worn,
 * gives Haste near farmland/crops and a chance to instantly grow crops you
 * walk over. See ModEvents#tickGreenThumb.
 */
public class GreenThumbEnchantment extends Enchantment {

	public GreenThumbEnchantment() {
		super(Rarity.RARE, EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public boolean isAcceptableItem(ItemStack stack) {
		return stack.getItem() instanceof ArmorItem armor && armor.getMaterial() == ModArmorMaterials.FARMING;
	}
}
