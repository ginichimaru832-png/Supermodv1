package com.supermod.client;

import com.supermod.client.render.SkullHelmetArmorRenderer;
import com.supermod.client.screen.SuperSmelterScreen;
import com.supermod.client.screen.WardrobeScreen;
import com.supermod.item.ModItems;
import com.supermod.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.item.Items;

public class SuperModClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		HandledScreens.register(ModScreenHandlers.SUPER_SMELTER, SuperSmelterScreen::new);
		HandledScreens.register(ModScreenHandlers.WARDROBE, WardrobeScreen::new);
		// SkullBench reuses a plain generic-container-style layout - see SkullBenchScreen.
		HandledScreens.register(ModScreenHandlers.SKULL_BENCH, com.supermod.client.screen.SkullBenchScreen::new);

		ArmorRenderer.register(SkullHelmetArmorRenderer.INSTANCE,
				Items.LEATHER_HELMET, Items.CHAINMAIL_HELMET, Items.IRON_HELMET, Items.GOLDEN_HELMET,
				Items.DIAMOND_HELMET, Items.NETHERITE_HELMET, Items.TURTLE_HELMET,
				ModItems.FARMING_HELMET, ModItems.MINING_HELMET);
	}
}
