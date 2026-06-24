package net.cosmos.moonlit_additions.datagen.server.tags;

import net.cosmos.moonlit_additions.MoonlitAdditions;
import net.cosmos.moonlit_additions.init.ModBlocks;
import net.cosmos.moonlit_additions.init.MoonlitTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.cosmos.moonlit_additions.init.ModBlocks.*;

public class MoonlitBlockTagsProvider extends BlockTagsProvider {
    public MoonlitBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MoonlitAdditions.MOD_ID, existingFileHelper);
    }

    protected void addTags(HolderLookup.Provider provider) {
        this.tag(MoonlitTags.MOONLIT_ASH_BLOCKS.blockTag()).add(ModBlocks.MOONLIT_ASH_BLOCK.block());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                BRONZE_TILES.block(), BRONZE_TILES_STAIRS.block(), BRONZE_TILES_SLAB.block(),
                BRONZE_PILLAR.block(), BRONZE_PILLAR_BASE.block(), BRONZE_BELL.block()
        );
        this.tag(BlockTags.NEEDS_IRON_TOOL).add(
                BRONZE_TILES.block(), BRONZE_TILES_STAIRS.block(), BRONZE_TILES_SLAB.block(),
                BRONZE_PILLAR.block(), BRONZE_PILLAR_BASE.block(), BRONZE_BELL.block()
        );
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(
                MOONLIT_ASH_PILE.block(), MOONLIT_ASH_BLOCK.block()
        );
    }

    @SafeVarargs
    protected final void addToTags(Block block, TagKey<Block>... blockTags) {
        List.of(blockTags).forEach((blockTag) -> this.tag(blockTag).add(block));
    }

    @SafeVarargs
    protected final void addToTags(TagKey<Block> block, TagKey<Block>... blockTags) {
        List.of(blockTags).forEach((blockTag) -> this.tag(blockTag).addTag(block));
    }
}
