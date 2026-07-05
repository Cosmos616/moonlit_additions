package net.cosmos.moonlit.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {
    @Invoker("renderShape")
    static void moonlit$renderShape(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            VoxelShape shape,
            double x,
            double y,
            double z,
            float red,
            float green,
            float blue,
            float alpha
    ) {
        throw new AssertionError();
    }
}