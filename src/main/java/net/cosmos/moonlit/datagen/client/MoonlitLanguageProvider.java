package net.cosmos.moonlit.datagen.client;

import com.farcr.nomansland.common.definitions.BlockDefinition;
import com.farcr.nomansland.common.definitions.ItemDefinition;
import net.cosmos.moonlit.Moonlit;
import net.cosmos.moonlit.init.ModBlocks;
import net.cosmos.moonlit.init.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class MoonlitLanguageProvider extends LanguageProvider {

    public MoonlitLanguageProvider(PackOutput output) {
        super(output, Moonlit.MOD_ID, "en_us");
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
        add("itemGroup.moonlit", "Moonlit");
        add("item.moonlit.bronze_mask_allomancer", "Bronze Mask");
    }
}
