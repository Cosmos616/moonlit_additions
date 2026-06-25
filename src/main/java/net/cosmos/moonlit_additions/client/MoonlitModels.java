package net.cosmos.moonlit_additions.client;

import net.cosmos.moonlit_additions.MoonlitAdditions;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.cosmos.moonlit_additions.MoonlitAdditions.moonlitPath;

public class MoonlitModels {
    private static final ResourceLocation bronzeBellId = moonlitPath("block/bronze_bell/body");

    private static final ResourceLocation allomancerSigilId = moonlitPath("special/sigil/allomancer");

    public static final MoonlitModels INSTANCE = new MoonlitModels();

    private final Map<ResourceLocation, Function<BakedModel, BakedModel>> afterBakeModifiers;
    private final Map<ResourceLocation, Consumer<BakedModel>> modelConsumers;

    public boolean registeredModels = false;

    @UnknownNullability
    public BakedModel
            bronzeBell,
            allomancerSigil
            ;

    public void onModelRegister(ResourceManager rm, Consumer<ResourceLocation> consumer) {
        modelConsumers.keySet().forEach(consumer);

        if (!registeredModels) {
            registeredModels = true;
        }
    }

    public void onModelBake(ModelBakery loader, Map<ModelResourceLocation, BakedModel> map) {
        if (!registeredModels) {
            MoonlitAdditions.LOGGER.error("Additional models failed to register! Aborting baking models to avoid early crashing.");
            return;
        }
        AtomicInteger baked = new AtomicInteger();
        afterBakeModifiers.forEach((resourceLocation, afterBakeModifier) -> map.computeIfPresent(new ModelResourceLocation(resourceLocation, ""), (resourceLoc, bakedModel) -> afterBakeModifier.apply(bakedModel)));
        modelConsumers.forEach((resourceLocation, bakedModelConsumer) -> {
            bakedModelConsumer.accept(map.get(new ModelResourceLocation(resourceLocation, "standalone")));
            MoonlitAdditions.LOGGER.error("Baking model: {}", resourceLocation);
            baked.getAndIncrement();
        });
        MoonlitAdditions.LOGGER.error("Baked {} additional model(s).", baked.get());
    }

    private ResourceLocation stripBlockPrefix(ResourceLocation id) {
        String path = id.getPath();
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), path.startsWith("block/") ? path.substring(6) : path);
    }

    private MoonlitModels() {
        afterBakeModifiers = new HashMap<>();

        modelConsumers = new HashMap<>();

        modelConsumers.put(bronzeBellId, bakedModel -> this.bronzeBell = bakedModel);
        modelConsumers.put(allomancerSigilId, bakedModel -> this.allomancerSigil = bakedModel);
    }

    private static BakedModel[] getBakedModels(Map<ResourceLocation, Consumer<BakedModel>> consumers, ResourceLocation[] ids) {
        final BakedModel[] bakedModels = new BakedModel[ids.length];
        for (int i = 0; i < ids.length; i++) {
            int index = i;
            consumers.put(ids[index], bakedModel -> bakedModels[index] = bakedModel);
        }
        return bakedModels;
    }
}
