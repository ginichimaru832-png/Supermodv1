package com.supermod.client.screen;

import com.supermod.SuperMod;
import com.supermod.screen.WardrobeScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class WardrobeScreen extends HandledScreen<WardrobeScreenHandler> {

	private static final Identifier TEXTURE = new Identifier(SuperMod.MOD_ID, "textures/gui/wardrobe.png");

	public WardrobeScreen(WardrobeScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.backgroundHeight = 200;
		this.playerInventoryTitleY = this.backgroundHeight - 94;
	}

	@Override
	protected void init() {
		super.init();
		int x = (width - backgroundWidth) / 2;
		int y = (height - backgroundHeight) / 2;
		for (int loadout = 0; loadout < 4; loadout++) {
			final int loadoutIndex = loadout;
			this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.supermod.wardrobe.equip", loadout + 1),
					button -> {
						if (this.client != null && this.client.interactionManager != null) {
							this.client.interactionManager.clickButton(this.handler.syncId, loadoutIndex);
						}
					})
					.dimensions(x + 20 + loadout * 36, y + 92, 32, 16)
					.build());
		}
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		int x = (width - backgroundWidth) / 2;
		int y = (height - backgroundHeight) / 2;
		context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
	}
}
