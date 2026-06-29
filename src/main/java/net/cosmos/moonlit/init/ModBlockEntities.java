package net.cosmos.moonlit.init;

import net.cosmos.moonlit.Moonlit;
import net.cosmos.moonlit.common.block_entity.dream.BronzeBellBlockEntity;
import net.cosmos.moonlit.common.block_entity.forge.ManufacturedSunBlockEntity;
import net.cosmos.moonlit.common.block_entity.forge.light.BronzeLensBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntityType;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntityTypeBuilder;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Moonlit.MOD_ID);

    public static final Supplier<BlockEntityType<BronzeBellBlockEntity>> BRONZE_BELL_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("bronze_bell_block_entity", () -> BlockEntityType.Builder.of(
                    BronzeBellBlockEntity::new, ModBlocks.BRONZE_BELL.get()).build(null));

    public static final Supplier<LodestoneBlockEntityType<ManufacturedSunBlockEntity>> MANUFACTURED_SUN = BLOCK_ENTITIES.register("manufactured_sun",
            () -> LodestoneBlockEntityTypeBuilder.create(ManufacturedSunBlockEntity::new, ModBlocks.MANUFACTURED_SUN.get()).build());

    public static final Supplier<LodestoneBlockEntityType<BronzeLensBlockEntity>> BRONZE_LENS = BLOCK_ENTITIES.register("bronze_lens",
            () -> LodestoneBlockEntityTypeBuilder.create(BronzeLensBlockEntity::new, ModBlocks.BRONZE_LENS.get()).build());

    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}
