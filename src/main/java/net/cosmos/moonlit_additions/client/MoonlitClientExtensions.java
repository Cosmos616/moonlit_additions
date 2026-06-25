package net.cosmos.moonlit_additions.client;

import com.farcr.nomansland.client.extensions.LodestoneArmorClientItemExtensions;
import net.cosmos.moonlit_additions.AccessoriesClientCompat;
import net.cosmos.moonlit_additions.init.ModItems;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public class MoonlitClientExtensions {

    public MoonlitClientExtensions() {
    }

    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        if (ModList.get().isLoaded("accessories")) {
            AccessoriesClientCompat.registerRenderers();
        }
        event.registerItem(new LodestoneArmorClientItemExtensions(() -> MoonlitArmorModels.ALLOMANCER_MASK), ModItems.BRONZE_MASK_ALLOMANCER.get());
    }
}
