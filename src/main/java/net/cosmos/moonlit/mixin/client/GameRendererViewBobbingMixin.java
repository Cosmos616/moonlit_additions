package net.cosmos.moonlit.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.cosmos.moonlit.client.reflection.ViewBobbingCapture;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererViewBobbingMixin {

    @Inject(
            method = "bobView",
            at = @At("HEAD")
    )
    private void moonlit$captureBeforeViewBob(
            PoseStack poseStack,
            float partialTick,
            CallbackInfo ci
    ) {
        ViewBobbingCapture.captureBefore(
                new org.joml.Matrix4f(
                        poseStack.last().pose()
                )
        );
    }

    @Inject(
            method = "bobView",
            at = @At("RETURN")
    )
    private void moonlit$captureAfterViewBob(
            PoseStack poseStack,
            float partialTick,
            CallbackInfo ci
    ) {
        ViewBobbingCapture.captureAfter(
                new org.joml.Matrix4f(
                        poseStack.last().pose()
                )
        );
    }
}