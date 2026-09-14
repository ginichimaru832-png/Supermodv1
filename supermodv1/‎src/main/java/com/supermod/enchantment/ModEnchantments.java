package com.supermod.enchantment;

import com.supermod.SuperMod;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModEnchantments {

	private ModEnchantments() {}

	public static final Enchantment FARMING_FORTUNE = register("farming_fortune", new FarmingFortuneEnchantment());
	public static final Enchantment TELETELE = register("teletele", new TeleteleEnchantment());
	public static final Enchantment SMELTING_TOUCH = register("smelting_touch", new SmeltingTouchEnchantment());
	public static final Enchantment NIGHT_GOGGLES = register("night_goggles", new NightGogglesEnchantment());
	public static final Enchantment HARD_MINER = register("hard_miner", new HardMinerEnchantment());
	public static final Enchantment GREEN_THUMB = register("green_thumb", new GreenThumbEnchantment());
	public static final Enchantment PROSPECTOR_SENSE = register("prospector_sense", new ProspectorSenseEnchantment());

	private static Enchantment register(String path, Enchantment enchantment) {
		return Registry.register(Registries.ENCHANTMENT, new Identifier(SuperMod.MOD_ID, path), enchantment);
	}

	public static void register() {
		SuperMod.LOGGER.info("[SuperMod] Enchantments registered");
	}
}
