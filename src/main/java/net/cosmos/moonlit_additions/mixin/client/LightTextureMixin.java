package net.cosmos.moonlit_additions.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.cosmos.moonlit_additions.rendering.BronzeMaskDarknessRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightTexture.class)
public class LightTextureMixin {

    @ModifyArg(
            method = "updateLightTexture",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/joml/Vector3f;add(Lorg/joml/Vector3fc;)Lorg/joml/Vector3f;"
            ),
            index = 0
    )
    private Vector3fc moonlit_additions$darkenSkyLight(
            Vector3fc original,
            @Local(ordinal = 0) int skyLight,
            @Local(ordinal = 2) Vector3f skyLightColor
    ) {
        BronzeMaskDarknessRenderer.getInstance().modifySkyLightColor(skyLightColor, skyLight);
        return skyLightColor;
    }

    @Inject(
            method = "updateLightTexture",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/joml/Vector3f;set(FFF)Lorg/joml/Vector3f;",
                    shift = At.Shift.AFTER
            )
    )
    private void moonlit_additions$darkenBlockLight(
            float partialTicks,
            CallbackInfo ci,
            @Local(ordinal = 1) int blockLight,
            @Local(ordinal = 1) Vector3f blockLightColor
    ) {
        BronzeMaskDarknessRenderer.getInstance().modifyBlockLightColor(blockLightColor, blockLight);
    }

    @ModifyArg(
            method = "updateLightTexture",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/joml/Vector3f;lerp(Lorg/joml/Vector3fc;F)Lorg/joml/Vector3f;",
                    ordinal = 2
            ),
            index = 1
    )
    private float moonlit_additions$darkenAmbientLight(float ambientLight) {
        return BronzeMaskDarknessRenderer.getInstance().modifyAmbientLightFactor(ambientLight);
    }
}