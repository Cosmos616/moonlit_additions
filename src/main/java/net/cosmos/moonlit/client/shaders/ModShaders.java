package net.cosmos.moonlit.client.shaders;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.cosmos.moonlit.Moonlit;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import team.lodestar.lodestone.systems.rendering.shader.ShaderHolder;
import team.lodestar.lodestone.systems.rendering.shader.ShaderRegister;

@EventBusSubscriber(
        modid = Moonlit.MOD_ID,
        value = Dist.CLIENT
)
public class ModShaders {
    private static final ShaderRegister SHADERS =
            new ShaderRegister(Moonlit.MOD_ID);

    public static final ShaderHolder METEOR_SPHERE =
            SHADERS.register(
                    "meteor_sphere",
                    DefaultVertexFormat.POSITION_TEX_COLOR
            );

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) {
        SHADERS.init(event);
    }
}
