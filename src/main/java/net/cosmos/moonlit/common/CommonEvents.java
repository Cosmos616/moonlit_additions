package net.cosmos.moonlit.common;

import com.farcr.nomansland.common.registry.NMLRegistries;
import net.cosmos.moonlit.AccessoriesClientCompat;
import net.cosmos.moonlit.Moonlit;
import net.cosmos.moonlit.common.commands.MeteorCommand;
import net.cosmos.moonlit.common.item.BronzeMaskItem;
import net.cosmos.moonlit.init.*;
import net.cosmos.moonlit.network.ArouraParticlesPayload;
import net.cosmos.moonlit.network.BronzeMaskDarknessPayload;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.Map;

import static net.cosmos.moonlit.Moonlit.moonlitPath;
import static net.cosmos.moonlit.init.ModAttachmentTypes.*;

@EventBusSubscriber(modid = Moonlit.MOD_ID)
public class CommonEvents {

    @SubscribeEvent
    public static void registerListeners(RegisterCommandsEvent event) {
        MeteorCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Moonlit.MOD_ID).versioned("1.0");

        registrar.playToClient(ArouraParticlesPayload.TYPE, ArouraParticlesPayload.STREAM_CODEC, ArouraParticlesPayload::handle);
        registrar.playToClient(BronzeMaskDarknessPayload.TYPE, BronzeMaskDarknessPayload.STREAM_CODEC, BronzeMaskDarknessPayload::handle);
    }

    /**
     * @see String The old name of the block/item (String)
     * <p>
     * @see ResourceLocation The new name of the block/item (ResourceLocation)
     */
    private static final Map<String, ResourceLocation> reMapBlock = new HashMap<>();
    private static final Map<String, ResourceLocation> reMapItem = new HashMap<>();

    static {
        reMapBlock.put("moon_light_pyre", moonlitPath("moonlight_pyre"));
        reMapBlock.put("bronze_tiles_stairs", moonlitPath("bronze_tile_stairs"));
        reMapBlock.put("bronze_tiles_slab", moonlitPath("bronze_tile_slab"));
        reMapItem.put("moonlit_bronze", moonlitPath("moonlit_bronze_ingot"));
    }

    @SubscribeEvent
    public static void missingMappings(RegisterEvent event) {
        Registry<?> registry = event.getRegistry();
        if (registry.key() == Registries.BLOCK) {
            reMapBlock.forEach((string, resourceLocation) -> registry.addAlias(moonlitPath(string), resourceLocation));
            ModBlocks.BLOCKS.getEntries().forEach((block) -> registry.addAlias(ResourceLocation.fromNamespaceAndPath("moonlit_additions", block.getId().getPath()), block.getId()));
        }
        if (registry.key() == Registries.ITEM) {
            reMapBlock.forEach((string, resourceLocation) -> registry.addAlias(moonlitPath(string), resourceLocation));
            reMapItem.forEach((string, resourceLocation) -> registry.addAlias(moonlitPath(string), resourceLocation));
            ModItems.ITEMS.getEntries().forEach((item) -> registry.addAlias(ResourceLocation.fromNamespaceAndPath("moonlit_additions", item.getId().getPath()), item.getId()));
        }
        if (registry.key() == Registries.BLOCK_ENTITY_TYPE) {
            ModBlockEntities.BLOCK_ENTITIES.getEntries().forEach((val) -> registry.addAlias(ResourceLocation.fromNamespaceAndPath("moonlit_additions", val.getId().getPath()), val.getId()));
        }
        if (registry.key() == NeoForgeRegistries.Keys.ATTACHMENT_TYPES) {
            ATTACHMENT_TYPES.getEntries().forEach((val) -> registry.addAlias(ResourceLocation.fromNamespaceAndPath("moonlit_additions", val.getId().getPath()), val.getId()));
        }
        if (registry.key() == NMLRegistries.DREAM_TYPE_KEY) {
            ModDreamTypes.DREAM_TYPES_REGISTRY.getEntries().forEach((val) -> registry.addAlias(ResourceLocation.fromNamespaceAndPath("moonlit_additions", val.getId().getPath()), val.getId()));
        }
    }

    @SubscribeEvent
    public static void entityTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof Player player) {
            boolean shouldTick = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(10)).stream().anyMatch((living) -> BronzeMaskItem.isWearingBronzeMask(living) || AccessoriesClientCompat.isWearingBronzeMask(living, false));
            if (player instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new BronzeMaskDarknessPayload(shouldTick));
        }
    }
}
