package net.cosmos.moonlit.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.cosmos.moonlit.client.ClientHelper;
import net.cosmos.moonlit.common.block_entity.forge.light.AbstractLensBlockEntity;
import net.cosmos.moonlit.common.block_entity.forge.light.LightBeam;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import org.joml.Matrix4f;

public class LightBeamRenderer {

    private record BeamRenderSettings(
            int color,
            int layers,
            float startWidthRadius,
            float startHeightRadius,
            float endWidthRadius,
            float endHeightRadius,
            float endAlphaMultiplier
    ) {
    }

    public LightBeamRenderer(BlockEntityRendererProvider.Context context) {

    }

    public void render(
            AbstractLensBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource
    ) {
        LightBeam lightBeam = blockEntity.getLightBeam();
        if (lightBeam == null) {
            return;
        }
        float length = (float) lightBeam.position().distanceTo(lightBeam.getLastReachedPosition());

        BeamRenderSettings settings = new BeamRenderSettings(
                0x55d7ffE8,
                2,
                0.5F,
                0.5F,
                1F,
                1F,
                0.0F
        );
        poseStack.pushPose();

        LensTransforms.applyLensTransform(blockEntity, poseStack, partialTick);

        // Move beam origin to center of block.
        poseStack.translate(0.5D, 21d/16d, 0.5D);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(ClientHelper.LIGHT_BEAM);
        Matrix4f pose = poseStack.last().pose();

        renderBeam(vertexConsumer, pose, length, settings);

        poseStack.popPose();
    }

    private static void renderBeam(
            VertexConsumer vertexConsumer,
            Matrix4f pose,
            float length,
            BeamRenderSettings settings
    ) {
        BeamColor baseColor = BeamColor.fromArgb(settings.color());

        int layers = Math.max(settings.layers(), 1);

        for (int layer = 0; layer < layers; layer++) {
            float t = layers == 1 ? 1.0F : layer / (float) (layers - 1);

            // Outer layer is wider and softer.
            // Inner layer is narrower and brighter.
            float widthScale = lerp(1F, 0.8F, t);
            float alphaScale = lerp(0.1F, 0.5F, t);

            float startWidth = settings.startWidthRadius() * widthScale;
            float startHeight = settings.startHeightRadius() * widthScale;
            float endWidth = settings.endWidthRadius() * widthScale;
            float endHeight = settings.endHeightRadius() * widthScale;

            BeamColor startColor = baseColor.withAlpha(baseColor.alpha() * alphaScale);
            BeamColor endColor = baseColor.withAlpha(baseColor.alpha() * alphaScale * settings.endAlphaMultiplier());

            renderBeamPrism(
                    vertexConsumer,
                    pose,
                    length,
                    startWidth,
                    startHeight,
                    endWidth,
                    endHeight,
                    startColor,
                    endColor
            );
        }
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private static void applyLensRotation(LightBeam lightBeam, AbstractLensBlockEntity blockEntity, PoseStack poseStack) {
        if (lightBeam.angle == null) {
            return;
        }
        float pitchDegrees = lightBeam.angle.x;
        float yawDegrees = lightBeam.angle.y;

        float modelYawOffset = 0.0F;
        float modelPitchOffset = 0.0F;

        Direction direction = blockEntity.getFacing();

        poseStack.rotateAround(direction.getRotation(), 0.5f, 0.5f, 0.5f);

        poseStack.rotateAround(
                Axis.YP.rotationDegrees(yawDegrees + modelYawOffset),
                0.5F,
                21f/16f,
                0.5F
        );

        poseStack.rotateAround(
                Axis.XP.rotationDegrees(pitchDegrees + modelPitchOffset),
                0.5F,
                21f/16f,
                0.5F
        );
    }


    private static void renderBeamPrism(
            VertexConsumer vertexConsumer,
            Matrix4f pose,
            float length,
            float startWidth,
            float startHeight,
            float endWidth,
            float endHeight,
            BeamColor startColor,
            BeamColor endColor
    ) {
        float startZ = 0.0F;
        float endZ = -length;

        float sx0 = -startWidth;
        float sx1 = startWidth;
        float sy0 = -startHeight;
        float sy1 = startHeight;

        float ex0 = -endWidth;
        float ex1 = endWidth;
        float ey0 = -endHeight;
        float ey1 = endHeight;

        // Bottom side - inner face only
        addDoubleSidedQuadGradient(
                vertexConsumer,
                pose,
                sx0, sy0, startZ, startColor,
                ex0, ey0, endZ, endColor,
                ex1, ey0, endZ, endColor,
                sx1, sy0, startZ, startColor
        );

        // Right side - inner face only
        addDoubleSidedQuadGradient(
                vertexConsumer,
                pose,
                sx1, sy0, startZ, startColor,
                ex1, ey0, endZ, endColor,
                ex1, ey1, endZ, endColor,
                sx1, sy1, startZ, startColor
        );

        // Top side - inner face only
        addDoubleSidedQuadGradient(
                vertexConsumer,
                pose,
                sx1, sy1, startZ, startColor,
                ex1, ey1, endZ, endColor,
                ex0, ey1, endZ, endColor,
                sx0, sy1, startZ, startColor
        );

        // Left side - inner face only
        addDoubleSidedQuadGradient(
                vertexConsumer,
                pose,
                sx0, sy1, startZ, startColor,
                ex0, ey1, endZ, endColor,
                ex0, ey0, endZ, endColor,
                sx0, sy0, startZ, startColor
        );
    }

    private record BeamColor(
            float red,
            float green,
            float blue,
            float alpha
    ) {
        private static BeamColor fromArgb(int color) {
            return new BeamColor(
                    FastColor.ARGB32.red(color) / 255.0F,
                    FastColor.ARGB32.green(color) / 255.0F,
                    FastColor.ARGB32.blue(color) / 255.0F,
                    FastColor.ARGB32.alpha(color) / 255.0F
            );
        }

        private BeamColor withAlpha(float alpha) {
            return new BeamColor(
                    red,
                    green,
                    blue,
                    Math.max(0.0F, Math.min(1.0F, alpha))
            );
        }
    }

    private static void addDoubleSidedQuadGradient(
            VertexConsumer vertexConsumer,
            Matrix4f pose,
            float x1, float y1, float z1, BeamColor color1,
            float x2, float y2, float z2, BeamColor color2,
            float x3, float y3, float z3, BeamColor color3,
            float x4, float y4, float z4, BeamColor color4
    ) {
        addQuadGradient(
                vertexConsumer,
                pose,
                x1, y1, z1, color1,
                x2, y2, z2, color2,
                x3, y3, z3, color3,
                x4, y4, z4, color4
        );

        addQuadGradient(
                vertexConsumer,
                pose,
                x4, y4, z4, color4,
                x3, y3, z3, color3,
                x2, y2, z2, color2,
                x1, y1, z1, color1
        );
    }

    private static void addQuadGradient(
            VertexConsumer vertexConsumer,
            Matrix4f pose,
            float x1, float y1, float z1, BeamColor color1,
            float x2, float y2, float z2, BeamColor color2,
            float x3, float y3, float z3, BeamColor color3,
            float x4, float y4, float z4, BeamColor color4
    ) {
        addVertex(vertexConsumer, pose, x1, y1, z1, color1);
        addVertex(vertexConsumer, pose, x2, y2, z2, color2);
        addVertex(vertexConsumer, pose, x3, y3, z3, color3);
        addVertex(vertexConsumer, pose, x4, y4, z4, color4);
    }

    private static void addDoubleSidedQuad(
            VertexConsumer vertexConsumer,
            Matrix4f pose,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float x4, float y4, float z4,
            BeamColor color
    ) {
        addQuad(
                vertexConsumer,
                pose,
                x1, y1, z1,
                x2, y2, z2,
                x3, y3, z3,
                x4, y4, z4,
                color
        );

        addQuad(
                vertexConsumer,
                pose,
                x4, y4, z4,
                x3, y3, z3,
                x2, y2, z2,
                x1, y1, z1,
                color
        );
    }

    private static void addInnerFacingQuadGradient(
            VertexConsumer vertexConsumer,
            Matrix4f pose,
            float x1, float y1, float z1, BeamColor color1,
            float x2, float y2, float z2, BeamColor color2,
            float x3, float y3, float z3, BeamColor color3,
            float x4, float y4, float z4, BeamColor color4
    ) {
        // Reversed winding order.
        // This makes the quad face inward instead of outward.
        addQuadGradient(
                vertexConsumer,
                pose,
                x4, y4, z4, color4,
                x3, y3, z3, color3,
                x2, y2, z2, color2,
                x1, y1, z1, color1
        );
    }

    private static void addQuad(
            VertexConsumer vertexConsumer,
            Matrix4f pose,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float x4, float y4, float z4,
            BeamColor color
    ) {
        addVertex(vertexConsumer, pose, x1, y1, z1, color);
        addVertex(vertexConsumer, pose, x2, y2, z2, color);
        addVertex(vertexConsumer, pose, x3, y3, z3, color);
        addVertex(vertexConsumer, pose, x4, y4, z4, color);
    }

    private static void addVertex(
            VertexConsumer vertexConsumer,
            Matrix4f pose,
            float x,
            float y,
            float z,
            BeamColor color
    ) {
        vertexConsumer.addVertex(pose, x, y, z)
                .setColor(color.red(), color.green(), color.blue(), color.alpha());
    }
}
