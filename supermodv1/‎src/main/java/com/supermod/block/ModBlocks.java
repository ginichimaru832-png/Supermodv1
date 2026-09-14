package com.supermod.block;

import com.supermod.SuperMod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModBlocks {

	private ModBlocks() {}

	public static final Block SUPER_SMELTER = register("super_smelter",
			new SuperSmelterBlock(AbstractBlock.Settings.copy(net.minecraft.block.Blocks.FURNACE)
					.mapColor(MapColor.STONE_GRAY)
					.strength(4.0F, 8.0F)));

	public static final Block WARDROBE = register("wardrobe",
			new WardrobeBlock(AbstractBlock.Settings.copy(net.minecraft.block.Blocks.BARREL)
					.mapColor(MapColor.BROWN)
					.strength(2.5F)));

	public static final Block SKULL_BENCH = register("skull_bench",
			new SkullBenchBlock(AbstractBlock.Settings.copy(net.minecraft.block.Blocks.SMITHING_TABLE)
					.mapColor(MapColor.STONE_GRAY)
					.strength(2.5F)));

	private static Block register(String path, Block block) {
		return Registry.register(Registries.BLOCK, new Identifier(SuperMod.MOD_ID, path), block);
	}

	public static void register() {
		SuperMod.LOGGER.info("[SuperMod] Blocks registered");
	}
}
