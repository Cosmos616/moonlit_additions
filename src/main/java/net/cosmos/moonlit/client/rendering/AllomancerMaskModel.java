package net.cosmos.moonlit.client.rendering;// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.LightTexture;
import team.lodestar.lodestone.systems.model.armor.LodestoneArmorModel;


public class AllomancerMaskModel extends LodestoneArmorModel {

	public AllomancerMaskModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition createBodyLayer() {
		return createArmorModel((mesh, root, head, body, right_arm, left_arm, leggings, right_legging, left_legging, right_foot, left_foot) -> {
			head.addOrReplaceChild("mask", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.51F)).texOffs(32, 0).addBox(-4.0F, -8.0F, -3.8F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.8F)), PartPose.offset(0.0F, 0.0F, 0.0F));
			return LayerDefinition.create(mesh, 64, 64);
		});

		//.texOffs(32, 0).addBox(-4.0F, -8.0F, -3.8F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.8F))
		//
	}

	@Override
	public void renderToBuffer(
			PoseStack poseStack,
			VertexConsumer vertexConsumer,
			int packedLight,
			int packedOverlay,
			int packedColor
	) {
		this.head.render(poseStack, vertexConsumer, LightTexture.FULL_BRIGHT, packedOverlay, packedColor);
	}
}
