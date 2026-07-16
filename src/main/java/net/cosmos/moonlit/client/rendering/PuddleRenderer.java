package net.cosmos.moonlit.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.cosmos.moonlit.client.ClientHelper;
import net.cosmos.moonlit.common.block_entity.dream.reflection.PuddleBlockEntity;
import net.cosmos.moonlit.init.ModAttachmentTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix4f;

import java.awt.*;

public class PuddleRenderer implements BlockEntityRenderer<PuddleBlockEntity> {

    public PuddleRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(PuddleBlockEntity puddleBlockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        float min = 0f;
        float max = 1f;
        Player player = Minecraft.getInstance().player;
        Color color = Color.BLACK;
        if (player != null) {
            color = player.hasData(ModAttachmentTypes.REFLECTION) ? Color.RED : Color.BLUE;
        }
        VertexConsumer vertexConsumer = bufferIn.getBuffer(ClientHelper.LIGHT_BEAM);
        Matrix4f pose = poseStack.last().pose();
        poseStack.pushPose();
        addDoubleSidedQuad(vertexConsumer, pose, min, max, max, max, max, max, max, max, min, min, max, min, color);
        poseStack.popPose();
    }

    private static void addDoubleSidedQuad(VertexConsumer vertexConsumer, Matrix4f pose, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, Color color) {
        addQuad(vertexConsumer, pose, x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, color);
        addQuad(vertexConsumer, pose, x4, y4, z4, x3, y3, z3, x2, y2, z2, x1, y1, z1, color);
    }

    private static void addQuad(VertexConsumer vertexConsumer, Matrix4f pose, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, Color color) {
        addVertex(vertexConsumer, pose, x1, y1, z1, color);
        addVertex(vertexConsumer, pose, x2, y2, z2, color);
        addVertex(vertexConsumer, pose, x3, y3, z3, color);
        addVertex(vertexConsumer, pose, x4, y4, z4, color);
    }

    private static void addVertex(VertexConsumer vertexConsumer, Matrix4f pose, float x, float y, float z, Color color) {
        vertexConsumer.addVertex(pose, x, y, z).setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }
}
