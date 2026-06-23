package net.cosmos.moonlit_additions.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.cosmos.moonlit_additions.MoonLitAdditions;
import net.cosmos.moonlit_additions.block.ModBlocks;
import net.cosmos.moonlit_additions.block_entity.BronzeBellBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BronzeBellRenderer implements BlockEntityRenderer<BronzeBellBlockEntity> {
//    private static final ResourceLocation BELL_BODY_MODEL =
//            ResourceLocation.fromNamespaceAndPath(MoonLitAdditions.MOD_ID, "block/bronze_bell_body");

    public BronzeBellRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(
            BronzeBellBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        poseStack.pushPose();

        poseStack.translate(0.5D, 0.8D, 0.5D);

        float swingX = 0.0F;
        float swingZ = 0.0F;

        if (blockEntity.shaking) {
            float time = blockEntity.ticks + partialTick;
            float swing = Mth.sin(time / (float)Math.PI) / (4.0F + time / 3.0F);

            Direction direction = blockEntity.clickDirection;

            if (direction == Direction.NORTH) {
                swingX = -swing;
            } else if (direction == Direction.SOUTH) {
                swingX = swing;
            } else if (direction == Direction.EAST) {
                swingZ = -swing;
            } else if (direction == Direction.WEST) {
                swingZ = swing;
            }
        }

        poseStack.mulPose(Axis.XP.rotation(swingX));
        poseStack.mulPose(Axis.ZP.rotation(swingZ));

        poseStack.translate(-0.5D, -0.8D, -0.5D);

        Minecraft minecraft = Minecraft.getInstance();


        BlockState bodyState = ModBlocks.BRONZE_BELL_BODY_MODEL.get().defaultBlockState();

        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                bodyState,
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay
        );

        poseStack.popPose();
    }
}