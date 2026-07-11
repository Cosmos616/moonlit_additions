package net.cosmos.moonlit.client.shaders.processor;

import net.cosmos.moonlit.Moonlit;
import net.cosmos.moonlit.client.shaders.fx.SunGlowFx;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import team.lodestar.lodestone.systems.postprocess.MultiInstancePostProcessor;

public class SunShaderProcessor extends MultiInstancePostProcessor<SunGlowFx> {
    public static final SunShaderProcessor INSTANCE = new SunShaderProcessor();
    private EffectInstance effectGlow;

    @Override
    public ResourceLocation getPostChainLocation() {
        return Moonlit.moonlitPath("sun_glow");
    }

    @Override
    protected int getMaxInstances() {
        return 16; // maximum number of lights at once
    }

    @Override
    protected int getDataSizePerInstance() {
        return 8; // must match writeDataToBuffer() or it will crash
    }

    @Override
    public void init() {
        super.init();
        if (postChain != null) {
            effectGlow = effects[0];
        }
    }

    @Override
    public void afterProcess() {

    }

    @Override
    public void beforeProcess(Matrix4f viewModelStack) {
        super.beforeProcess(viewModelStack);
        setDataBufferUniform(effectGlow, "DataBuffer", "InstanceCount");
    }
}
