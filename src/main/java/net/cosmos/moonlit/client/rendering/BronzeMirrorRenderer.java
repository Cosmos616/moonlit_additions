package net.cosmos.moonlit.client.rendering;

import net.cosmos.moonlit.client.MoonlitModels;
import net.cosmos.moonlit.common.block_entity.forge.light.BronzeMirrorBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;

public class BronzeMirrorRenderer extends AbstractLensRenderer<BronzeMirrorBlockEntity> {

    public BronzeMirrorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public BakedModel lensModel() {
        return MoonlitModels.INSTANCE.bronzeMirror;
    }

    @Override
    public BakedModel middleModel() {
        return MoonlitModels.INSTANCE.bronzeMirrorMiddle;
    }
}
