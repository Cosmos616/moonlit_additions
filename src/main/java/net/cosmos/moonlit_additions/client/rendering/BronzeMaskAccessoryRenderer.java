package net.cosmos.moonlit_additions.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.cosmos.moonlit_additions.MoonlitAdditions;
import net.cosmos.moonlit_additions.client.MoonlitModelLayers;
import net.cosmos.moonlit_additions.client.MoonlitModels;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class BronzeMaskAccessoryRenderer implements AccessoryRenderer {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    MoonlitAdditions.MOD_ID,
                    "textures/armor/bronze_mask_allomancer.png"
            );

    private AllomancerMaskModel maskModel;

    public BronzeMaskAccessoryRenderer() {
    }

    private AllomancerMaskModel getMaskModel() {
        if (this.maskModel == null) {
            this.maskModel = new AllomancerMaskModel(
                    Minecraft.getInstance()
                            .getEntityModels()
                            .bakeLayer(MoonlitModelLayers.ALLOMANCER_MASK_LAYER)
            );
        }

        return this.maskModel;
    }

    @Override
    public <M extends LivingEntity> void render(
            ItemStack stack,
            SlotReference reference,
            PoseStack matrices,
            EntityModel<M> model,
            MultiBufferSource multiBufferSource,
            int light,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        if (!(model instanceof HumanoidModel<?> humanoidModel)) {
            return;
        }

        AllomancerMaskModel maskModel = this.getMaskModel();

        maskModel.head.visible = true;
        maskModel.hat.visible = false;
        maskModel.body.visible = false;
        maskModel.rightArm.visible = false;
        maskModel.leftArm.visible = false;
        maskModel.rightLeg.visible = false;
        maskModel.leftLeg.visible = false;

        // Copy the entity/player head rotation so the mask follows the head.
        maskModel.head.copyFrom(humanoidModel.head);

        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(
                RenderType.armorCutoutNoCull(TEXTURE)
        );

        matrices.pushPose();

        maskModel.head.render(
                matrices,
                vertexConsumer,
                light,
                OverlayTexture.NO_OVERLAY
        );
        matrices.popPose();
    }

    @Override
    public boolean shouldRender(boolean isRendering) {
        return true;
    }

    @Override
    public boolean shouldRenderInFirstPerson(
            HumanoidArm arm,
            ItemStack stack,
            SlotReference reference
    ) {
        return false;
    }
}
