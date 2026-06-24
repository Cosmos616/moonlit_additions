package net.cosmos.moonlit_additions.datagen.server.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashSet;
import java.util.Set;

public class MoonlitEntityLootTables extends EntityLootSubProvider {
    private final Set<EntityType<?>> generatedLootTables = new HashSet<>();

    public MoonlitEntityLootTables(HolderLookup.Provider registries) {
        super(FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    public void generate() {

    }

    @Override
    protected void add(EntityType<?> entityType, LootTable.Builder builder) {
        this.generatedLootTables.add(entityType);
        this.add(entityType, entityType.getDefaultLootTable(), builder);
    }

    protected java.util.stream.Stream<EntityType<?>> getKnownEntityTypes() {
        return generatedLootTables.stream();
    }
}
