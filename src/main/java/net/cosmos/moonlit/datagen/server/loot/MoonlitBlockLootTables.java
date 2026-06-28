package net.cosmos.moonlit.datagen.server.loot;

import com.farcr.nomansland.common.definitions.BlockDefinition;
import net.cosmos.moonlit.common.block.MoonLightPyreBlock;
import net.cosmos.moonlit.common.block.forge.ManufacturedSunBlock;
import net.cosmos.moonlit.init.ModBlocks;
import net.cosmos.moonlit.init.ModItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MoonlitBlockLootTables extends BlockLootSubProvider {
    private final Set<Block> generatedLootTables = new HashSet<>();

    public MoonlitBlockLootTables(HolderLookup.Provider pRegistries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), pRegistries);
    }

    @Override
    protected void generate() {
        dropSelf(
                ModBlocks.BRONZE_BELL,
                ModBlocks.BRONZE_TILES,
                ModBlocks.BRONZE_TILE_STAIRS,
                ModBlocks.BRONZE_PILLAR,
                ModBlocks.BRONZE_PILLAR_BASE,
                ModBlocks.MOONLIT_ASH_BLOCK,
                ModBlocks.METEOR,
                ModBlocks.HOLLOW_METEOR
        );
        this.add(ModBlocks.MOONLIGHT_PYRE.block(), pyreDrops(ModBlocks.MOONLIGHT_PYRE));
        this.add(ModBlocks.BRONZE_TILE_SLAB.block(), createSlabItemTable(ModBlocks.BRONZE_TILE_SLAB.block()));
        this.add(ModBlocks.MOONLIT_ASH_PILE.block(), ashPileDrops(ModBlocks.MOONLIT_ASH_PILE));
        this.add(ModBlocks.MANUFACTURED_SUN.block(), sunDrops(ModBlocks.MANUFACTURED_SUN));
    }

    protected void dropNamedContainer(Block block) {
        add(block, this::createNameableBlockEntityTable);
    }

    protected void dropSelf(BlockDefinition<?>... block) {
        for (BlockDefinition<?> b : block) {
            dropSelf(b.block());
        }
    }

    protected LootTable.Builder pyreDrops(BlockDefinition<?> pyre) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                .add(this.applyExplosionDecay(pyre, LootItem.lootTableItem(pyre)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))
                                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(pyre.block()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(MoonLightPyreBlock.LAYERS, 5))))))
                .add(this.applyExplosionDecay(pyre, LootItem.lootTableItem(ModItems.MOONLIT_WAX)
                        .apply(List.of(1, 2, 3, 4), (layers) -> SetItemCountFunction.setCount(layers == 1 ? ConstantValue.exactly(1) : UniformGenerator.between(1, layers))
                                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(pyre.block()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(MoonLightPyreBlock.LAYERS, layers))))
                )));
    }

    protected LootTable.Builder ashPileDrops(BlockDefinition<?> ash) {
        return LootTable.lootTable().withPool(LootPool.lootPool().when(LootItemEntityPropertyCondition.entityPresent(LootContext.EntityTarget.THIS))
                .add(AlternativesEntry.alternatives(AlternativesEntry.alternatives(SnowLayerBlock.LAYERS.getPossibleValues(),
                        (layers) -> (LootItem.lootTableItem(ModItems.MOONLIT_ASH)
                                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ash.block()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowLayerBlock.LAYERS, layers))))
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly((float)layers))))
                        .when(this.doesNotHaveSilkTouch()), AlternativesEntry.alternatives(SnowLayerBlock.LAYERS.getPossibleValues(), (layers) -> (layers == 8 ? LootItem.lootTableItem(ModBlocks.MOONLIT_ASH_BLOCK) :
                        LootItem.lootTableItem(ModBlocks.MOONLIT_ASH_PILE)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly((float)layers))).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ash.block()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowLayerBlock.LAYERS, layers))))))));
    }

    protected LootTable.Builder sunDrops(BlockDefinition<?> sun) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                .add(this.applyExplosionDecay(sun, LootItem.lootTableItem(sun)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))
                                .when(InvertedLootItemCondition.invert(LootItemBlockStatePropertyCondition.hasBlockStateProperties(sun.block()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ManufacturedSunBlock.STAGE, ManufacturedSunBlock.Stage.FAILED)))))))
                .add(this.applyExplosionDecay(sun, LootItem.lootTableItem(ModItems.FAILED_SUN)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))
                                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(sun.block()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ManufacturedSunBlock.STAGE, ManufacturedSunBlock.Stage.FAILED))))
                )));
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
