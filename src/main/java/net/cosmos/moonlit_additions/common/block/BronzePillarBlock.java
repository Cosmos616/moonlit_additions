package net.cosmos.moonlit_additions.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class BronzePillarBlock extends RotatedPillarBlock {
    public static final BooleanProperty SHINY = BooleanProperty.create("shiny");

    public BronzePillarBlock(BlockBehaviour.Properties properties) {
        super(properties);

        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(AXIS, Direction.Axis.Y)
                        .setValue(SHINY, false)
        );
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SHINY);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction.Axis axis = context.getClickedFace().getAxis();

        BlockState state = this.defaultBlockState()
                .setValue(AXIS, axis)
                .setValue(SHINY, false);

        return state.setValue(
                SHINY,
                shouldBeShiny(context.getLevel(), context.getClickedPos(), state)
        );
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        boolean shouldBeShiny = shouldBeShiny(level, pos, state);

        if (state.getValue(SHINY) != shouldBeShiny) {
            return state.setValue(SHINY, shouldBeShiny);
        }

        return state;
    }

    private static boolean shouldBeShiny(BlockGetter level, BlockPos pos, BlockState state) {
        Direction.Axis axis = state.getValue(AXIS);

        Direction positiveDirection = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
        Direction negativeDirection = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE);

        BlockState positiveNeighbor = level.getBlockState(pos.relative(positiveDirection));
        BlockState negativeNeighbor = level.getBlockState(pos.relative(negativeDirection));

        return isMatchingPillar(state, positiveNeighbor)
                && isMatchingPillar(state, negativeNeighbor);
    }

    private static boolean isMatchingPillar(BlockState selfState, BlockState otherState) {
        if (!otherState.is(selfState.getBlock())) {
            return false;
        }

        if (!otherState.hasProperty(AXIS)) {
            return false;
        }

        return otherState.getValue(AXIS) == selfState.getValue(AXIS);
    }
}
