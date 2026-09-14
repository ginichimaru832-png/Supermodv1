package com.supermod.item;

import com.supermod.SuperMod;
import com.supermod.block.ModBlocks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

/**
 * Registers every custom item added by Super Mod: the two armor sets and
 * the block-items for our new blocks.
 */
public final class ModItems {

	private ModItems() {}

	// ---- Farming armor set ("Harvester's" gear) ----
	public static final Item FARMING_HELMET = register("farming_helmet",
			new ArmorItem(ModArmorMaterials.FARMING, ArmorItem.Type.HELMET, new FabricItemSettings().maxCount(1)));
	public static final Item FARMING_CHESTPLATE = register("farming_chestplate",
			new ArmorItem(ModArmorMaterials.FARMING, ArmorItem.Type.CHESTPLATE, new FabricItemSettings().maxCount(1)));
	public static final Item FARMING_LEGGINGS = register("farming_leggings",
			new ArmorItem(ModArmorMaterials.FARMING, ArmorItem.Type.LEGGINGS, new FabricItemSettings().maxCount(1)));
	public static final Item FARMING_BOOTS = register("farming_boots",
			new ArmorItem(ModArmorMaterials.FARMING, ArmorItem.Type.BOOTS, new FabricItemSettings().maxCount(1)));

	// ---- Mining armor set ("Prospector's" gear) ----
	public static final Item MINING_HELMET = register("mining_helmet",
			new ArmorItem(ModArmorMaterials.MINING, ArmorItem.Type.HELMET, new FabricItemSettings().maxCount(1)));
	public static final Item MINING_CHESTPLATE = register("mining_chestplate",
			new ArmorItem(ModArmorMaterials.MINING, ArmorItem.Type.CHESTPLATE, new FabricItemSettings().maxCount(1)));
	public static final Item MINING_LEGGINGS = register("mining_leggings",
			new ArmorItem(ModArmorMaterials.MINING, ArmorItem.Type.LEGGINGS, new FabricItemSettings().maxCount(1)));
	public static final Item MINING_BOOTS = register("mining_boots",
			new ArmorItem(ModArmorMaterials.MINING, ArmorItem.Type.BOOTS, new FabricItemSettings().maxCount(1)));

	// ---- Block items ----
	public static final Item SUPER_SMELTER_ITEM = register("super_smelter",
			new BlockItem(ModBlocks.SUPER_SMELTER, new FabricItemSettings()));
	public static final Item WARDROBE_ITEM = register("wardrobe",
			new BlockItem(ModBlocks.WARDROBE, new FabricItemSettings()));
	public static final Item SKULL_BENCH_ITEM = register("skull_bench",
			new BlockItem(ModBlocks.SKULL_BENCH, new FabricItemSettings()));

	private static Item register(String path, Item item) {
		return Registry.register(Registries.ITEM, new Identifier(SuperMod.MOD_ID, path), item);
	}

	public static void register() {
		// Put everything into the Combat / Tools / Building tabs so it's easy to find in creative.
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
			entries.add(FARMING_HELMET);
			entries.add(FARMING_CHESTPLATE);
			entries.add(FARMING_LEGGINGS);
			entries.add(FARMING_BOOTS);
			entries.add(MINING_HELMET);
			entries.add(MINING_CHESTPLATE);
			entries.add(MINING_LEGGINGS);
			entries.add(MINING_BOOTS);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
			entries.add(SUPER_SMELTER_ITEM);
			entries.add(WARDROBE_ITEM);
			entries.add(SKULL_BENCH_ITEM);
		});
		SuperMod.LOGGER.info("[SuperMod] Items registered");
	}
}
