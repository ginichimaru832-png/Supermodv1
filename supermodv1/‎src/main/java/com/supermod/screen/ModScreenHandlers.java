package com.supermod.screen;

import com.supermod.SuperMod;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModScreenHandlers {

	private ModScreenHandlers() {}

	public static final ScreenHandlerType<SuperSmelterScreenHandler> SUPER_SMELTER =
			Registry.register(Registries.SCREEN_HANDLER, new Identifier(SuperMod.MOD_ID, "super_smelter"),
					new ScreenHandlerType<>(SuperSmelterScreenHandler::new));

	public static final ScreenHandlerType<WardrobeScreenHandler> WARDROBE =
			Registry.register(Registries.SCREEN_HANDLER, new Identifier(SuperMod.MOD_ID, "wardrobe"),
					new ScreenHandlerType<>(WardrobeScreenHandler::new));

	public static final ExtendedScreenHandlerType<SkullBenchScreenHandler> SKULL_BENCH =
			Registry.register(Registries.SCREEN_HANDLER, new Identifier(SuperMod.MOD_ID, "skull_bench"),
					new ExtendedScreenHandlerType<>((syncId, inventory, buf) -> new SkullBenchScreenHandler(syncId, inventory)));

	public static void register() {
		SuperMod.LOGGER.info("[SuperMod] Screen handlers registered");
	}
}
