package net.cosmos.moonlit.datagen.server.loot;

import net.cosmos.moonlit.init.MoonlitLootTables;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MoonlitLootGenerator extends LootTableProvider {

    public MoonlitLootGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, MoonlitLootTables.allBuiltin(), List.of(
                new LootTableProvider.SubProviderEntry(MoonlitChestLootTables::new, LootContextParamSets.CHEST),
                new LootTableProvider.SubProviderEntry(MoonlitBlockLootTables::new, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(MoonlitEntityLootTables::new, LootContextParamSets.ENTITY),
                new LootTableProvider.SubProviderEntry(MoonlitArchaeologyLootTables::new, LootContextParamSets.ARCHAEOLOGY)
        ), provider);
    }

    @Override
    protected void validate(WritableRegistry<LootTable> writableregistry, ValidationContext validationcontext, ProblemReporter.Collector problemreporter$collector) {

    }
}
