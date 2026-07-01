package net.cosmos.moonlit.init;

import net.cosmos.moonlit.Moonlit;
import net.cosmos.moonlit.common.block.forge.BronzeLensBlock;
import net.cosmos.moonlit.common.block.forge.BronzeMirrorBlock;
import net.cosmos.moonlit.common.block_entity.dream.BronzeBellBlockEntity;
import net.cosmos.moonlit.common.block_entity.forge.ManufacturedSunBlockEntity;
import net.cosmos.moonlit.common.block_entity.forge.light.BronzeLensBlockEntity;
import net.cosmos.moonlit.common.block_entity.forge.light.BronzeMirrorBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntityTicker;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntityType;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntityTypeBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Moonlit.MOD_ID);

    public static final Supplier<BlockEntityType<BronzeBellBlockEntity>> BRONZE_BELL_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("bronze_bell_block_entity", () -> BlockEntityType.Builder.of(
                    BronzeBellBlockEntity::new, ModBlocks.BRONZE_BELL.get()).build(null));

    public static final Supplier<LodestoneBlockEntityType<ManufacturedSunBlockEntity>> MANUFACTURED_SUN = BLOCK_ENTITIES.register("manufactured_sun",
            () -> LodestoneBlockEntityTypeBuilder.create(ManufacturedSunBlockEntity::new, ModBlocks.MANUFACTURED_SUN.get()).setTickerType(LodestoneBlockEntityTicker.Type.BOTH).build());

    public static final Supplier<LodestoneBlockEntityType<BronzeLensBlockEntity>> BRONZE_LENS = BLOCK_ENTITIES.register("bronze_lens",
            () -> LodestoneBlockEntityTypeBuilder.create(BronzeLensBlockEntity::new, getBlocks(BronzeLensBlock.class)).setTickerType(LodestoneBlockEntityTicker.Type.BOTH).build());

    public static final Supplier<LodestoneBlockEntityType<BronzeMirrorBlockEntity>> BRONZE_MIRROR = BLOCK_ENTITIES.register("bronze_mirror",
            () -> LodestoneBlockEntityTypeBuilder.create(BronzeMirrorBlockEntity::new, getBlocks(BronzeMirrorBlock.class)).setTickerType(LodestoneBlockEntityTicker.Type.BOTH).build());

    public static Block[] getBlocks(Class<?>... blockClasses) {
        Collection<DeferredHolder<Block, ? extends Block>> blocks = ModBlocks.BLOCKS.getEntries();
        List<Block> matchingBlocks = new ArrayList<>();
        for (DeferredHolder<Block, ? extends Block> registryObject : blocks) {
            if (registryObject.isBound() && Arrays.stream(blockClasses).anyMatch(b -> b.isInstance(registryObject.get()))) {
                matchingBlocks.add(registryObject.get());
            }
        }
        return matchingBlocks.toArray(new Block[0]);
    }

    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}
