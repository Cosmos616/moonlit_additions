package net.cosmos.moonlit_additions.rendering;

import com.farcr.nomansland.common.block.moonlight.MoonlightCandleBlock;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public class BronzeMaskDarknessRenderer {
    private static final BronzeMaskDarknessRenderer INSTANCE = new BronzeMaskDarknessRenderer();

    private float darkeningOpacity = 0.0F;

    public static BronzeMaskDarknessRenderer getInstance() {
        return INSTANCE;
    }

    public void tick(boolean wearingMask) {
        float target = wearingMask ? 1.5F : 0.0F;
        float fadeSpeed = 1.0F / 25.0F;

        this.darkeningOpacity = Mth.lerp(fadeSpeed, this.darkeningOpacity, target);
    }

    public float modifyAmbientLightFactor(float ambientLight) {
        float darkeningAmount = 1 - this.darkeningOpacity;
        return ambientLight * darkeningAmount;
    }
    public void modifySkyLightColor(Vector3f color, int skyLightLevel) {
        color.mul(1 - this.darkeningOpacity);
    }
    public void modifyBlockLightColor(Vector3f color, int blockLightLevel) {
        if (this.darkeningOpacity <= 0) return;
        if (blockLightLevel < MoonlightCandleBlock.LIGHT_LEVEL) {
            float factor = Mth.map(blockLightLevel, 0, MoonlightCandleBlock.LIGHT_LEVEL, 0, 1);
            factor = (float) Math.pow(factor, Mth.lerp(this.darkeningOpacity, 1, 5));
            color.mul(factor);
        }
    }
}