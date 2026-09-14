package com.supermod.block;

import com.supermod.SuperMod;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModBlockEntities {

	private ModBlockEntities() {}

	public static final BlockEntityType<SuperSmelterBlockEntity> SUPER_SMELTER = Registry.register(
			Registries.BLOCK_ENTITY_TYPE,
			new Identifier(SuperMod.MOD_ID, "super_smelter"),
			FabricBlockEntityTypeBuilder.create(SuperSmelterBlockEntity::new, ModBlocks.SUPER_SMELTER).build());

	public static final BlockEntityType<WardrobeBlockEntity> WARDROBE = Registry.register(
			Registries.BLOCK_ENTITY_TYPE,
			new Identifier(SuperMod.MOD_ID, "wardrobe"),
			FabricBlockEntityTypeBuilder.create(WardrobeBlockEntity::new, ModBlocks.WARDROBE).build());

	public static void register() {
		SuperMod.LOGGER.info("[SuperMod] Block entities registered");
	}
}
