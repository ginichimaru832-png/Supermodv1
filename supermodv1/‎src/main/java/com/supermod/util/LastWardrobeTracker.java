package com.supermod.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Remembers the last Wardrobe each player interacted with, so the quick-swap
 * button added to the vanilla inventory screen (see InventoryScreenMixin)
 * knows which one to open. In-memory only - forgotten on server restart.
 */
public final class LastWardrobeTracker {

	private static final Map<UUID, GlobalPos> LAST_WARDROBE = new ConcurrentHashMap<>();

	private LastWardrobeTracker() {}

	public static void remember(UUID playerId, GlobalPos pos) {
		LAST_WARDROBE.put(playerId, pos);
	}

	public static GlobalPos get(UUID playerId) {
		return LAST_WARDROBE.get(playerId);
	}
}
