package net.cosmos.moonlit_additions.client;

import net.cosmos.moonlit_additions.MoonlitAdditions;
import net.cosmos.moonlit_additions.client.shockwave.ShockwaveClientData;
import net.cosmos.moonlit_additions.client.shockwave.ShockwavePostProcessor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(
        modid = MoonlitAdditions.MOD_ID,
        value = Dist.CLIENT
)
public class ClientRenderEvents {

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            return;
        }

        ShockwavePostProcessor.render(event);
        ShockwaveClientData.clear();
    }
}