package net.cosmos.moonlit_additions.client.rendering;// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.cosmos.moonlit_additions.MoonlitAdditions;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;


public class BronzeMaskAllomancer<T extends LivingEntity> extends HumanoidModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION =
			new ModelLayerLocation(
					ResourceLocation.fromNamespaceAndPath(MoonlitAdditions.MOD_ID, "bronze_mask_allomancer"),
					"main"
			);

	public BronzeMaskAllomancer(ModelPart root) {
		super(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition root = meshDefinition.getRoot();

		PartDefinition head = root.addOrReplaceChild(
				"head",
				CubeListBuilder.create(),
				PartPose.ZERO
		);

		head.addOrReplaceChild(
				"bronze_mask",
				CubeListBuilder.create()
						.texOffs(0, 0)
						.addBox(-13.0F, -16.0F, -4.75F, 30.0F, 24.0F, 0.0F, new CubeDeformation(0.0F))
						.texOffs(0, 28)
						.addBox(-4.0F, -5.0F, -4.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)),
				PartPose.ZERO
		);

		root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);

		root.addOrReplaceChild(
				"right_arm",
				CubeListBuilder.create(),
				PartPose.offset(-5.0F, 2.0F, 0.0F)
		);

		root.addOrReplaceChild(
				"left_arm",
				CubeListBuilder.create(),
				PartPose.offset(5.0F, 2.0F, 0.0F)
		);

		root.addOrReplaceChild(
				"right_leg",
				CubeListBuilder.create(),
				PartPose.offset(-1.9F, 12.0F, 0.0F)
		);

		root.addOrReplaceChild(
				"left_leg",
				CubeListBuilder.create(),
				PartPose.offset(1.9F, 12.0F, 0.0F)
		);

		return LayerDefinition.create(meshDefinition, 64, 64);
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
