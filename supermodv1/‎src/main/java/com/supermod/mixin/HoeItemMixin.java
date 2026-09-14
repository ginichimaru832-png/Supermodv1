package com.supermod.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Part of Feature 2: hoes now mine melon and pumpkin blocks at axe-like
 * speed instead of the default (slow) hand-ish speed.
 */
@Mixin(HoeItem.class)
public class HoeItemMixin {

	@Inject(method = "getMiningSpeedMultiplier", at = @At("HEAD"), cancellable = true)
	private void supermod$fasterMelonPumpkin(ItemStack stack, BlockState state, CallbackInfoReturnable<Float> cir) {
		if (state.isOf(Blocks.MELON) || state.isOf(Blocks.PUMPKIN)) {
			cir.setReturnValue(8.0F);
		}
	}
}
