package net.cosmos.moonlit.common.entity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cosmos.moonlit.Moonlit;
import net.cosmos.moonlit.client.shockwave.ShockwaveClientData;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ShockwaveEntityRenderer extends EntityRenderer<ShockwaveProjectorEntity> {

    public ShockwaveEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(
            ShockwaveProjectorEntity entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        ShockwaveClientData.active = true;

        ShockwaveClientData.center.set(
                (float) Mth.lerp(partialTick, entity.xOld, entity.getX()),
                (float) Mth.lerp(partialTick, entity.yOld, entity.getY()),
                (float) Mth.lerp(partialTick, entity.zOld, entity.getZ())
        );

        ShockwaveClientData.radius = entity.getRadius(partialTick);
        ShockwaveClientData.alpha = entity.getAlpha(partialTick);

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ShockwaveProjectorEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(
                Moonlit.MOD_ID,
                "textures/misc/blank.png"
        );
    }
}
