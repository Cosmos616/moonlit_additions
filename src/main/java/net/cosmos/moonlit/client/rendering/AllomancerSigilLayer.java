package net.cosmos.moonlit.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.cosmos.moonlit.AccessoriesClientCompat;
import net.cosmos.moonlit.client.ClientHelper;
import net.cosmos.moonlit.client.MoonlitModels;
import net.cosmos.moonlit.common.item.BronzeMaskItem;
import net.cosmos.moonlit.mixin.EntityRenderDispatcherAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

@SuppressWarnings("deprecation")
public class AllomancerSigilLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    public AllomancerSigilLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffers, int light, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        M entityModel = getParentModel();
        if (!(entityModel instanceof HumanoidModel<?> model))
            return;
        if (BronzeMaskItem.isWearingBronzeMask(entity) || AccessoriesClientCompat.isWearingBronzeMask(entity, true)) {
            boolean hasGlint = entity.getItemBySlot(EquipmentSlot.HEAD).hasFoil();
            VertexConsumer vc = ItemRenderer.getFoilBuffer(buffers, Sheets.cutoutBlockSheet(), false, hasGlint);
            poseStack.pushPose();
            model.head.translateAndRotate(poseStack);
            poseStack.translate(-0.95, 0.71, 0.25);
            poseStack.scale(2, 2, 1);
            poseStack.mulPose(ClientHelper.rotateX(180));
            BakedModel sigil = MoonlitModels.INSTANCE.allomancerSigil;
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), vc, null, sigil, 1, 1, 1, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }

    public static void registerOnAll(EntityRenderDispatcher renderManager) {
        for (EntityRenderer<? extends Player> renderer : renderManager.getSkinMap().values())
            registerOn(renderer);
        for (EntityRenderer<?> renderer : ((EntityRenderDispatcherAccessor) renderManager).create$getRenderers().values())
            registerOn(renderer);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void registerOn(EntityRenderer<?> entityRenderer) {
        if (!(entityRenderer instanceof LivingEntityRenderer<?, ?> livingRenderer))
            return;
        if (!(livingRenderer.getModel() instanceof HumanoidModel))
            return;
        AllomancerSigilLayer<?, ?> layer = new AllomancerSigilLayer<>(livingRenderer);
        livingRenderer.addLayer((AllomancerSigilLayer) layer);
    }
}
