package net.cosmos.moonlit_additions.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.cosmos.moonlit_additions.MoonlitAdditions;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import team.lodestar.lodestone.systems.rendering.shader.ShaderHolder;
import team.lodestar.lodestone.systems.rendering.shader.ShaderRegister;

@EventBusSubscriber(
        modid = MoonlitAdditions.MOD_ID,
        value = Dist.CLIENT,
        bus = EventBusSubscriber.Bus.MOD
)
public class ModShaders {
    private static final ShaderRegister SHADERS =
            new ShaderRegister(MoonlitAdditions.MOD_ID);

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