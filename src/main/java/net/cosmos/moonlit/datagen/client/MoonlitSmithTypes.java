package net.cosmos.moonlit.datagen.client;

import net.cosmos.moonlit.common.block.BronzeBellBlock;
import net.cosmos.moonlit.common.block.BronzePillarBlock;
import net.cosmos.moonlit.common.block.MoonLightPyreBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import team.lodestar.lodestone.modules.datagen.smith.blockstate.BlockStateSmith;

import static net.cosmos.moonlit.Moonlit.moonlitPath;

public class MoonlitSmithTypes {



    public static BlockStateSmith<BronzePillarBlock> SHINY_PILLAR = new BlockStateSmith<>(BronzePillarBlock.class, (block, provider) -> {
        var name = provider.getBlockName(block);
        provider.getVariantBuilder(block).forAllStates(blockState -> {
            boolean shiny = blockState.getValue(BronzePillarBlock.SHINY);
            var axis = blockState.getValue(BronzePillarBlock.AXIS);
            var texture = "block/%s".formatted(name + (shiny ? "_shiny" : ""));
            int y = axis.equals(Direction.Axis.X) ? 90 : 180;
            int x = axis.equals(Direction.Axis.Y) ? 0 : 90;
            var model = provider.models().cubeColumn(name, moonlitPath(texture), moonlitPath("block/%s_top".formatted(name)));
            return ConfiguredModel.builder().modelFile(model).rotationX(x).rotationY(y).build();
        });
    });

    public static BlockStateSmith<Block> PILLAR_BASE = new BlockStateSmith<>(Block.class, (block, provider) -> {
        var name = provider.getBlockName(block);
        var horizontalModel = provider.models().getExistingFile(moonlitPath("block/%s".formatted(name)));
        provider.horizontalBlock(block, horizontalModel);
    });

    public static BlockStateSmith<MoonLightPyreBlock> PYRE = new BlockStateSmith<>(MoonLightPyreBlock.class, (block, provider) -> {
        var name = provider.getBlockName(block);
        provider.getVariantBuilder(block).forAllStates(blockState -> {
            int layer = blockState.getValue(MoonLightPyreBlock.LAYERS);
            boolean lit = blockState.getValue(MoonLightPyreBlock.LIT);
            var model = provider.models().getExistingFile(moonlitPath("block/%s_%s".formatted(name, layer + (lit ? "_lit" : ""))));
            return ConfiguredModel.builder().modelFile(model).build();
        });
    });

    public static BlockStateSmith<SnowLayerBlock> ASH_PILE = new BlockStateSmith<>(SnowLayerBlock.class, (block, provider) -> {
        var name = provider.getBlockName(block);
        provider.getVariantBuilder(block).forAllStates(blockState -> {
            int layer = blockState.getValue(SnowLayerBlock.LAYERS);
            ModelFile.ExistingModelFile model;
            if (layer < 8 ) model = provider.models().getExistingFile(moonlitPath("block/%s".formatted(name) + "height%s".formatted(layer)));
            else model = provider.models().getExistingFile(moonlitPath("block/moonlit_ash_block"));
            return ConfiguredModel.builder().modelFile(model).build();
        });
    });

    public static BlockStateSmith<BronzeBellBlock> BELL = new BlockStateSmith<>(BronzeBellBlock.class, (block, provider) -> {
        var name = provider.getBlockName(block);
        provider.horizontalBlock(block, (state) -> provider.models().getExistingFile(moonlitPath("block/%s/%s".formatted(name, state.getValue(BronzeBellBlock.ATTACHMENT).getSerializedName()))));
    });
}
