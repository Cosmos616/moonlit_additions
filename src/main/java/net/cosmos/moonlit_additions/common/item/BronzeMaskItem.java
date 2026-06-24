package net.cosmos.moonlit_additions.common.item;

import net.cosmos.moonlit_additions.client.rendering.BronzeMaskAllomancer;
import net.cosmos.moonlit_additions.client.rendering.BronzeMaskDarknessRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class BronzeMaskItem extends ArmorItem {
    private float friendMoonDarkneningOpacity = 0.0F;

    public BronzeMaskItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }


    public static boolean isWearingBronzeMask(LivingEntity entity) {
        return entity.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.BRONZE_MASK_ALLOMANCER.get());
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





    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity entity,
                    ItemStack stack,
                    EquipmentSlot slot,
                    HumanoidModel<?> original
            ) {
                BronzeMaskAllomancer<LivingEntity> model = new BronzeMaskAllomancer<>(
                        Minecraft.getInstance()
                                .getEntityModels()
                                .bakeLayer(BronzeMaskAllomancer.LAYER_LOCATION)
                );

                model.head.visible = slot == EquipmentSlot.HEAD;
                model.hat.visible = false;
                model.body.visible = false;
                model.rightArm.visible = false;
                model.leftArm.visible = false;
                model.rightLeg.visible = false;
                model.leftLeg.visible = false;

                return model;
            }
        });
    }
}
