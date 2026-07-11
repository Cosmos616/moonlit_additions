package net.cosmos.moonlit;

import net.cosmos.moonlit.client.MoonlitArmorModels;
import net.cosmos.moonlit.client.MoonlitClientExtensions;
import net.cosmos.moonlit.client.MoonlitModelLayers;
import net.cosmos.moonlit.client.particle.MeteorAuroraParticle;
import net.cosmos.moonlit.client.particle.ModParticles;
import net.cosmos.moonlit.client.rendering.*;
import net.cosmos.moonlit.client.shaders.processor.SunShaderProcessor;
import net.cosmos.moonlit.client.shockwave.ShockwavePostProcessor;
import net.cosmos.moonlit.init.ModBlockEntities;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import team.lodestar.lodestone.systems.postprocess.PostProcessHandler;

@Mod(value = Moonlit.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Moonlit.MOD_ID, value = Dist.CLIENT)
public class MoonlitClient {

    public MoonlitClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        Moonlit.LOGGER.info("HELLO FROM CLIENT SETUP");
        Moonlit.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

        event.enqueueWork(ShockwavePostProcessor::load);
        PostProcessHandler.addInstance(SunShaderProcessor.INSTANCE);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        LensClientSelection.clientTick();
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        MoonlitModelLayers.registerLayers(event);
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        MoonlitArmorModels.addLayers(event);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.BRONZE_BELL_BLOCK_ENTITY.get(), BronzeBellRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.BRONZE_LENS.get(), BronzeLensRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.BRONZE_MIRROR.get(), BronzeMirrorRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MANUFACTURED_SUN.get(), ManufacturedSunRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(
                ModParticles.METEOR_AURORA.get(),
                MeteorAuroraParticle.Provider::new
        );
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        MoonlitClientExtensions.registerClientExtensions(event);
    }
}
