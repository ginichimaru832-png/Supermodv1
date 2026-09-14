package com.supermod.village;

import com.supermod.enchantment.ModEnchantments;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

/**
 * Feature 2 (part of it): Farming Fortune is a treasure enchantment, so the
 * ONLY way to get it is trading with a Librarian. Level I shows up at
 * Journeyman, II at Expert, III at Master - mirroring how vanilla spreads
 * out its rarer enchant-book trades across villager levels.
 */
public final class ModVillagerTrades {

	private ModVillagerTrades() {}

	public static void register() {
		TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 3, factories -> factories.add(new FarmingFortuneBookFactory(1)));
		TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 4, factories -> factories.add(new FarmingFortuneBookFactory(2)));
		TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 5, factories -> factories.add(new FarmingFortuneBookFactory(3)));
	}

	private record FarmingFortuneBookFactory(int level) implements TradeOffers.Factory {
		@Override
		public TradeOffer create(Entity entity, Random random) {
			ItemStack book = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(ModEnchantments.FARMING_FORTUNE, level));
			int emeraldPrice = 6 + random.nextInt(8) + (level - 1) * 4;
			return new TradeOffer(new ItemStack(Items.EMERALD, emeraldPrice), book, 12, 10 + level * 5, 0.2F);
		}
	}
}
