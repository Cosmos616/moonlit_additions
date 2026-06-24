package net.cosmos.moonlit_additions.common.block_entity;

import net.cosmos.moonlit_additions.MoonLitAdditions;
import net.cosmos.moonlit_additions.common.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MoonLitAdditions.MOD_ID);

    public static final Supplier<BlockEntityType<BronzeBellBlockEntity>> BRONZE_BELL_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("bronze_bell_block_entity", () -> BlockEntityType.Builder.of(
                    BronzeBellBlockEntity::new, ModBlocks.BRONZE_BELL.get()).build(null));

    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}
