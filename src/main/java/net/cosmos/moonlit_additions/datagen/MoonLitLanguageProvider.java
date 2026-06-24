package net.cosmos.moonlit_additions.datagen;

import com.farcr.nomansland.common.definitions.BlockDefinition;
import com.farcr.nomansland.common.definitions.ItemDefinition;
import net.cosmos.moonlit_additions.MoonLitAdditions;
import net.cosmos.moonlit_additions.common.block.ModBlocks;
import net.cosmos.moonlit_additions.common.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class MoonLitLanguageProvider extends LanguageProvider {

    public MoonLitLanguageProvider(PackOutput output) {
        super(output, MoonLitAdditions.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        for (BlockDefinition<?> definition : ModBlocks.BLOCK_DEFINITIONS) {
            if (!definition.hasCustomLang()) {
                add(definition.langKey(), definition.langName());
            }
        }
        for (ItemDefinition<?> definition : ModItems.ITEM_DEFINITIONS) {
            if (!definition.hasCustomLang() && !definition.isBlockItem()) {
                add(definition.langKey(), definition.langName());
            }
        }
        add("itemGroup.moonlit_additions", "Moon Light Additions");
        add("item.moonlit_additions.bronze_mask_allomancer", "Bronze Mask");
    }
}
