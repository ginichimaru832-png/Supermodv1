package com.supermod;

import com.supermod.block.ModBlockEntities;
import com.supermod.block.ModBlocks;
import com.supermod.enchantment.ModEnchantments;
import com.supermod.item.ModItems;
import com.supermod.screen.ModScreenHandlers;
import com.supermod.village.ModVillagerTrades;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuperMod implements ModInitializer {

	public static final String MOD_ID = "supermod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.register();
		ModBlocks.register();
		ModBlockEntities.register();
		ModEnchantments.register();
		ModScreenHandlers.register();
		ModVillagerTrades.register();
		ModEvents.register();
		ModNetworking.registerServerReceivers();
		LOGGER.info("[SuperMod] Initialized - Super Smelter, Farming Fortune, Wardrobe, Skull Bench and 7 new enchantments are live.");
	}
}
