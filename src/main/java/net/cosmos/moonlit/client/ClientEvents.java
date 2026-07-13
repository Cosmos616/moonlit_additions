package net.cosmos.moonlit.client;

import net.cosmos.moonlit.Moonlit;
import net.cosmos.moonlit.client.shockwave.ShockwavePostProcessor;
import net.cosmos.moonlit.client.manfactured_sun.SunGlowPostProcessor;
import net.cosmos.moonlit.init.ModAttachmentTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@EventBusSubscriber(
        modid = Moonlit.MOD_ID,
        value = Dist.CLIENT
)
public class ClientEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ShockwavePostProcessor.load();
            SunGlowPostProcessor.load();
        });
    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((barrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor) -> {
            return barrier.wait(null).thenRunAsync(() -> {
                ShockwavePostProcessor.load();
                SunGlowPostProcessor.load();
            }, gameExecutor);
        });
    }

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Pre event) {
        Player player = Minecraft.getInstance().player;
        GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
        if (player != null) {
            gameRenderer.shutdownEffect();
            if (player.hasData(ModAttachmentTypes.REFLECTION)) {
                gameRenderer.loadEffect(Moonlit.moonlitPath("shaders/post/reflection.json"));
            }
        }
    }
}
