package com.supermod.block;

import com.supermod.screen.ModScreenHandlers;
import com.supermod.screen.SkullBenchScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraft.block.BlockState;

/**
 * Feature 6: a workbench, laid out just like the vanilla Smithing Table,
 * that combines a Helmet + any Player Head into a helmet wearing that
 * player's skin as its "face". Unlike SuperSmelter/Wardrobe this block has
 * no persistent state of its own - it's a stateless 2-in-1-out crafting UI,
 * same as the vanilla smithing table.
 */
public class SkullBenchBlock extends Block {

	public SkullBenchBlock(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient) {
			player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
					(syncId, inv, p) -> new SkullBenchScreenHandler(syncId, inv),
					Text.translatable("block.supermod.skull_bench")));
		}
		return ActionResult.SUCCESS;
	}
}
