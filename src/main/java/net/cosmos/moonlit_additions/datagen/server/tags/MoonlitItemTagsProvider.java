package net.cosmos.moonlit_additions.datagen.server.tags;

import net.cosmos.moonlit_additions.MoonlitAdditions;
import net.cosmos.moonlit_additions.init.ModBlocks;
import net.cosmos.moonlit_additions.init.ModItems;
import net.cosmos.moonlit_additions.init.MoonlitTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class MoonlitItemTagsProvider extends ItemTagsProvider {
    public MoonlitItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, MoonlitAdditions.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ItemTags.HEAD_ARMOR).add(ModItems.BRONZE_MASK_ALLOMANCER.get());
        this.tag(ItemTags.HEAD_ARMOR_ENCHANTABLE).add(ModItems.BRONZE_MASK_ALLOMANCER.get());

        this.tag(MoonlitTags.MOONLIT_ASH).add(ModItems.MOONLIT_ASH.get());
        this.tag(MoonlitTags.MOONLIT_ASH_BLOCKS.itemTag()).add(ModBlocks.MOONLIT_ASH_BLOCK.asItem());
    }
}
