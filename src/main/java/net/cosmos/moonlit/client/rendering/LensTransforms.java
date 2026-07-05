package net.cosmos.moonlit.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.cosmos.moonlit.common.block_entity.forge.light.AbstractLensBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec2;

public final class LensTransforms {
    private LensTransforms() {}

    public static void applyFacing(AbstractLensBlockEntity blockEntity, PoseStack poseStack) {
        Direction direction = blockEntity.getFacing();

        if (direction == null) {
            return;
        }

        poseStack.rotateAround(
                direction.getRotation(),
                0.5F,
                0.5F,
                0.5F
        );
    }

    public static void applyMiddleTransform(
            AbstractLensBlockEntity blockEntity,
            PoseStack poseStack, float partialTick
    ) {
        applyFacing(blockEntity, poseStack);

        Vec2 angle = getRenderAngle(blockEntity,partialTick);
        float yawDegrees = angle == null ? 0.0F : angle.y;

        poseStack.rotateAround(
                Axis.YP.rotationDegrees(yawDegrees),
                0.5F,
                0.5F,
                0.5F
        );
    }

    public static void applyLensTransform(
            AbstractLensBlockEntity blockEntity,
            PoseStack poseStack, float partialTick
    ) {
        applyMiddleTransform(blockEntity, poseStack, partialTick);

        Vec2 angle = getRenderAngle(blockEntity,partialTick);
        float pitchDegrees = angle == null ? 0.0F : angle.x;

        poseStack.rotateAround(
                Axis.XP.rotationDegrees(pitchDegrees),
                0.5F,
                21F / 16F,
                0.5F
        );
    }

    private static Vec2 getRenderAngle(
            AbstractLensBlockEntity blockEntity,
            float partialTick
    ) {
        return blockEntity.getRenderAngle(partialTick);
    }


}