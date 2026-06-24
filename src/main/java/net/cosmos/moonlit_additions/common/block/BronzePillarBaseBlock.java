package net.cosmos.moonlit_additions.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BronzePillarBaseBlock extends DirectionalBlock {
    public static final MapCodec<BronzePillarBaseBlock> CODEC =
            simpleCodec(BronzePillarBaseBlock::new);
    public BronzePillarBaseBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.UP)
        );
    }

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getClickedFace());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape cube = Shapes.box(0,0,0,16,16,16);
        return switch (state.getValue(FACING)) {
            case DOWN -> Shapes.or(cube, Shapes.box(-2, 0, -2, 18, 14, 18));
            case UP -> Shapes.or(cube, Shapes.box(-2, 2, -2, 18, 16, 18));
            case NORTH -> Shapes.or(cube, Shapes.box(-2, -2, 2, 18, 18, 16));
            case SOUTH -> Shapes.or(cube, Shapes.box(-2, -2, 0, 18, 18, 14));
            case WEST -> Shapes.or(cube, Shapes.box(2, -2, -2, 16, 18, 18));
            case EAST -> Shapes.or(cube, Shapes.box(0, -2, -2, 14, 18, 18));
        };
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
