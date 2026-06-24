package net.cosmos.moonlit_additions.datagen;

import net.cosmos.moonlit_additions.MoonlitAdditions;
import net.cosmos.moonlit_additions.datagen.client.MoonlitBlockStateProvider;
import net.cosmos.moonlit_additions.datagen.client.MoonlitItemModelProvider;
import net.cosmos.moonlit_additions.datagen.client.MoonlitLanguageProvider;
import net.cosmos.moonlit_additions.datagen.server.MoonlitAdvancementProvider;
import net.cosmos.moonlit_additions.datagen.server.loot.MoonlitLootGenerator;
import net.cosmos.moonlit_additions.datagen.server.tags.MoonlitBlockTagsProvider;
import net.cosmos.moonlit_additions.datagen.server.tags.MoonlitItemTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = MoonlitAdditions.MOD_ID)
public class MoonlitDataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();

        boolean server = event.includeServer();
        boolean client = event.includeClient();

        MoonlitBlockTagsProvider blockTags = new MoonlitBlockTagsProvider(packOutput, lookupProvider, existingFileHelper);
        MoonlitItemTagsProvider itemTags = new MoonlitItemTagsProvider(packOutput, lookupProvider, blockTags.contentsGetter(), existingFileHelper);

        AdvancementProvider advancements = new AdvancementProvider(packOutput, lookupProvider, existingFileHelper, List.of(new MoonlitAdvancementProvider()));
        MoonlitLootGenerator lootGenerator = new MoonlitLootGenerator(packOutput, lookupProvider);

        MoonlitLanguageProvider language = new MoonlitLanguageProvider(packOutput);
        MoonlitItemModelProvider itemModels = new MoonlitItemModelProvider(packOutput, existingFileHelper);
        MoonlitBlockStateProvider blockStates = new MoonlitBlockStateProvider(packOutput, existingFileHelper);

        generator.addProvider(server, blockTags);
        generator.addProvider(server, itemTags);

        generator.addProvider(server, advancements);
        generator.addProvider(server, lootGenerator);

        generator.addProvider(client, itemModels);
        generator.addProvider(client, blockStates);
        generator.addProvider(client, language);
    }
}
