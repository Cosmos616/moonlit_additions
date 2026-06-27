package net.cosmos.moonlit.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.cosmos.moonlit.client.MoonlitModels;
import net.cosmos.moonlit.common.block_entity.BronzeBellBlockEntity;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

public class BronzeBellRenderer implements BlockEntityRenderer<BronzeBellBlockEntity> {
    private final BlockRenderDispatcher blockRenderDispatcher;

    public BronzeBellRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderDispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(BronzeBellBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BakedModel body = MoonlitModels.INSTANCE.bronzeBell;
        VertexConsumer buffer = bufferSource.getBuffer(ItemBlockRenderTypes.getRenderType(blockEntity.getBlockState(), false));

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

        blockRenderDispatcher.getModelRenderer()
                .renderModel(poseStack.last(), buffer, blockEntity.getBlockState(),
                        body, 1, 1, 1, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
