package net.cosmos.moonlit_additions.client;

import com.farcr.nomansland.NoMansLand;
import net.cosmos.moonlit_additions.client.rendering.AllomancerMaskModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class MoonlitModelLayers {
    public static final ModelLayerLocation ALLOMANCER_MASK_LAYER = new ModelLayerLocation(NoMansLand.location("bronze_mask_allomancer"), "main");

    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ALLOMANCER_MASK_LAYER, AllomancerMaskModel::createBodyLayer);
    }
}
