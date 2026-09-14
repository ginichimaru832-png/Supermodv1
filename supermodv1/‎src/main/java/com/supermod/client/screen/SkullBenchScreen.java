package com.supermod.client.screen;

import com.supermod.SuperMod;
import com.supermod.screen.SkullBenchScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SkullBenchScreen extends HandledScreen<SkullBenchScreenHandler> {

	private static final Identifier TEXTURE = new Identifier(SuperMod.MOD_ID, "textures/gui/skull_bench.png");

	public SkullBenchScreen(SkullBenchScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		int x = (width - backgroundWidth) / 2;
		int y = (height - backgroundHeight) / 2;
		context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
	}
}
