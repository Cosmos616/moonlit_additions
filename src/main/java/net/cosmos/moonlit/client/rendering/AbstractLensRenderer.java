package net.cosmos.moonlit.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.cosmos.moonlit.common.block_entity.forge.light.AbstractLensBlockEntity;
import net.cosmos.moonlit.common.block_entity.forge.light.BeamHelpers;
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
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractLensRenderer<T extends AbstractLensBlockEntity> implements BlockEntityRenderer<T> {

    private final BlockRenderDispatcher blockRenderDispatcher;
    private final LightBeamRenderer lightBeamRenderer;

    public AbstractLensRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderDispatcher = context.getBlockRenderDispatcher();
        this.lightBeamRenderer = new LightBeamRenderer(context);
    }

    @Override
    public void render(T blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
            renderDebug(blockEntity, poseStack, bufferIn);
        }

        if (blockEntity.getLightBeam() != null) {
            lightBeamRenderer.render(blockEntity.getLightBeam(), poseStack, bufferIn);
        }

        Direction direction = blockEntity.getFacing();

        BakedModel lens = lensModel();
        BakedModel middle = middleModel();

        VertexConsumer buffer = bufferIn.getBuffer(
                ItemBlockRenderTypes.getRenderType(blockEntity.getBlockState(), false)
        );

        poseStack.pushPose();

        float pitchDegrees = blockEntity.getAngle().x;
        float yawDegrees = blockEntity.getAngle().y;

        float modelYawOffset = 0.0F;
        float modelPitchOffset = 0.0F;

        poseStack.rotateAround(direction.getRotation(), 0.5f, 0.5f, 0.5f);

        poseStack.rotateAround(Axis.YP.rotationDegrees(yawDegrees + modelYawOffset), 0.5F, 0.5F, 0.5F);

        blockRenderDispatcher.getModelRenderer().renderModel(poseStack.last(), buffer, blockEntity.getBlockState(), middle, 1.0F, 1.0F, 1.0F, combinedLightIn, combinedOverlayIn);

        poseStack.rotateAround(Axis.XP.rotationDegrees(pitchDegrees + modelPitchOffset), 0.5F, 21f/16f, 0.5F);

        blockRenderDispatcher.getModelRenderer().renderModel(poseStack.last(), buffer, blockEntity.getBlockState(), lens, 1.0F, 1.0F, 1.0F, combinedLightIn, combinedOverlayIn);

        poseStack.popPose();
    }

    private void renderDebug(T blockEntity, PoseStack poseStack, MultiBufferSource bufferIn) {
        VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderType.lines());
        LightBeam lightBeam = blockEntity.getLightBeam();
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
            Vec3 pos = Vec3.ZERO.add(0.5, 0.5, 0.5);
            double range = lightBeam.getLastReachedPosition().distanceTo(lightBeam.position());
            Vec3 endPoint = BeamHelpers.locate3DPos(lightBeam.getAngle(), pos, (float) range);
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

    }

    @Override
    public boolean shouldRenderOffScreen(T blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 96;
    }

    @Override
    public AABB getRenderBoundingBox(T blockEntity) {
        return AABB.INFINITE;
    }

    public abstract BakedModel lensModel();
    public abstract BakedModel middleModel();
}
