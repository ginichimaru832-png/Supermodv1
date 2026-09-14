package com.supermod.client.render;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

/**
 * EXPERIMENTAL - this is the trickiest part of the mod to get pixel-perfect
 * without being able to test-launch the game. It:
 *  - falls back to normal vanilla helmet rendering (via ArmorRenderer.renderPart)
 *    when the item has no "SupermodSkullSkin" tag,
 *  - otherwise renders a skull/player-head model over the wearer's head using
 *    the stored profile texture.
 * If the head doesn't line up right or the texture doesn't load for custom
 * player heads, this is the file to tweak first.
 */
public class SkullHelmetArmorRenderer implements ArmorRenderer {

	public static final SkullHelmetArmorRenderer INSTANCE = new SkullHelmetArmorRenderer();

	private SkullEntityModel skullModel;

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack,
					   LivingEntity entity, EquipmentSlot slot, int light, BipedEntityModel<LivingEntity> contextModel) {
		if (slot != EquipmentSlot.HEAD) return;

		NbtCompound nbt = stack.getNbt();
		NbtCompound skin = nbt != null ? nbt.getCompound("SupermodSkullSkin") : null;

		if (skin == null || skin.isEmpty()) {
			renderVanillaHelmet(matrices, vertexConsumers, stack, light, contextModel);
			return;
		}

		try {
			renderSkullSkin(matrices, vertexConsumers, skin, entity, light, contextModel);
		} catch (Exception e) {
			// Never let a texture-lookup hiccup crash rendering; just show the plain helmet instead.
			renderVanillaHelmet(matrices, vertexConsumers, stack, light, contextModel);
		}
	}

	private void renderVanillaHelmet(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack,
									  int light, BipedEntityModel<LivingEntity> contextModel) {
		String namespace = "minecraft";
		String material = "iron";
		if (stack.getItem() instanceof net.minecraft.item.ArmorItem armor) {
			material = armor.getMaterial().getName();
			// Our own Farming/Mining helmets store their texture under the mod's namespace.
			if (material.startsWith("supermod_")) {
				namespace = "supermod";
				material = material.substring("supermod_".length());
			}
		}
		Identifier texture = new Identifier(namespace, "textures/models/armor/" + material + "_layer_1.png");
		ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, contextModel.head, texture);
	}

	private void renderSkullSkin(MatrixStack matrices, VertexConsumerProvider vertexConsumers, NbtCompound skin,
								  LivingEntity entity, int light, BipedEntityModel<LivingEntity> contextModel) {
		if (skullModel == null) {
			skullModel = new SkullEntityModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(EntityModelLayers.PLAYER_HEAD));
		}

		Identifier texture;
		if (skin.contains("Profile")) {
			GameProfile profile = net.minecraft.nbt.NbtHelper.toGameProfile(skin.getCompound("Profile"));
			texture = SkullBlockEntityRenderer.getTexture(profile);
		} else {
			// Mob skull (creeper/zombie/etc) - use its vanilla skull texture.
			String sourceHead = skin.getString("SourceHead");
			String name = sourceHead.contains(":") ? sourceHead.split(":")[1] : sourceHead;
			texture = new Identifier("minecraft", "textures/entity/" + name.replace("_head", "") + "/" + name.replace("_head", "") + ".png");
		}

		matrices.push();
		matrices.translate(0.0D, -0.35D, 0.0D);
		skullModel.setHeadRotation(0.0F, 0.0F, 0.0F);
		skullModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(texture)),
				light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
		matrices.pop();
	}
      }
