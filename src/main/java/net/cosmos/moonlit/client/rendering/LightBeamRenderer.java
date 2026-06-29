package net.cosmos.moonlit.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.cosmos.moonlit.client.ClientHelper;
import net.cosmos.moonlit.common.block_entity.forge.light.BeamHelpers;
import net.cosmos.moonlit.common.block_entity.forge.light.BronzeLensBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class LightBeamRenderer {

    public LightBeamRenderer(BlockEntityRendererProvider.Context context) {

    }

    public void render(BronzeLensBlockEntity blockEntityIn, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        double length = blockEntityIn.beamRange;
        //Quaternionf transform = ClientHelper.rotateY((float) blockEntityIn.rotation.y);
        //transform.mul(ClientHelper.rotateX((float) blockEntityIn.rotation.x));
        //transform.mul(ClientHelper.rotateZ((float) blockEntityIn.rotation.z));
        poseStack.pushPose();
        //poseStack.rotateAround(transform, 0.5f, 0.5f, 0.5f);
        float yaw = (float) Math.atan2(-blockEntityIn.rotation.x, blockEntityIn.rotation.z);
        float pitch = (float) Math.atan2(
                -blockEntityIn.rotation.y,
                Math.sqrt(blockEntityIn.rotation.x * blockEntityIn.rotation.x + blockEntityIn.rotation.z * blockEntityIn.rotation.z)
        );
        poseStack.rotateAround(Axis.YN.rotation(yaw), 0.5f, 0.5f, 0.5f);
        poseStack.rotateAround(Axis.XP.rotation(pitch), 0.5f, 0.5f, 0.5f);
        poseStack.translate(0.5, 0.5, 0.5);
        VertexConsumer builder = bufferSource.getBuffer(RenderType.lightning());
        Matrix4f matrix4f = poseStack.last().pose();
        int color = 0x40FFFFFF;
        float alpha = FastColor.ARGB32.alpha(color) / 255f;
        float red = FastColor.ARGB32.red(color) / 255f;
        float green = FastColor.ARGB32.green(color) / 255f;
        float blue = FastColor.ARGB32.blue(color) / 255f;
        int layers = 1;
        float startWidthRadius = (float) (1.0 / 4);
        float startHeightRadius = (float) (1.0 / 4);
        float endWidthRadius = (float) (1.0 / 4);
        float endHeightRadius = (float) (1.0 / 4);
        for (int i = 1; i <= layers; i++) {
            float sWidthRadius = startWidthRadius * i / layers;
            float sHeightRadius = startHeightRadius * i / layers;
            float eWidthRadius = endWidthRadius * i / layers;
            float eHeightRadius = endHeightRadius * i / layers;

            builder.addVertex(matrix4f, -sWidthRadius, -sHeightRadius, 0).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -eWidthRadius, -eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, 0 + eWidthRadius, -eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, sWidthRadius, -sHeightRadius, 0).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, sWidthRadius, -sHeightRadius, 0).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, 0 + eWidthRadius, -eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -eWidthRadius, -eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -sWidthRadius, -sHeightRadius, 0).setColor(red, green, blue, alpha);

            builder.addVertex(matrix4f, sWidthRadius, -sHeightRadius, 0).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, 0 + eWidthRadius, -eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, 0 + eWidthRadius, 0 + eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, sWidthRadius, sHeightRadius, 0).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, sWidthRadius, sHeightRadius, 0).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, 0 + eWidthRadius, 0 + eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, 0 + eWidthRadius, -eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, sWidthRadius, -sHeightRadius, 0).setColor(red, green, blue, alpha);

            builder.addVertex(matrix4f, sWidthRadius, sHeightRadius, 0).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, 0 + eWidthRadius, 0 + eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -eWidthRadius, 0 + eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -sWidthRadius, sHeightRadius, 0).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -sWidthRadius, sHeightRadius, 0).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -eWidthRadius, 0 + eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, 0 + eWidthRadius, 0 + eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, sWidthRadius, sHeightRadius, 0).setColor(red, green, blue, alpha);

            builder.addVertex(matrix4f, -sWidthRadius, sHeightRadius, 0).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -eWidthRadius, 0 + eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -eWidthRadius, -eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -sWidthRadius, -sHeightRadius, 0).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -sWidthRadius, -sHeightRadius, 0).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -eWidthRadius, -eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -eWidthRadius, 0 + eHeightRadius, (float) -length).setColor(red, green, blue, alpha);
            builder.addVertex(matrix4f, -sWidthRadius, sHeightRadius, 0).setColor(red, green, blue, alpha);
        }
        builder.addVertex(matrix4f, -startWidthRadius, -startHeightRadius, 0).setColor(red, green, blue, alpha);
        builder.addVertex(matrix4f, -startWidthRadius, startHeightRadius, 0).setColor(red, green, blue, alpha);
        builder.addVertex(matrix4f, startWidthRadius, startHeightRadius, 0).setColor(red, green, blue, alpha);
        builder.addVertex(matrix4f, startWidthRadius, -startHeightRadius, 0).setColor(red, green, blue, alpha);

        builder.addVertex(matrix4f, startWidthRadius, -startHeightRadius, 0).setColor(red, green, blue, alpha);
        builder.addVertex(matrix4f, startWidthRadius, startHeightRadius, 0).setColor(red, green, blue, alpha);
        builder.addVertex(matrix4f, -startWidthRadius, startHeightRadius, 0).setColor(red, green, blue, alpha);
        builder.addVertex(matrix4f, -startWidthRadius, -startHeightRadius, 0).setColor(red, green, blue, alpha);

        builder.addVertex(matrix4f, -endWidthRadius, -endHeightRadius, (float) -length).setColor(red, green, blue, alpha);
        builder.addVertex(matrix4f, -endWidthRadius, endHeightRadius, (float) -length).setColor(red, green, blue, alpha);
        builder.addVertex(matrix4f, endWidthRadius, endHeightRadius, (float) -length).setColor(red, green, blue, alpha);
        builder.addVertex(matrix4f, endWidthRadius, -endHeightRadius, (float) -length).setColor(red, green, blue, alpha);

        builder.addVertex(matrix4f, endWidthRadius, -endHeightRadius, (float) -length).setColor(red, green, blue, alpha);
        builder.addVertex(matrix4f, endWidthRadius, endHeightRadius, (float) -length).setColor(red, green, blue, alpha);
        builder.addVertex(matrix4f, -endWidthRadius, endHeightRadius, (float) -length).setColor(red, green, blue, alpha);
        builder.addVertex(matrix4f, -endWidthRadius, -endHeightRadius, (float) -length).setColor(red, green, blue, alpha);

        poseStack.popPose();
    }
}
