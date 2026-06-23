package net.cosmos.moonlit_additions.block;

import com.farcr.nomansland.common.block.moonlight.MoonlightBasinBlock;
import com.mojang.serialization.MapCodec;
import net.cosmos.moonlit_additions.MoonLitAdditions;
import net.cosmos.moonlit_additions.block.custom.BronzePillarBaseBlock;
import net.cosmos.moonlit_additions.block.MoonLightPyreBlock;
import net.cosmos.moonlit_additions.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(MoonLitAdditions.MOD_ID);

    public static final DeferredRegister.Blocks ITEMLESS_BLOCKS =
            DeferredRegister.createBlocks(MoonLitAdditions.MOD_ID);

    public static final DeferredBlock<Block> BRONZE_TILES = registerBlock("bronze_tiles",
            ()-> new Block(BlockBehaviour.Properties.of()
                    .strength(4f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHERITE_BLOCK)
                    ));
    public static final DeferredBlock<Block> BRONZE_TILES_STAIRS = registerBlock("bronze_tiles_stairs",
            ()-> new StairBlock(BRONZE_TILES.get().defaultBlockState(), BlockBehaviour.Properties.of()
                    .strength(4f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHERITE_BLOCK)
            ));
    public static final DeferredBlock<Block> BRONZE_TILES_SLAB = registerBlock("bronze_tiles_slab",
            ()-> new SlabBlock(BlockBehaviour.Properties.of()
                    .strength(4f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHERITE_BLOCK)
            ));

    public static final DeferredBlock<Block> BRONZE_PILLAR = registerBlock("bronze_pillar",
            ()-> new BronzePillarBlock(BlockBehaviour.Properties.of()
                    .strength(4f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHERITE_BLOCK)
            ));

    public static final DeferredBlock<Block> BRONZE_PILLAR_BASE = registerBlock("bronze_pillar_base",
            ()-> new BronzePillarBaseBlock(BlockBehaviour.Properties.of()
                    .strength(4f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHERITE_BLOCK)
            ));
    public static final DeferredBlock<Block> BRONZE_BELL = registerBlock("bronze_bell",
            ()-> new BronzeBellBlock(BlockBehaviour.Properties.of()
                    .strength(4f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHERITE_BLOCK)
            ));

    public static final DeferredBlock<Block> BRONZE_BELL_BODY_MODEL = registerBlockWithoutItem("bronze_bell_body_model",
            ()-> new BronzeBellBlock(BlockBehaviour.Properties.of()
            ));

    public static final DeferredBlock<Block> MOONLIGHT_PYRE = registerBlock("moon_light_pyre",
            ()-> new MoonLightPyreBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.CANDLE)
                    .lightLevel(state -> state.getValue(MoonLightPyreBlock.LIT) ? 15 : 0)
            ));

    public static final DeferredBlock<Block> MOONLIT_ASH_PILE = registerBlock("moonlit_ash_pile",
            ()-> new SnowLayerBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.SAND)
            ));

    public static final DeferredBlock<Block> MOONLIT_ASH_BLOCK = registerBlock("moonlit_ash_block",
            ()-> new MoonLitAshBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.SAND)
            ));

    public static final DeferredBlock<Block> MOONLIT_FIRE = registerBlockWithoutItem("moonlit_fire",
            () -> new MoonLitFireBlock(BlockBehaviour.Properties.of()
                    .replaceable()
                    .noCollission()
                    .instabreak()
                    .lightLevel(state -> 12)
                    .sound(SoundType.WOOL)
            ));

    public static final DeferredBlock<Block> METEOR =
            registerBlock(
                    "meteor",
                    () -> new MeteorBlock(
                            BlockBehaviour.Properties.of()
                                    .strength(3.5F, 6.0F)
                                    .requiresCorrectToolForDrops()
                                    .lightLevel(state -> state.getValue(MeteorBlock.LIT) ? 12 : 0)
                    )
            );


    private static <T extends Block> DeferredBlock<T> registerBlockWithoutItem(String name, Supplier<T> block){
        DeferredBlock<T> toReturn = ITEMLESS_BLOCKS.register(name,block);
        return toReturn;
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block){
        DeferredBlock<T> toReturn = BLOCKS.register(name,block);
        registerBlockItem(name,toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block){
        ModItems.ITEMS.register(name,() -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMLESS_BLOCKS.register(eventBus);
    }
}
