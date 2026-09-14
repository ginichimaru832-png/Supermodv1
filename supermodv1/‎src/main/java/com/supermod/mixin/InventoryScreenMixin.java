package com.supermod.mixin;

import com.supermod.ModNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Feature 4 (part 2): "Add new button from Access[ibility]" - a small
 * "Wardrobe" button in the top-right of the player's own inventory screen
 * that asks the server to reopen the last Wardrobe the player used, so you
 * can quick-swap loadouts without walking back to the block.
 */
@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {

	@Inject(method = "init", at = @At("TAIL"))
	private void supermod$addWardrobeButton(CallbackInfo ci) {
		InventoryScreen self = (InventoryScreen) (Object) this;
		int x = self.width / 2 + 78;
		int y = self.height / 2 - 90;
		self.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.supermod.open_wardrobe"),
						button -> ClientPlayNetworking.send(ModNetworking.OPEN_LAST_WARDROBE, ModNetworking.emptyBuf()))
				.dimensions(x, y, 20, 20)
				.build());
	}
}
