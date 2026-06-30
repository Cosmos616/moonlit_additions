package net.cosmos.moonlit;

import net.cosmos.moonlit.client.MoonlitModels;
import net.cosmos.moonlit.common.entity.ModEntities;
import net.cosmos.moonlit.common.entity.ShockwaveEntityRenderer;
import net.cosmos.moonlit.init.ModCreativeTabs;
import net.cosmos.moonlit.client.particle.ModParticles;
import net.cosmos.moonlit.init.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Moonlit.MOD_ID)
public class Moonlit {
    public static final String MOD_ID = "moonlit";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Moonlit(IEventBus modEventBus, ModContainer modContainer) {
        ModCreativeTabs.register(modEventBus);

        ModBlocks.register(modEventBus);

        ModBlockEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModParticles.PARTICLES.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModAttachmentTypes.register(modEventBus);
        ModDreamTypes.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static ResourceLocation moonlitPath(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientOnly {
        @SubscribeEvent
        public static void onModelRegister(ModelEvent.RegisterAdditional evt) {
            var resourceManager = Minecraft.getInstance().getResourceManager();
            MoonlitModels.INSTANCE.onModelRegister(resourceManager,
                    id -> evt.register(ModelResourceLocation.standalone(id)));
        }

        @SubscribeEvent
        public static void onModelBake(ModelEvent.ModifyBakingResult evt) {
            MoonlitModels.INSTANCE.onModelBake(evt.getModelBakery(), evt.getModels());
        }
        @SubscribeEvent
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(
                    ModEntities.SHOCKWAVE_PROJECTOR.get(),
                    ShockwaveEntityRenderer::new
            );
        }
    }
}
