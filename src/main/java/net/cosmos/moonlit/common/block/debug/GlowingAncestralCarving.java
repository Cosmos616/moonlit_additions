package net.cosmos.moonlit.common.block.debug;

import net.cosmos.moonlit.common.block_entity.debug.GlowingAncestralCarvingBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import team.lodestar.lodestone.modules.toolkit.block.LodestoneEntityBlock;

public class GlowingAncestralCarving extends LodestoneEntityBlock<GlowingAncestralCarvingBlockEntity> {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public GlowingAncestralCarving(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
