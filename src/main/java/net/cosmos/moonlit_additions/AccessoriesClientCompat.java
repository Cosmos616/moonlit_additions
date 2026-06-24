package net.cosmos.moonlit_additions;

import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import net.cosmos.moonlit_additions.init.ModItems;
import net.cosmos.moonlit_additions.client.rendering.BronzeMaskAccessoryRenderer;

public class AccessoriesClientCompat {
    public static void registerRenderers() {
        AccessoriesRendererRegistry.registerRenderer(
                ModItems.BRONZE_MASK_ALLOMANCER.get(),
                BronzeMaskAccessoryRenderer::new
        );
    }

}
