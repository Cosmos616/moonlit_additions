package net.cosmos.moonlit_additions.client;

import net.cosmos.moonlit_additions.client.rendering.AllomancerSigilLayer;
import net.cosmos.moonlit_additions.client.rendering.AllomancerMaskModel;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class MoonlitArmorModels {
    public static AllomancerMaskModel ALLOMANCER_MASK;

    public MoonlitArmorModels() {
    }

    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        AllomancerSigilLayer.registerOnAll(event.getContext().getEntityRenderDispatcher());
        ALLOMANCER_MASK = new AllomancerMaskModel(event.getEntityModels().bakeLayer(MoonlitModelLayers.ALLOMANCER_MASK_LAYER));
    }
}
