package net.cosmos.moonlit.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;

public class ClientHelper {

    public static BakedModel getResourceModel(ResourceLocation id) {
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(id));
        return model != Minecraft.getInstance().getModelManager().getMissingModel() ? model : null;
    }

    public static float toRadians(float degrees) {
        return (float) (degrees / 180F * Math.PI);
    }

    public static Quaternionf rotateX(float degrees) {
        return new Quaternionf().rotateX(toRadians(degrees));
    }

    public static Quaternionf rotateY(float degrees) {
        return new Quaternionf().rotateY(toRadians(degrees));
    }

    public static Quaternionf rotateZ(float degrees) {
        return new Quaternionf().rotateZ(toRadians(degrees));
    }
}
