package com.supermod;

import com.supermod.util.LastWardrobeTracker;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.GlobalPos;

public final class ModNetworking {

	public static final Identifier OPEN_LAST_WARDROBE = new Identifier(SuperMod.MOD_ID, "open_last_wardrobe");

	private ModNetworking() {}

	public static void registerServerReceivers() {
		ServerPlayNetworking.registerGlobalReceiver(OPEN_LAST_WARDROBE, (server, player, handler, buf, responseSender) -> {
			server.execute(() -> {
				GlobalPos pos = LastWardrobeTracker.get(player.getUuid());
				if (pos == null || pos.getDimension() != player.getWorld().getRegistryKey()) {
					player.sendMessage(Text.translatable("message.supermod.no_wardrobe"), true);
					return;
				}
				BlockEntity be = player.getWorld().getBlockEntity(pos.getPos());
				if (be instanceof NamedScreenHandlerFactory factory) {
					player.openHandledScreen(factory);
				} else {
					player.sendMessage(Text.translatable("message.supermod.no_wardrobe"), true);
				}
			});
		});
	}

	public static PacketByteBuf emptyBuf() {
		return PacketByteBufs.create();
	}
}
