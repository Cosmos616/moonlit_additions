package net.cosmos.moonlit.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.cosmos.moonlit.client.ClientHelper;
import net.cosmos.moonlit.client.MoonlitModels;
import net.cosmos.moonlit.common.block_entity.forge.light.BeamHelpers;
import net.cosmos.moonlit.common.block_entity.forge.light.BronzeLensBlockEntity;
import net.cosmos.moonlit.common.block_entity.forge.light.LightBeam;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class BronzeLensRenderer implements BlockEntityRenderer<BronzeLensBlockEntity> {

    private final BlockRenderDispatcher blockRenderDispatcher;
    private final LightBeamRenderer lightBeamRenderer;

    public BronzeLensRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderDispatcher = context.getBlockRenderDispatcher();
        this.lightBeamRenderer = new LightBeamRenderer(context);
    }

    public void render(BronzeLensBlockEntity blockEntityIn, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
            renderDebug(blockEntityIn, partialTicks, poseStack, bufferIn, combinedLightIn, combinedOverlayIn);
        }
        if (blockEntityIn.lightBeam != null) lightBeamRenderer.render(blockEntityIn, partialTicks, poseStack, bufferIn, combinedLightIn, combinedOverlayIn);
        BakedModel lens = MoonlitModels.INSTANCE.bronzeLens;

        VertexConsumer buffer = bufferIn.getBuffer(ItemBlockRenderTypes.getRenderType(blockEntityIn.getBlockState(), false));

        poseStack.pushPose();
        float yaw = (float) Math.atan2(-blockEntityIn.rotation.x, blockEntityIn.rotation.z);
        float pitch = (float) Math.atan2(
                -blockEntityIn.rotation.y,
                Math.sqrt(blockEntityIn.rotation.x * blockEntityIn.rotation.x + blockEntityIn.rotation.z * blockEntityIn.rotation.z)
        );
        poseStack.rotateAround(Axis.YN.rotation(yaw), 0.5f, 0.5f, 0.5f);
        poseStack.rotateAround(Axis.XP.rotation(pitch), 0.5f, 0.5f, 0.5f);
        //Quaternionf transform = ClientHelper.rotateY((float) blockEntityIn.rotation.y);
        //transform.mul(ClientHelper.rotateX((float) blockEntityIn.rotation.x));
        //transform.mul(ClientHelper.rotateZ((float) blockEntityIn.rotation.z));
        //poseStack.rotateAround(transform, 0.5f, 0.5f, 0.5f);
        blockRenderDispatcher.getModelRenderer()
                .renderModel(poseStack.last(), buffer, blockEntityIn.getBlockState(),
                        lens, 1, 1, 1, combinedLightIn, combinedOverlayIn);

        poseStack.popPose();
    }

    private void renderDebug(BronzeLensBlockEntity blockEntityIn, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderType.lines());
        LightBeam lightBeam = blockEntityIn.lightBeam;
        BlockPos worldPos = blockEntityIn.getBlockPos();
        if (lightBeam != null) {
            if (lightBeam.getAvailablePositions() != null) {
                for (BlockPos pos : lightBeam.getAvailablePositions()) {
                    Vec3i relative = new Vec3i(pos.getX(), pos.getY(), pos.getZ());
                    AABB blockBox = new AABB(new BlockPos(relative));
                    poseStack.pushPose();
                    LevelRenderer.renderLineBox(poseStack, vertexconsumer, blockBox, 1.0F, 1.0F, 0.0F, 1.0F);
                    poseStack.popPose();
                }
            }
        }

        Vec3 pos = Vec3.ZERO.add(0.5, 0.5, 0.5);
        double range = blockEntityIn.beamRange;
        Vec3 endPoint = BeamHelpers.locate3DPos(blockEntityIn.rotation, pos, blockEntityIn.beamRange);
        AABB focus = new AABB(-range, -range, -range, range, range, range);
        poseStack.pushPose();
        LevelRenderer.renderLineBox(poseStack, vertexconsumer, focus, 1.0F, 0.5F, 0.5F, 0.5F);
        poseStack.popPose();
        PoseStack.Pose posestack$pose = poseStack.last();
        vertexconsumer.addVertex(posestack$pose,
                        (float)(pos.x),
                        (float)(pos.y),
                        (float)(pos.z))
                .setColor(-16776961)
                .setNormal(posestack$pose, (float)endPoint.x, (float)endPoint.y, (float)endPoint.z);
        vertexconsumer.addVertex(
                        posestack$pose,
                        (float)(endPoint.x),
                        (float)(endPoint.y),
                        (float)(endPoint.z)
                )
                .setColor(-16776961)
                .setNormal(posestack$pose, (float)endPoint.x, (float)endPoint.y, (float)endPoint.z);
    }

    public boolean shouldRenderOffScreen(BronzeLensBlockEntity blockEntityIn) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 96;
    }

    @Override
    public AABB getRenderBoundingBox(BronzeLensBlockEntity blockEntityIn) {
        return AABB.INFINITE;
    }
}
