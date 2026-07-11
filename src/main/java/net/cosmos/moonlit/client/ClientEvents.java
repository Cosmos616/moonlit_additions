package net.cosmos.moonlit.client;

import net.cosmos.moonlit.Moonlit;
import net.cosmos.moonlit.client.shockwave.ShockwavePostProcessor;
import net.cosmos.moonlit.client.manfactured_sun.SunGlowPostProcessor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@EventBusSubscriber(
        modid = Moonlit.MOD_ID,
        value = Dist.CLIENT,
        bus = EventBusSubscriber.Bus.MOD
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
}