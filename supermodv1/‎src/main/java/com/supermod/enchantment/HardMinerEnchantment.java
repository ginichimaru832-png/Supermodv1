package com.supermod.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;

/**
 * Feature 5d: "Hard Miner" - pickaxe only. 50% chance per block mined to also
 * break 2-3 extra adjacent blocks (mini vein-breaker). See ModEvents.
 */
public class HardMinerEnchantment extends Enchantment {

	public HardMinerEnchantment() {
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
		return stack.getItem() instanceof PickaxeItem;
	}
}
