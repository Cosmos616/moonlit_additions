package net.cosmos.moonlit.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.cosmos.moonlit.client.ClientHelper;
import net.cosmos.moonlit.common.block_entity.forge.light.LightBeam;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.FastColor;
import org.joml.Matrix4f;

public class LightBeamRenderer {

    private record BeamRenderSettings(
            int color,
            int layers,
            float startWidthRadius,
            float startHeightRadius,
            float endWidthRadius,
            float endHeightRadius
    ) {
    }

    public LightBeamRenderer(BlockEntityRendererProvider.Context context) {

    }

    public void render(
            LightBeam lightBeam,
            PoseStack poseStack,
            MultiBufferSource bufferSource
    ) {
        float length = (float) lightBeam.position().distanceTo(lightBeam.getLastReachedPosition());

        BeamRenderSettings settings = new BeamRenderSettings(
                0x40FFFFFF, // color: ARGB
                1,          // layers
                0.25F,      // start width radius
                0.25F,      // start height radius
                0.25F,      // end width radius
                0.25F       // end height radius
        );

        poseStack.pushPose();

        applyLensRotation(lightBeam, poseStack);

        // Move beam origin to center of block.
        poseStack.translate(0.5D, 0.5D, 0.5D);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(ClientHelper.LIGHTNING_CULL);
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
        BeamColor color = BeamColor.fromArgb(settings.color());

        int layers = Math.max(settings.layers(), 1);

        for (int layer = 1; layer <= layers; layer++) {
            float layerScale = layer / (float) layers;

            float startWidth = settings.startWidthRadius() * layerScale;
            float startHeight = settings.startHeightRadius() * layerScale;
            float endWidth = settings.endWidthRadius() * layerScale;
            float endHeight = settings.endHeightRadius() * layerScale;

            renderBeamPrism(
                    vertexConsumer,
                    pose,
                    length,
                    startWidth,
                    startHeight,
                    endWidth,
                    endHeight,
                    color
            );
        }
    }

    private static void applyLensRotation(LightBeam lightBeam, PoseStack poseStack) {
        if (lightBeam.cachedAngle() == null) {
            return;
        }
        float pitchDegrees = lightBeam.cachedAngle().x;
        float yawDegrees = lightBeam.cachedAngle().y;

        float modelYawOffset = 0.0F;
        float modelPitchOffset = 90.0F;

        poseStack.rotateAround(
                Axis.YP.rotationDegrees(yawDegrees + modelYawOffset),
                0.5F,
                0.5F,
                0.5F
        );

        poseStack.rotateAround(
                Axis.XP.rotationDegrees(pitchDegrees + modelPitchOffset),
                0.5F,
                0.5F,
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
            BeamColor color
    ) {
        float startZ = 0.0F;
        float endZ = -length;

        // Start face corners
        float sx0 = -startWidth;
        float sx1 = startWidth;
        float sy0 = -startHeight;
        float sy1 = startHeight;

        // End face corners
        float ex0 = -endWidth;
        float ex1 = endWidth;
        float ey0 = -endHeight;
        float ey1 = endHeight;

        // Bottom side
        addDoubleSidedQuad(
                vertexConsumer,
                pose,
                sx0, sy0, startZ,
                ex0, ey0, endZ,
                ex1, ey0, endZ,
                sx1, sy0, startZ,
                color
        );

        // Right side
        addDoubleSidedQuad(
                vertexConsumer,
                pose,
                sx1, sy0, startZ,
                ex1, ey0, endZ,
                ex1, ey1, endZ,
                sx1, sy1, startZ,
                color
        );

        // Top side
        addDoubleSidedQuad(
                vertexConsumer,
                pose,
                sx1, sy1, startZ,
                ex1, ey1, endZ,
                ex0, ey1, endZ,
                sx0, sy1, startZ,
                color
        );

        // Left side
        addDoubleSidedQuad(
                vertexConsumer,
                pose,
                sx0, sy1, startZ,
                ex0, ey1, endZ,
                ex0, ey0, endZ,
                sx0, sy0, startZ,
                color
        );

        // Start cap
        addDoubleSidedQuad(
                vertexConsumer,
                pose,
                sx0, sy0, startZ,
                sx0, sy1, startZ,
                sx1, sy1, startZ,
                sx1, sy0, startZ,
                color
        );

        // End cap
        addDoubleSidedQuad(
                vertexConsumer,
                pose,
                ex0, ey0, endZ,
                ex1, ey0, endZ,
                ex1, ey1, endZ,
                ex0, ey1, endZ,
                color
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
