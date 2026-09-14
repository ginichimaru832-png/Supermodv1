package com.supermod.client.screen;

import com.supermod.SuperMod;
import com.supermod.screen.SuperSmelterScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Visually identical layout to the vanilla furnace screen, just pointed at our
 * own (recolored) background texture and using the 10x-faster property delegate.
 */
public class SuperSmelterScreen extends HandledScreen<SuperSmelterScreenHandler> {

	private static final Identifier TEXTURE = new Identifier(SuperMod.MOD_ID, "textures/gui/super_smelter.png");

	public SuperSmelterScreen(SuperSmelterScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		int x = (width - backgroundWidth) / 2;
		int y = (height - backgroundHeight) / 2;
		context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

		if (handler.isBurning()) {
			int fuelHeight = handler.getFuelProgress();
			context.drawTexture(TEXTURE, x + 56, y + 53 + 12 - fuelHeight, 176, 12 - fuelHeight, 14, fuelHeight + 1);
		}

		int cookProgress = handler.getCookProgress();
		context.drawTexture(TEXTURE, x + 79, y + 17, 176, 14, cookProgress + 1, 16);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		drawMouseoverTooltip(context, mouseX, mouseY);
	}
}
