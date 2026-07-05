package net.cosmos.moonlit.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.cosmos.moonlit.common.block_entity.forge.light.BeamHelpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;

import static net.minecraft.client.renderer.RenderStateShard.*;

public class ClientHelper {

    public static BakedModel getResourceModel(ResourceLocation id) {
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(id));
        return model != Minecraft.getInstance().getModelManager().getMissingModel() ? model : null;
    }

    public static Quaternionf rotateX(float degrees) {
        return new Quaternionf().rotateX(BeamHelpers.toRadians(degrees));
    }

    public static Quaternionf rotateY(float degrees) {
        return new Quaternionf().rotateY(BeamHelpers.toRadians(degrees));
    }

    public static Quaternionf rotateZ(float degrees) {
        return new Quaternionf().rotateZ(BeamHelpers.toRadians(degrees));
    }

    public static final RenderType LIGHTNING_CULL = RenderType.create("lightning_cull", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 1536, true, true, RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
            .setWriteMaskState(COLOR_WRITE)
            .setTransparencyState(LIGHTNING_TRANSPARENCY)
            .setOutputState(WEATHER_TARGET)
            .createCompositeState(true));
}
