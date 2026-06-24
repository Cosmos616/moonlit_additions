package net.cosmos.moonlit_additions.datagen.server.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashSet;
import java.util.Set;

public class MoonlitBlockLootTables extends BlockLootSubProvider {
    private final Set<Block> generatedLootTables = new HashSet<>();

    public MoonlitBlockLootTables(HolderLookup.Provider pRegistries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), pRegistries);
    }

    @Override
    protected void generate() {

    }

    protected void dropNamedContainer(Block block) {
        add(block, this::createNameableBlockEntityTable);
    }

    protected void dropSelf(Block... block) {
        for (Block b : block) {
            dropSelf(b);
        }
    }

    @Override
    protected void add(Block block, LootTable.Builder builder) {
        this.generatedLootTables.add(block);
        this.map.put(block.getLootTable(), builder);
    }
    protected void otherWhenSilkTouch(Block pBlock, Block pOther) {
        this.add(pBlock, createSilkTouchOnlyTable(pOther));
    }


    @Override
    protected Iterable<Block> getKnownBlocks() {
        return generatedLootTables;
    }
}
