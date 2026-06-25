package net.cosmos.moonlit_additions.common.item;

import net.cosmos.moonlit_additions.client.rendering.BronzeMaskDarknessRenderer;
import net.cosmos.moonlit_additions.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.cosmos.moonlit_additions.MoonlitAdditions.moonlitPath;

public class BronzeMaskItem extends ArmorItem {
    private float friendMoonDarkneningOpacity = 0.0F;

    public BronzeMaskItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }


    public static boolean isWearingBronzeMask(LivingEntity entity) {
        return entity.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.BRONZE_MASK_ALLOMANCER.get());
    }

    public @Nullable ResourceLocation getArmorTexture(@NotNull ItemStack stack, @NotNull Entity entity, @NotNull EquipmentSlot slot, ArmorMaterial.@NotNull Layer layer, boolean innerModel) {
        return moonlitPath("textures/armor/bronze_mask_allomancer.png");
    }

    @Override
    public void inventoryTick(
            ItemStack stack,
            Level level,
            Entity entity,
            int slotId,
            boolean isSelected
    ) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (!level.isClientSide) {
            return;
        }

        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        boolean isInHelmetSlot = livingEntity.getItemBySlot(EquipmentSlot.HEAD) == stack;

        if (entity == Minecraft.getInstance().player) {
            BronzeMaskDarknessRenderer.getInstance().tick(isInHelmetSlot);
        }
    }
}
