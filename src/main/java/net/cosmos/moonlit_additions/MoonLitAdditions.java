package net.cosmos.moonlit_additions;

import net.cosmos.moonlit_additions.client.MoonlitModels;
import net.cosmos.moonlit_additions.common.block.ModBlocks;
import net.cosmos.moonlit_additions.common.block_entity.ModBlockEntities;
import net.cosmos.moonlit_additions.common.item.ModCreativeTabs;
import net.cosmos.moonlit_additions.common.item.ModItems;
import net.cosmos.moonlit_additions.client.particle.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(MoonLitAdditions.MOD_ID)
public class MoonLitAdditions {
    public static final String MOD_ID = "moonlit_additions";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MoonLitAdditions(IEventBus modEventBus, ModContainer modContainer) {
        ModCreativeTabs.register(modEventBus);

        ModBlocks.register(modEventBus);

        ModBlockEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModParticles.PARTICLES.register(modEventBus);

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
    }
}
