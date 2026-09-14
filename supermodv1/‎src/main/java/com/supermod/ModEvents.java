package com.supermod;

import com.supermod.enchantment.ModEnchantments;
import com.supermod.util.StemGrowthTracker;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.SimpleInventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * All of the "make the enchantment actually do something" logic lives here,
 * kept separate from registration so ModEnchantments stays declarative.
 */
public final class ModEvents {

	private ModEvents() {}

	public static void register() {
		PlayerBlockBreakEvents.AFTER.register(ModEvents::onBlockBreak);
		ServerTickEvents.END_SERVER_TICK.register(ModEvents::onServerTick);
		ServerLivingEntityEvents.AFTER_DEATH.register(ModEvents::onMobDeath);
		SuperMod.LOGGER.info("[SuperMod] Gameplay events registered");
	}

	/**
	 * Teletele for mob drops: if the killer was a player holding a Teletele
	 * weapon, vacuum up whatever the mob just dropped.
	 * (If your pinned Fabric API version doesn't have AFTER_DEATH, the
	 * fallback is a one-line Mixin into LivingEntity#onDeath calling this
	 * same method.)
	 */
	private static void onMobDeath(net.minecraft.entity.LivingEntity entity, net.minecraft.entity.damage.DamageSource damageSource) {
		if (!(entity.getWorld() instanceof ServerWorld world)) return;
		if (!(damageSource.getAttacker() instanceof ServerPlayerEntity player)) return;
		ItemStack weapon = player.getMainHandStack();
		if (EnchantmentHelper.getLevel(ModEnchantments.TELETELE, weapon) <= 0) return;

		List<ItemEntity> drops = world.getEntitiesByClass(ItemEntity.class, entity.getBoundingBox().expand(2.0), e -> e.age < 5);
		for (ItemEntity itemEntity : drops) {
			ItemStack leftover = player.getInventory().addStack(itemEntity.getStack());
			if (leftover.isEmpty()) itemEntity.discard(); else itemEntity.setStack(leftover);
		}
	}

	private static void onBlockBreak(net.minecraft.world.World world, net.minecraft.entity.player.PlayerEntity player,
									  BlockPos pos, net.minecraft.block.BlockState state, net.minecraft.block.entity.BlockEntity blockEntity) {
		if (!(world instanceof ServerWorld serverWorld) || !(player instanceof ServerPlayerEntity serverPlayer)) return;
		ItemStack tool = player.getMainHandStack();

		handleFarmingFortune(serverWorld, pos, state, tool);
		handleHardMiner(serverWorld, player, pos, state, tool);

		// Smelting Touch + Teletele both act on the drops that just landed at `pos`.
		int smeltingTouch = EnchantmentHelper.getLevel(ModEnchantments.SMELTING_TOUCH, tool);
		int teletele = EnchantmentHelper.getLevel(ModEnchantments.TELETELE, tool);
		if (smeltingTouch > 0 || teletele > 0) {
			List<ItemEntity> drops = serverWorld.getEntitiesByClass(ItemEntity.class,
					new Box(pos).expand(1.5), e -> !e.cannotPickup() && e.age < 5);
			for (ItemEntity itemEntity : drops) {
				ItemStack stack = itemEntity.getStack();
				if (smeltingTouch > 0) {
					stack = trySmelt(serverWorld, stack);
					itemEntity.setStack(stack);
				}
				if (teletele > 0) {
					ItemStack leftover = serverPlayer.getInventory().addStack(stack);
					if (leftover.isEmpty()) {
						itemEntity.discard();
					} else {
						itemEntity.setStack(leftover);
					}
				}
			}
		}
	}

	private static void handleFarmingFortune(ServerWorld world, BlockPos pos, net.minecraft.block.BlockState state, ItemStack tool) {
		int level = EnchantmentHelper.getLevel(ModEnchantments.FARMING_FORTUNE, tool);

		net.minecraft.item.Item bonusItem = null;
		if (level > 0) {
			if (state.isOf(Blocks.WHEAT) && state.contains(Properties.AGE_7) && state.get(Properties.AGE_7) == 7) {
				bonusItem = Items.WHEAT;
			} else if (state.isOf(Blocks.CARROTS) && state.contains(Properties.AGE_7) && state.get(Properties.AGE_7) == 7) {
				bonusItem = Items.CARROT;
			} else if (state.isOf(Blocks.POTATOES) && state.contains(Properties.AGE_7) && state.get(Properties.AGE_7) == 7) {
				bonusItem = Items.POTATO;
			} else if (state.isOf(Blocks.BEETROOTS) && state.contains(Properties.AGE_3) && state.get(Properties.AGE_3) == 3) {
				bonusItem = Items.BEETROOT;
			} else if (state.isOf(Blocks.MELON) && StemGrowthTracker.isStemGrown(world, pos)) {
				bonusItem = Items.MELON_SLICE;
			} else if (state.isOf(Blocks.PUMPKIN) && StemGrowthTracker.isStemGrown(world, pos)) {
				bonusItem = Items.PUMPKIN;
			}
		}

		if (state.isOf(Blocks.MELON) || state.isOf(Blocks.PUMPKIN)) {
			StemGrowthTracker.clear(world, pos);
		}

		if (bonusItem != null) {
			int bonus = world.random.nextInt(level + 1);
			if (bonus > 0) {
				net.minecraft.util.ItemScatterer.spawn(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
						new ItemStack(bonusItem, bonus));
			}
		}
	}

	private static void handleHardMiner(ServerWorld world, net.minecraft.entity.player.PlayerEntity player,
										 BlockPos pos, net.minecraft.block.BlockState state, ItemStack tool) {
		if (!(tool.getItem() instanceof PickaxeItem)) return;
		if (EnchantmentHelper.getLevel(ModEnchantments.HARD_MINER, tool) <= 0) return;
		if (world.random.nextFloat() >= 0.5F) return;

		List<BlockPos> candidates = new ArrayList<>();
		for (Direction direction : Direction.values()) {
			BlockPos neighbor = pos.offset(direction);
			net.minecraft.block.BlockState neighborState = world.getBlockState(neighbor);
			float hardness = neighborState.getHardness(world, neighbor);
			if (!neighborState.isAir() && hardness >= 0.0F && hardness <= 50.0F) {
				candidates.add(neighbor);
			}
		}
		java.util.Collections.shuffle(candidates, new java.util.Random(world.random.nextLong()));
		int extraBreaks = 2 + world.random.nextInt(2); // 2-3 extra blocks
		int broken = 0;
		for (BlockPos candidate : candidates) {
			if (broken >= extraBreaks) break;
			// NOTE: breaking blocks this way does not re-trigger PlayerBlockBreakEvents,
			// so Teletele/Smelting Touch/Farming Fortune don't chain off Hard Miner hits.
			world.breakBlock(candidate, true, player);
			broken++;
		}
	}

	private static ItemStack trySmelt(ServerWorld world, ItemStack input) {
		if (input.isEmpty()) return input;
		SimpleInventory testInventory = new SimpleInventory(input);
		Optional<SmeltingRecipe> recipe = world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, testInventory, world);
		if (recipe.isPresent()) {
			ItemStack output = recipe.get().getOutput(world.getRegistryManager());
			if (!output.isEmpty()) {
				ItemStack result = output.copy();
				result.setCount(input.getCount());
				return result;
			}
		}
		return input;
	}

	private static void onServerTick(net.minecraft.server.MinecraftServer server) {
		if (server.getTicks() % 20 != 0) return; // once a second is plenty for these effects
		for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
			applyNightGoggles(player);
			applySetBonus(player, ModEnchantments.GREEN_THUMB, StatusEffects.HASTE);
			applySetBonus(player, ModEnchantments.PROSPECTOR_SENSE, StatusEffects.HASTE);
		}
	}

	private static void applyNightGoggles(ServerPlayerEntity player) {
		ItemStack helmet = player.getEquippedStack(EquipmentSlot.HEAD);
		if (EnchantmentHelper.getLevel(ModEnchantments.NIGHT_GOGGLES, helmet) > 0) {
			player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 240, 0, true, false, true));
		}
	}

	private static void applySetBonus(ServerPlayerEntity player, net.minecraft.enchantment.Enchantment enchantment,
									   net.minecraft.entity.effect.StatusEffect effect) {
		ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);
		int level = EnchantmentHelper.getLevel(enchantment, chest);
		if (level > 0) {
			player.addStatusEffect(new StatusEffectInstance(effect, 40, level - 1, true, false, true));
		}
	}
}
