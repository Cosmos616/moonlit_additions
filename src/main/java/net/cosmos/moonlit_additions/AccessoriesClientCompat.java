package net.cosmos.moonlit_additions;

import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.EquipmentChecking;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.pond.CosmeticArmorLookupTogglable;
import net.cosmos.moonlit_additions.init.ModItems;
import net.cosmos.moonlit_additions.client.rendering.BronzeMaskAccessoryRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class AccessoriesClientCompat {
    public static void registerRenderers() {
        AccessoriesRendererRegistry.registerRenderer(
                ModItems.BRONZE_MASK_ALLOMANCER.get(),
                BronzeMaskAccessoryRenderer::new
        );
    }

    public static boolean isWearingBronzeMask(LivingEntity entity) {
        var cap = AccessoriesCapability.get(entity);
        if (cap != null) return cap.isEquipped(ModItems.BRONZE_MASK_ALLOMANCER.get()) || cap.isEquipped(ModItems.BRONZE_MASK_ALLOMANCER.get(), EquipmentChecking.COSMETICALLY_OVERRIDABLE);
        return false;
    }
}
