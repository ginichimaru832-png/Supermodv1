package com.supermod.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import com.supermod.item.ModArmorMaterials;

/**
 * Feature 3: exclusive enchantment for the Mining armor set. While worn,
 * gives permanent Haste and immunity to mining fatigue. See ModEvents#tickProspectorSense.
 */
public class ProspectorSenseEnchantment extends Enchantment {

	public ProspectorSenseEnchantment() {
		super(Rarity.RARE, EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public boolean isAcceptableItem(ItemStack stack) {
		return stack.getItem() instanceof ArmorItem armor && armor.getMaterial() == ModArmorMaterials.MINING;
	}
}
