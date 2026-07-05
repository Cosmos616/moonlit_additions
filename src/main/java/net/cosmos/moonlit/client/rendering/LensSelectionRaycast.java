package net.cosmos.moonlit.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.cosmos.moonlit.common.block_entity.forge.light.AbstractLensBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public final class LensSelectionRaycast {
    private LensSelectionRaycast() {}

    public static LensPartHit pickLensPart(
            AbstractLensBlockEntity blockEntity,
            Player player,
            float partialTick,
            double reach
    ) {
        Vec3 eye = player.getEyePosition(partialTick);
        Vec3 end = eye.add(player.getViewVector(partialTick).scale(reach));

        BlockPos pos = blockEntity.getBlockPos();

        BlockHitResult lensHit = raycastLensHead(blockEntity, eye, end, partialTick);
        BlockHitResult middleHit = raycastMiddle(blockEntity, eye, end, partialTick);

        LensPartHit best = null;

        if (lensHit != null) {
            best = new LensPartHit(
                    LensHitPart.LENS_HEAD,
                    lensHit,
                    lensHit.getLocation().distanceToSqr(eye)
            );
        }

        if (middleHit != null) {
            LensPartHit middle = new LensPartHit(
                    LensHitPart.MIDDLE,
                    middleHit,
                    middleHit.getLocation().distanceToSqr(eye)
            );

            if (best == null || middle.distanceSq() < best.distanceSq()) {
                best = middle;
            }
        }

        return best;
    }

    public static BlockHitResult raycastLensHead(
            AbstractLensBlockEntity blockEntity,
            Vec3 worldStart,
            Vec3 worldEnd,
            float partialTick
    ) {
        PoseStack transform = new PoseStack();
        LensTransforms.applyLensTransform(blockEntity, transform, partialTick);

        return raycastTransformedShape(
                blockEntity.getBlockPos(),
                LensSelectionShapes.LENS_HEAD,
                transform,
                worldStart,
                worldEnd
        );
    }

    public static BlockHitResult raycastMiddle(
            AbstractLensBlockEntity blockEntity,
            Vec3 worldStart,
            Vec3 worldEnd,
            float partialTick
    ) {
        PoseStack transform = new PoseStack();
        LensTransforms.applyMiddleTransform(blockEntity, transform, partialTick);

        return raycastTransformedShape(
                blockEntity.getBlockPos(),
                LensSelectionShapes.MIDDLE,
                transform,
                worldStart,
                worldEnd
        );
    }

    private static BlockHitResult raycastTransformedShape(
            BlockPos blockPos,
            VoxelShape shape,
            PoseStack transformStack,
            Vec3 worldStart,
            Vec3 worldEnd
    ) {
        Vec3 blockLocalStart = worldStart.subtract(
                blockPos.getX(),
                blockPos.getY(),
                blockPos.getZ()
        );

        Vec3 blockLocalEnd = worldEnd.subtract(
                blockPos.getX(),
                blockPos.getY(),
                blockPos.getZ()
        );

        Matrix4f forward = new Matrix4f(transformStack.last().pose());
        Matrix4f inverse = new Matrix4f(forward);
        inverse.invert();

        Vec3 shapeLocalStart = transformPosition(blockLocalStart, inverse);
        Vec3 shapeLocalEnd = transformPosition(blockLocalEnd, inverse);

        BlockHitResult localHit = shape.clip(
                shapeLocalStart,
                shapeLocalEnd,
                BlockPos.ZERO
        );

        if (localHit == null) {
            return null;
        }

        Vec3 transformedHitLocation = transformPosition(localHit.getLocation(), forward)
                .add(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        return new BlockHitResult(
                transformedHitLocation,
                localHit.getDirection(),
                blockPos,
                false
        );
    }

    private static Vec3 transformPosition(Vec3 vec, Matrix4f matrix) {
        Vector4f result = new Vector4f(
                (float) vec.x,
                (float) vec.y,
                (float) vec.z,
                1.0F
        );

        result.mul(matrix);

        return new Vec3(result.x(), result.y(), result.z());
    }
}