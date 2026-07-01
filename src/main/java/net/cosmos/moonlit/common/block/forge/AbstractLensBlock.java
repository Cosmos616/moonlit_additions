package net.cosmos.moonlit.common.block.forge;

import net.cosmos.moonlit.common.block_entity.forge.light.AbstractLensBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import team.lodestar.lodestone.modules.toolkit.block.WaterLoggedEntityBlock;

public abstract class AbstractLensBlock<T extends AbstractLensBlockEntity> extends WaterLoggedEntityBlock<T> {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    private static final VoxelShape SHAPE_D = Block.box(0, 12, 0, 16, 16, 16);
    private static final VoxelShape SHAPE_U = Block.box(0, 0, 0, 16, 4, 16);
    private static final VoxelShape SHAPE_N = Block.box(0, 0, 12, 16, 16, 16);
    private static final VoxelShape SHAPE_E = Block.box(0, 0, 0, 4, 16, 16);
    private static final VoxelShape SHAPE_S = Block.box(0, 0, 0, 16, 16, 4);
    private static final VoxelShape SHAPE_W = Block.box(12, 0, 0, 16, 16, 16);

    public AbstractLensBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case DOWN -> SHAPE_D;
            case UP -> SHAPE_U;
            case NORTH -> SHAPE_N;
            case SOUTH -> SHAPE_S;
            case WEST -> SHAPE_W;
            case EAST -> SHAPE_E;
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(FACING, context.getClickedFace());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }
}
