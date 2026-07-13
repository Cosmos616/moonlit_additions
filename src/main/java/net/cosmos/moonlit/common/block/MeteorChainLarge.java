package net.cosmos.moonlit.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MeteorChainLarge extends ChainBlock {
    public MeteorChainLarge(Properties properties) {
        super(properties);
    }

    static final VoxelShape Y_AXIS_SHAPE;
    static final VoxelShape Z_AXIS_SHAPE;
    static final VoxelShape X_AXIS_SHAPE;

    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        switch ((Direction.Axis)state.getValue(AXIS)) {
            case X:
            default:
                return X_AXIS_SHAPE;
            case Z:
                return Z_AXIS_SHAPE;
            case Y:
                return Y_AXIS_SHAPE;
        }
    }

    static {
        Y_AXIS_SHAPE = Block.box(5F, 0.0F, 5F, 11F, 16.0F, 11F);
        Z_AXIS_SHAPE = Block.box(5F, 5F, 0.0F, 11F, 11F, 16.0F);
        X_AXIS_SHAPE = Block.box(0.0F, 5F, 5F, 16.0F, 11F, 11F);
    }

}
