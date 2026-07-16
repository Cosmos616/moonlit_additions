package net.cosmos.moonlit.client.rendering.debug;

import com.farcr.nomansland.common.block.CarvingFormation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.cosmos.moonlit.common.block.debug.GlowingAncestralCarving;
import net.cosmos.moonlit.common.block_entity.debug.GlowingAncestralCarvingBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Vector3f;
import org.joml.Vector4f;
import team.lodestar.lodestone.modules.core.easing.Easing;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypes;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;
import team.lodestar.lodestone.systems.rendering.cube.CubeVertexData;
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeToken;

import java.awt.*;

import static net.cosmos.moonlit.Moonlit.moonlitPath;

public class GlowingAncestralCarvingRenderer implements BlockEntityRenderer<GlowingAncestralCarvingBlockEntity> {

    public GlowingAncestralCarvingRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(
            GlowingAncestralCarvingBlockEntity blockEntityIn,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferIn,
            int combinedLightIn,
            int combinedOverlayIn
    ) {
        var blockState = blockEntityIn.getBlockState();

        Direction direction = blockState.getValue(BlockStateProperties.FACING);
        int rotation = blockState.getValue(GlowingAncestralCarving.ROTATION);
        CarvingFormation formation =
                blockState.getValue(GlowingAncestralCarving.FORMATION);

        var texture = moonlitPath(
                "textures/vfx/glowing_carving/"
                        + formation.getSerializedName()
                        + ".png"
        );

        var renderType = LodestoneRenderTypes.ADDITIVE_TEXTURE.apply(
                RenderTypeToken.createToken(texture)
        );

        var level = Minecraft.getInstance().level;

        float delta = blockEntityIn.getGlowDelta();
        float alpha = delta * 0.7f;
        float ease = Easing.SINE_OUT.ease(delta);
        float offsetDistance = 0.2f - ease * 0.2f;
        float wobbleStrength = 0.1f - ease * 0.075f;

        Vector3f[] vertices = new Vector3f[]{
                new Vector3f(-0.5125f, -0.5125f, 0.5125f),
                new Vector3f( 0.5125f, -0.5125f, 0.5125f),
                new Vector3f( 0.5125f,  0.5125f, 0.5125f),
                new Vector3f(-0.5125f,  0.5125f, 0.5125f)
        };

        poseStack.pushPose();

        poseStack.translate(0.5f, 0.5f, 0.5f);
        applyCarvingRotation(poseStack, direction, rotation);

        float gameTime = level.getGameTime() + partialTicks;
        int time = 160;

        for (int i = 0; i < 4; i++) {
            var color = i <= 2
                    ? new Color(1.0F, 0.75F, 0.28F)
                    : new Color(255, 246, 141);

            double offset = 0;

            if (offsetDistance > 0) {
                double angle = i / 4f * (Math.PI * 2);
                angle += ((gameTime % time) / time) * (Math.PI * 2);

                offset = offsetDistance * Math.cos(angle);

                if (i % 2 == 0) {
                    offset *= -1;
                }
            }

            poseStack.pushPose();
            poseStack.translate(offset, 0, 0);

            CubeVertexData.applyVertexWobble(
                    vertices,
                    0,
                    wobbleStrength
            );

            new VFXBuilders.WorldVFXBuilder()
                    .setColor(color, alpha)
                    .setRenderType(renderType)
                    .renderQuad(poseStack, vertices, 1f);

            poseStack.popPose();

            alpha *= 1 - (delta + 0.2f) * 0.5f;
        }

        poseStack.popPose();
    }

    private static void applyCarvingRotation(
            PoseStack poseStack,
            Direction direction,
            int rotation
    ) {
        float rotationDegrees = rotation * 90.0f;

        if (direction.getAxis().isHorizontal()) {
            poseStack.mulPose(Axis.YN.rotationDegrees(direction.toYRot()));
            return;
        }

        if (direction == Direction.UP) {
            poseStack.mulPose(Axis.XN.rotationDegrees(90.0f));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-rotationDegrees + 180f));
        } else {
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0f));
            poseStack.mulPose(Axis.ZN.rotationDegrees(-rotationDegrees));
        }
    }


}
