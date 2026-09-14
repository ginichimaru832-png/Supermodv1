package com.supermod.util;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Remembers which melon/pumpkin blocks were grown naturally by an attached
 * stem (as opposed to being placed directly by a player), so that Farming
 * Fortune only applies its bonus drops to the "real" farmed ones.
 * <p>
 * NOTE: this is deliberately kept as simple in-memory tracking rather than a
 * PersistentState (world-save) implementation, so a server restart forgets
 * which melons/pumpkins were stem-grown. If you want it to survive restarts,
 * swap this out for a PersistentState per-dimension - the hook points
 * (StemBlockMixin and ModEvents) stay the same either way.
 */
public final class StemGrowthTracker {

	private static final Map<RegistryKey<World>, LongSet> STEM_GROWN = new ConcurrentHashMap<>();

	private StemGrowthTracker() {}

	public static void markStemGrown(World world, BlockPos pos) {
		STEM_GROWN.computeIfAbsent(world.getRegistryKey(), k -> new LongOpenHashSet()).add(pos.asLong());
	}

	public static boolean isStemGrown(World world, BlockPos pos) {
		LongSet set = STEM_GROWN.get(world.getRegistryKey());
		return set != null && set.contains(pos.asLong());
	}

	public static void clear(World world, BlockPos pos) {
		LongSet set = STEM_GROWN.get(world.getRegistryKey());
		if (set != null) set.remove(pos.asLong());
	}
}
