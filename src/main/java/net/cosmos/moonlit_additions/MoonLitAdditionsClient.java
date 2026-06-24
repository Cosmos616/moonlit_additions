package net.cosmos.moonlit_additions;

import net.cosmos.moonlit_additions.common.block_entity.ModBlockEntities;
import net.cosmos.moonlit_additions.client.particle.MeteorAuroraParticle;
import net.cosmos.moonlit_additions.client.particle.ModParticles;
import net.cosmos.moonlit_additions.client.rendering.BronzeBellRenderer;
import net.cosmos.moonlit_additions.client.rendering.BronzeMaskAllomancer;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = MoonLitAdditions.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MoonLitAdditions.MOD_ID, value = Dist.CLIENT)
public class MoonLitAdditionsClient {

    public MoonLitAdditionsClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        MoonLitAdditions.LOGGER.info("HELLO FROM CLIENT SETUP");
        MoonLitAdditions.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(
                BronzeMaskAllomancer.LAYER_LOCATION,
                BronzeMaskAllomancer::createBodyLayer
        );
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.BRONZE_BELL_BLOCK_ENTITY.get(), BronzeBellRenderer::new);
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
        // Your vanilla helmet armor renderer registration here.

        if (ModList.get().isLoaded("accessories")) {
            AccessoriesClientCompat.registerRenderers();
        }
    }
}
