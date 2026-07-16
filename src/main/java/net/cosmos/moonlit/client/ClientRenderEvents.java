package net.cosmos.moonlit.client;

import net.cosmos.moonlit.Moonlit;
import net.cosmos.moonlit.client.reflection.ReflectionCausticsPostProcessor;
import net.cosmos.moonlit.client.shockwave.ShockwaveClientData;
import net.cosmos.moonlit.client.shockwave.ShockwavePostProcessor;
import net.cosmos.moonlit.client.manfactured_sun.SunGlowPostProcessor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(
        modid = Moonlit.MOD_ID,
        value = Dist.CLIENT
)
public class ClientRenderEvents {

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            return;
        }

        ReflectionCausticsPostProcessor.render(event);
        ShockwavePostProcessor.render(event);
        ShockwaveClientData.clear();

        SunGlowPostProcessor.render(event);
    }
}