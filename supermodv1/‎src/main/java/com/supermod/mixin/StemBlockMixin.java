package com.supermod.mixin;

import com.supermod.util.StemGrowthTracker;
import net.minecraft.block.Blocks;
import net.minecraft.block.StemBlock;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * A StemBlock (the growing melon/pumpkin stalk) turns itself + a neighbor
 * into AttachedStemBlock + the fruit block during randomTick. We piggyback
 * on that same call: if a melon/pumpkin has just appeared next to this stem,
 * it must have just grown from it, so we tag that position.
 * <p>
 * NOTE (offline-authored caveat): if your Yarn mappings differ slightly and
 * this fails to compile, check the exact signature of
 * StemBlock#randomTick(BlockState, ServerWorld, BlockPos, Random) in your
 * local mappings (e.g. via the Linkie mappings browser) and adjust.
 */
@Mixin(StemBlock.class)
public class StemBlockMixin {

	@Inject(method = "randomTick", at = @At("TAIL"))
	private void supermod$onRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos neighbor = pos.offset(direction);
			BlockState neighborState = world.getBlockState(neighbor);
			if (neighborState.isOf(Blocks.MELON) || neighborState.isOf(Blocks.PUMPKIN)) {
				StemGrowthTracker.markStemGrown(world, neighbor);
			}
		}
	}
}
