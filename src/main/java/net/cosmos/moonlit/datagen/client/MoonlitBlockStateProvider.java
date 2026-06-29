package net.cosmos.moonlit.datagen.client;

import net.cosmos.moonlit.Moonlit;
import net.cosmos.moonlit.common.block.dream.BronzeBellBlock;
import net.cosmos.moonlit.common.block.BronzePillarBlock;
import net.cosmos.moonlit.common.block.MoonLightPyreBlock;
import net.cosmos.moonlit.common.block.forge.BronzeLensBlock;
import net.cosmos.moonlit.common.block.forge.ManufacturedSunBlock;
import net.cosmos.moonlit.init.ModBlocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Supplier;

import static net.cosmos.moonlit.Moonlit.moonlitPath;

public class MoonlitBlockStateProvider extends BlockStateProvider {

    public MoonlitBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Moonlit.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        this.simpleBlockAndItem(ModBlocks.BRONZE_TILES);
        this.simpleBlockAndItem(ModBlocks.MOONLIT_ASH_BLOCK, moonlitPath("block/moonlit_ash"));
        this.litCube(ModBlocks.METEOR);
        this.simpleBlockAndItem(ModBlocks.HOLLOW_METEOR, RenderType.CUTOUT);
        this.simpleStairs(ModBlocks.BRONZE_TILE_STAIRS, ModBlocks.BRONZE_TILES);
        this.simpleSlab(ModBlocks.BRONZE_TILE_SLAB, ModBlocks.BRONZE_TILES);
        this.shinyPillar(ModBlocks.BRONZE_PILLAR);
        this.directionalBlock(ModBlocks.BRONZE_PILLAR_BASE.block(), (state) -> models().getExistingFile(moonlitPath("block/bronze_pillar_base")));
        this.simpleBlockItem(ModBlocks.BRONZE_PILLAR_BASE.get(), models().getExistingFile(moonlitPath("block/bronze_pillar_base")));
        this.pyre(ModBlocks.MOONLIGHT_PYRE);
        this.bell(ModBlocks.BRONZE_BELL);
        this.pile(ModBlocks.MOONLIT_ASH_PILE, ModBlocks.MOONLIT_ASH_BLOCK);
        this.sun(ModBlocks.MANUFACTURED_SUN);
        this.lens(ModBlocks.BRONZE_LENS);
    }

    private void simpleBlockItem(Supplier<? extends Block> block) {
        super.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(moonlitPath("block/" + this.name(block.get()))));
    }
    private void simpleBlockWithItem(Supplier<? extends Block> block) {
        super.simpleBlockWithItem(block.get(), new ModelFile.UncheckedModelFile(moonlitPath("block/" + this.name(block.get()))));
    }
    private void simpleBlockAndItem(Supplier<? extends Block> block) {
        this.simpleBlock(block.get());
        this.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(moonlitPath("block/" + this.name(block.get()))));
    }
    private void simpleBlockAndItem(Supplier<? extends Block> block, RenderType renderType) {
        this.simpleBlock(block.get(), this.models().cubeAll(this.name(block.get()), this.blockTexture(block.get())).renderType(renderType.name));
        this.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(moonlitPath("block/" + this.name(block.get()))));
    }
    private void simpleBlockAndItem(Supplier<? extends Block> block, ResourceLocation texture) {
        this.simpleBlock(block.get(), this.models().cubeAll(this.name(block.get()), texture));
        this.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(moonlitPath("block/" + this.name(block.get()))));
    }
    private void simpleBlockAndItem(Supplier<? extends Block> block, ResourceLocation texture, RenderType renderType) {
        this.simpleBlock(block.get(), this.models().cubeAll(this.name(block.get()), texture).renderType(renderType.name));
        this.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(moonlitPath("block/" + this.name(block.get()))));
    }
    private void cubePillar(Supplier<? extends Block> block, ResourceLocation end) {
        String name = this.name(block.get());
        ResourceLocation side = moonlitPath("block/" + name + "/side");
        this.simpleBlock(block.get(), this.models().cubeBottomTop(name, side, end, end));
        this.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(moonlitPath("block/" + this.name(block.get()))));
    }
    private void cubeBottomTop(Supplier<? extends Block> block, ResourceLocation top, ResourceLocation bottom) {
        String name = this.name(block.get());
        ResourceLocation side = moonlitPath("block/" + name);
        this.simpleBlock(block.get(), this.models().cubeBottomTop(name, side, top, bottom));
        this.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(moonlitPath("block/" + this.name(block.get()))));
    }
    private void blockBottomTop(Supplier<? extends Block> block) {
        String name = this.name(block.get());
        ResourceLocation side = moonlitPath("block/" + name);
        ResourceLocation top = moonlitPath("block/" + name + "_top");
        ResourceLocation bottom = moonlitPath("block/" + name + "_bottom");
        this.simpleBlock(block.get(), this.models().cubeBottomTop(name, side, bottom, top));
        this.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(moonlitPath("block/" + this.name(block.get()))));
    }
    private void sidedSlab(Supplier<? extends Block> block, ResourceLocation side, ResourceLocation top, ResourceLocation bottom) {
        String name = this.name(block.get());
        ResourceLocation doubleSlab = moonlitPath("block/" + name.replace("_slab", ""));
        this.slabBlock((SlabBlock) block.get(), doubleSlab, side, top, bottom);
        this.simpleBlockItem(block.get(), models().slab(name + "_inventory", side, top, bottom));
        this.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(moonlitPath("block/" + this.name(block.get()) + "_inventory")));
    }
    private void sidedStairs(Supplier<? extends Block> block, ResourceLocation side, ResourceLocation top, ResourceLocation bottom) {
        String name = this.name(block.get());
        this.stairsBlock((StairBlock) block.get(), name, side, top, bottom);
        this.simpleBlockItem(block.get(), models().stairs(name + "_inventory", side, top, bottom));
        this.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(moonlitPath("block/" + this.name(block.get()) + "_inventory")));
    }

    private void simpleSlab(Supplier<? extends Block> block, Supplier<? extends Block> texture) {
        String name = this.name(block.get());
        ResourceLocation main = blockTexture(texture.get());
        ResourceLocation doubleSlab = moonlitPath("block/" + this.name(texture.get()));
        this.slabBlock((SlabBlock) block.get(), doubleSlab, main, main, main);
        this.simpleBlockItem(block.get(), models().slab(name + "_inventory", main, main, main));
        this.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(moonlitPath("block/" + this.name(block.get()) + "_inventory")));
    }
    private void simpleStairs(Supplier<? extends Block> block, Supplier<? extends Block> texture) {
        String name = this.name(block.get());
        ResourceLocation main = blockTexture(texture.get());
        this.stairsBlock((StairBlock) block.get(), name, main, main, main);
        this.simpleBlockItem(block.get(), models().stairs(name + "_inventory", main, main, main));
        this.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(moonlitPath("block/" + this.name(block.get()) + "_inventory")));
    }
    private void simpleExisting(Supplier<? extends Block> block, ResourceLocation existing) {
        this.simpleBlock(block.get(), new ModelFile.UncheckedModelFile(existing));
        this.simpleBlockItem(block);
    }

    private void horizontalFacing(Supplier<? extends Block> block, String loc) {
        getVariantBuilder(block.get()).forAllStatesExcept(state -> {
            ResourceLocation modelLoc = moonlitPath("block/" + loc);
            int yRot = Mth.floor((state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360);
            return ConfiguredModel.builder().modelFile(new ModelFile.UncheckedModelFile(modelLoc)).rotationY(yRot).build();
        });
    }

    private void shinyPillar(Supplier<? extends Block> block) {
        String name  = this.name(block.get());
        this.getVariantBuilder(block.get()).forAllStates((state) -> {
            boolean shiny = state.getValue(BronzePillarBlock.SHINY);
            var axis = state.getValue(BronzePillarBlock.AXIS);
            var texture = "block/%s".formatted(name + (shiny ? "_shiny" : ""));
            int y = axis.equals(Direction.Axis.X) ? 90 : 180;
            int x = axis.equals(Direction.Axis.Y) ? 0 : 90;
            var model = this.models().cubeColumn(name + (shiny ? "_shiny" : ""), moonlitPath(texture), moonlitPath("block/%s_top".formatted(name)));
            return ConfiguredModel.builder().modelFile(model).rotationX(x).rotationY(y).build();
        });
        this.simpleBlockItem(block);
    }

    private void pyre(Supplier<? extends Block> block) {
        String name  = this.name(block.get());
        this.getVariantBuilder(block.get()).forAllStates((state) -> {
            int layer = state.getValue(MoonLightPyreBlock.LAYERS);
            boolean lit = state.getValue(MoonLightPyreBlock.LIT);
            var model = models().getExistingFile(moonlitPath("block/%s/%s".formatted(name, "height_%s%s".formatted(layer, (lit ? "_lit" : "")))));
            return ConfiguredModel.builder().modelFile(model).build();
        });
        this.simpleBlockItem(block.get(), models().getExistingFile(moonlitPath("block/%s/height_5".formatted(name))));
    }

    private void bell(Supplier<? extends Block> block) {
        String name  = this.name(block.get());
        this.horizontalBlock(block.get(), (state) -> models().getExistingFile(moonlitPath("block/%s/%s".formatted(name, state.getValue(BronzeBellBlock.ATTACHMENT).getSerializedName()))), 90);
        this.simpleBlockItem(block.get(), models().getExistingFile(moonlitPath("block/%s/item".formatted(name))));
    }

    private void pile(Supplier<? extends Block> block, Supplier<? extends Block> full) {
        String name = this.name(block.get());
        this.getVariantBuilder(block.get()).forAllStates(blockState -> {
            int layer = blockState.getValue(SnowLayerBlock.LAYERS);
            ModelFile.ExistingModelFile model;
            if (layer < 8 ) model = this.models().getExistingFile(moonlitPath("block/%s/%s".formatted(name, "height_%s".formatted(layer*2))));
            else model = this.models().getExistingFile(moonlitPath("block/%s".formatted(this.name(full.get()))));
            return ConfiguredModel.builder().modelFile(model).build();
        });
        this.simpleBlockItem(block.get(), models().getExistingFile(moonlitPath("block/%s/%s".formatted(name, "height_2"))));
    }

    private void litCube(Supplier<? extends Block> block) {
        String name  = this.name(block.get());
        this.getVariantBuilder(block.get()).forAllStatesExcept(blockState -> {
            boolean lit = blockState.getValue(BlockStateProperties.LIT);
            String loc = name + (lit ? "_lit" : "");
            var model = this.models().cubeAll(loc, moonlitPath("block/%s".formatted(loc)));
            return ConfiguredModel.builder().modelFile(model).build();
        }, BlockStateProperties.PERSISTENT);
        this.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(moonlitPath("block/%s".formatted(name))));
    }

    private void sun(Supplier<? extends Block> block) {
        String name = this.name(block.get());
        this.getVariantBuilder(block.get()).forAllStates(blockState -> {
            ManufacturedSunBlock.Stage stage = blockState.getValue(ManufacturedSunBlock.STAGE);
            var model = this.models().getExistingFile(moonlitPath("block/%s/%s".formatted(name, stage.getSerializedName())));
            return ConfiguredModel.builder().modelFile(model).build();
        });
        this.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(moonlitPath("block/%s/%s".formatted(name, ManufacturedSunBlock.Stage.BLOOMED.getSerializedName()))));
    }

    private void lens(Supplier<? extends Block> block) {
        String name  = this.name(block.get());
        this.getVariantBuilder(block.get()).forAllStates(blockState -> {
            var direction = blockState.getValue(BronzeLensBlock.FACING);
            var model = this.models().getExistingFile(moonlitPath("block/%s/base".formatted(name)));
            return ConfiguredModel.builder().modelFile(model)
                    .rotationX(direction == Direction.DOWN ? 180 : direction.getAxis().isHorizontal() ? 90 : 0)
                    .rotationY(direction.getAxis().isVertical() ? 0 : (((int) direction.toYRot() + 180)) % 360)
                    .build();
        });
        this.simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(moonlitPath("block/%s/item".formatted(name))));
    }

    private ResourceLocation extend(ResourceLocation rl, String suffix) {return ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), rl.getPath() + suffix);}

    private String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }
}
