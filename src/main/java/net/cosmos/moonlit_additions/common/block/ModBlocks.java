package net.cosmos.moonlit_additions.common.block;

import com.farcr.nomansland.common.definitions.BlockDefinition;
import com.farcr.nomansland.common.definitions.BlockProperties;
import net.cosmos.moonlit_additions.MoonLitAdditions;
import net.cosmos.moonlit_additions.common.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MoonLitAdditions.MOD_ID);
    public static List<BlockDefinition<?>> BLOCK_DEFINITIONS = new ArrayList<>();

    public static final BlockDefinition<Block> BRONZE_TILES = register("bronze_tiles",
            ()-> new Block(BlockBehaviour.Properties.of()
                    .strength(4f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHERITE_BLOCK)
                    ));
    public static final BlockDefinition<Block> BRONZE_TILES_STAIRS = register("bronze_tiles_stairs",
            ()-> new StairBlock(BRONZE_TILES.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHERITE_BLOCK)
            ));
    public static final BlockDefinition<Block> BRONZE_TILES_SLAB = register("bronze_tiles_slab",
            ()-> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHERITE_BLOCK)
            ));

    public static final BlockDefinition<Block> BRONZE_PILLAR = register("bronze_pillar",
            ()-> new BronzePillarBlock(BlockBehaviour.Properties.of()
                    .strength(4f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHERITE_BLOCK)
            ));

    public static final BlockDefinition<Block> BRONZE_PILLAR_BASE = register("bronze_pillar_base",
            ()-> new BronzePillarBaseBlock(BlockBehaviour.Properties.of()
                    .strength(4f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHERITE_BLOCK)
            ));
    public static final BlockDefinition<Block> BRONZE_BELL = register("bronze_bell",
            ()-> new BronzeBellBlock(BlockBehaviour.Properties.of()
                    .strength(4f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHERITE_BLOCK)
            ));

    //public static final BlockDefinition<Block> BRONZE_BELL_BODY_MODEL = registerNoItem("bronze_bell_body_model", ()-> new BronzeBellBlock(BlockBehaviour.Properties.of()));

    public static final BlockDefinition<Block> MOONLIGHT_PYRE = register("moon_light_pyre",
            ()-> new MoonLightPyreBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.CANDLE)
                    .lightLevel(state -> state.getValue(MoonLightPyreBlock.LIT) ? 15 : 0)
            ));

    public static final BlockDefinition<Block> MOONLIT_ASH_PILE = register("moonlit_ash_pile",
            ()-> new SnowLayerBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.SAND)
            ));

    public static final BlockDefinition<Block> MOONLIT_ASH_BLOCK = register("block_of_moonlit_ash",
            ()-> new MoonLitAshBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.SAND)
            ));

    public static final BlockDefinition<Block> MOONLIT_FIRE = registerNoItem("moonlit_fire",
            () -> new MoonLitFireBlock(BlockBehaviour.Properties.of()
                    .replaceable()
                    .noCollission()
                    .instabreak()
                    .lightLevel(state -> 12)
                    .sound(SoundType.WOOL)
            ));

    public static final BlockDefinition<Block> METEOR =
            register(
                    "meteor",
                    () -> new MeteorBlock(
                            BlockBehaviour.Properties.of()
                                    .strength(3.5F, 6.0F)
                                    .requiresCorrectToolForDrops()
                                    .lightLevel(state -> state.getValue(MeteorBlock.LIT) ? 12 : 0)
                    )
            );


    public static <T extends Block> BlockDefinition<T> registerNoItem(String name, Supplier<T> block, BlockProperties properties) {
        DeferredBlock<T> deferred = BLOCKS.register(name, block);
        BlockDefinition<T> definition = BlockDefinition.fromHolder(deferred, properties);
        BLOCK_DEFINITIONS.add(definition);
        return definition;
    }

    public static <T extends Block> BlockDefinition<T> registerNoItem(String name, Supplier<T> block) {
        return registerNoItem(name, block, BlockProperties.custom(false));
    }

    public static <T extends Block> BlockDefinition<T> registerNoItem(String name, Supplier<T> block, boolean customLang) {
        return registerNoItem(name, block, BlockProperties.custom(customLang));
    }

    public static <T extends Block> BlockDefinition<T> register(String name, Supplier<T> block, BlockProperties properties) {
        BlockDefinition<T> definition = registerNoItem(name, block, properties);
        ModItems.register(name, () -> new BlockItem(definition.get(), new Item.Properties()));
        return definition;
    }

    public static <T extends Block> BlockDefinition<T> register(String name, Supplier<T> block) {
        return register(name, block, BlockProperties.custom(false));
    }

    public static <T extends Block> BlockDefinition<T> register(String name, Supplier<T> block, boolean customLang) {
        return register(name, block, BlockProperties.custom(customLang));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
