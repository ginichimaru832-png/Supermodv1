package com.supermod.block;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Feature 4: the Wardrobe. Right-click to open a small locker with 4 armor
 * loadout slots (helmet/chest/legs/boots each) and one "Equip" button per
 * loadout that instantly swaps your current armor with what's stored there.
 */
public class WardrobeBlock extends BlockWithEntity {

	public WardrobeBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new WardrobeBlockEntity(pos, state);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof NamedScreenHandlerFactory factory) {
				player.openHandledScreen(factory);
				com.supermod.util.LastWardrobeTracker.remember(player.getUuid(),
						net.minecraft.util.math.GlobalPos.create(world.getRegistryKey(), pos));
			}
		}
		return ActionResult.SUCCESS;
	}
}
