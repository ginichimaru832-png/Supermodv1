package com.supermod.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Lazy;

import java.util.EnumMap;
import java.util.Map;

/**
 * Two brand-new armor materials:
 *  - FARMING ("Harvester's" set): light armor (leather-tier protection) that leans on
 *    the new Green Thumb enchantment rather than raw defense.
 *  - MINING ("Prospector's" set): tanky, iron/diamond-tier protection that leans on the
 *    new Prospector's Sense enchantment.
 */
public enum ModArmorMaterials implements ArmorMaterial {
	FARMING("supermod_farming", 18,
			mapOf(2, 5, 6, 2), 12, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F, 0.0F,
			() -> Ingredient.ofItems(Items.HAY_BLOCK)),

	MINING("supermod_mining", 33,
			mapOf(3, 7, 8, 3), 14, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 2.0F, 0.05F,
			() -> Ingredient.ofItems(Items.IRON_INGOT, Items.DIAMOND));

	// Durability multiplier per slot, matches vanilla's base table (boots, legs, chest, helmet)
	private static final int[] BASE_DURABILITY = {13, 15, 16, 11};

	private final String name;
	private final int durabilityMultiplier;
	private final Map<ArmorItem.Type, Integer> protectionAmounts;
	private final int enchantability;
	private final SoundEvent equipSound;
	private final float toughness;
	private final float knockbackResistance;
	private final Lazy<Ingredient> repairIngredient;

	ModArmorMaterials(String name, int durabilityMultiplier, Map<ArmorItem.Type, Integer> protectionAmounts,
					   int enchantability, SoundEvent equipSound, float toughness, float knockbackResistance,
					   java.util.function.Supplier<Ingredient> repairIngredient) {
		this.name = name;
		this.durabilityMultiplier = durabilityMultiplier;
		this.protectionAmounts = protectionAmounts;
		this.enchantability = enchantability;
		this.equipSound = equipSound;
		this.toughness = toughness;
		this.knockbackResistance = knockbackResistance;
		this.repairIngredient = new Lazy<>(repairIngredient);
	}

	private static Map<ArmorItem.Type, Integer> mapOf(int boots, int legs, int chest, int helmet) {
		Map<ArmorItem.Type, Integer> map = new EnumMap<>(ArmorItem.Type.class);
		map.put(ArmorItem.Type.BOOTS, boots);
		map.put(ArmorItem.Type.LEGGINGS, legs);
		map.put(ArmorItem.Type.CHESTPLATE, chest);
		map.put(ArmorItem.Type.HELMET, helmet);
		return map;
	}

	@Override
	public int getDurability(ArmorItem.Type type) {
		return BASE_DURABILITY[type.ordinal()] * this.durabilityMultiplier;
	}

	@Override
	public int getProtectionAmount(ArmorItem.Type type) {
		return this.protectionAmounts.get(type);
	}

	@Override
	public int getEnchantability() {
		return this.enchantability;
	}

	@Override
	public SoundEvent getEquipSound() {
		return this.equipSound;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return this.repairIngredient.get();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public float getToughness() {
		return this.toughness;
	}

	@Override
	public float getKnockbackResistance() {
		return this.knockbackResistance;
	}
}
