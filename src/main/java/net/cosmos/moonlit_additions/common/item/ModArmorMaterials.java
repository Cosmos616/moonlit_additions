package net.cosmos.moonlit_additions.common.item;

import net.cosmos.moonlit_additions.MoonlitAdditions;
import net.cosmos.moonlit_additions.init.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Map;

public class ModArmorMaterials {
    public static final Holder<ArmorMaterial> BRONZE_MASK_ALLOMANCER =
            Holder.direct(
                    new ArmorMaterial(
                            Map.of(
                                    ArmorItem.Type.HELMET, 2,
                                    ArmorItem.Type.CHESTPLATE, 0,
                                    ArmorItem.Type.LEGGINGS, 0,
                                    ArmorItem.Type.BOOTS, 0
                            ),
                            12,
                            SoundEvents.ARMOR_EQUIP_IRON,
                            () -> Ingredient.of(ModItems.MOONLIT_BRONZE.get()),
                            List.of(
                                    new ArmorMaterial.Layer(
                                            ResourceLocation.fromNamespaceAndPath(
                                                    MoonlitAdditions.MOD_ID,
                                                    "bronze_mask_allomancer"
                                            )
                                    )
                            ),
                            0.0F,
                            0.0F
                    )
            );
}
