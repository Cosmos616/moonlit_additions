package net.cosmos.moonlit.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class WrappedBeamBlock extends Block {
    public static final MapCodec<WrappedBeamBlock> CODEC = simpleCodec(WrappedBeamBlock::new);

    public static final EnumProperty<Direction.Axis> AXIS =
            EnumProperty.create("axis", Direction.Axis.class, Direction.Axis.X, Direction.Axis.Z);

    public static final EnumProperty<BeamPart> PART =
            EnumProperty.create("part", BeamPart.class);

    private static final VoxelShape SHAPE_Z = Block.box(
            4.0D, 3.0D, 0.0D,
            12.0D, 13.0D, 16.0D
    );

    private static final VoxelShape SHAPE_X = Block.box(
            0.0D, 3.0D, 4.0D,
            16.0D, 13.0D, 12.0D
    );

    // Optional fuller collision/outline for cross pieces.
    private static final VoxelShape SHAPE_CROSS = Block.box(
            0.0D, 3.0D, 0.0D,
            16.0D, 13.0D, 16.0D
    );

    public WrappedBeamBlock(BlockBehaviour.Properties properties) {
        super(properties);

        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(AXIS, Direction.Axis.Z)
                        .setValue(PART, BeamPart.SINGLE)
        );
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, PART);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction.Axis axis = getPlacementAxis(context);
        BeamPart part = getPartFor(context.getLevel(), context.getClickedPos(), axis);

        return this.defaultBlockState()
                .setValue(AXIS, axis)
                .setValue(PART, part);
    }

    private Direction.Axis getPlacementAxis(BlockPlaceContext context) {
        // This no longer checks neighbors.
        // It only uses the direction the player is placing/facing.
        Direction.Axis axis = context.getHorizontalDirection().getAxis();

        // If somehow vertical, fallback to Z.
        if (axis == Direction.Axis.Y) {
            return Direction.Axis.Z;
        }

        return axis;
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        // Any horizontal neighbor can affect single/cross detection.
        if (direction.getAxis() == Direction.Axis.Y) {
            return state;
        }

        Direction.Axis axis = state.getValue(AXIS);
        BeamPart newPart = getPartFor(level, pos, axis);

        return state.setValue(PART, newPart);
    }

    private BeamPart getPartFor(BlockGetter level, BlockPos pos, Direction.Axis axis) {
        boolean north = isBeam(level, pos.north());
        boolean south = isBeam(level, pos.south());
        boolean west = isBeam(level, pos.west());
        boolean east = isBeam(level, pos.east());
//
//        int neighborCount = 0;
//
//        if (north) neighborCount++;
//        if (south) neighborCount++;
//        if (west) neighborCount++;
//        if (east) neighborCount++;
//
//        if (neighborCount == 0) {
//            return BeamPart.SINGLE;
//        }

        Direction negativeDir = axis == Direction.Axis.X ? Direction.WEST : Direction.NORTH;
        Direction positiveDir = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;

        boolean hasNegative = isConnectedAlongAxis(level, pos, negativeDir, axis);
        boolean hasPositive = isConnectedAlongAxis(level, pos, positiveDir, axis);

        if (axis == Direction.Axis.X) {
            if (north && south){
                return BeamPart.CROSS;
            }
        }

        if (axis == Direction.Axis.Z) {
            if (east && west){
                return BeamPart.CROSS;
            }
        }

        if (hasNegative && hasPositive) {
            return BeamPart.CENTER;
        }

        if (!hasNegative && !hasPositive){
            return BeamPart.SINGLE;
        }

        if (hasNegative) {
            return BeamPart.POSITIVE;
        }

        if (hasPositive) {
            return BeamPart.NEGATIVE;
        }

        // If it has neighbors, but not along this beam's axis,
        // treat it like a cross/junction so it can use a special model.
        return BeamPart.SINGLE;
    }

    private boolean isBeam(BlockGetter level, BlockPos pos) {
        return level.getBlockState(pos).is(this);
    }

    private boolean isConnectedAlongAxis(BlockGetter level, BlockPos pos, Direction direction, Direction.Axis axis) {
        BlockState other = level.getBlockState(pos.relative(direction));
        return other.is(this) && other.getValue(AXIS) == axis;
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        if (state.getValue(PART) == BeamPart.CROSS) {
            return SHAPE_CROSS;
        }

        return state.getValue(AXIS) == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
    }

    @Override
    protected VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        if (state.getValue(PART) == BeamPart.CROSS) {
            return SHAPE_CROSS;
        }

        return state.getValue(AXIS) == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        if (rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90) {
            Direction.Axis axis = state.getValue(AXIS);
            return state.setValue(AXIS, axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X);
        }

        return state;
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        BeamPart part = state.getValue(PART);

        if (part == BeamPart.CENTER || part == BeamPart.SINGLE || part == BeamPart.CROSS) {
            return state;
        }

        Direction.Axis axis = state.getValue(AXIS);

        if (mirror == Mirror.FRONT_BACK && axis == Direction.Axis.Z) {
            return state.setValue(PART, swapPart(part));
        }

        if (mirror == Mirror.LEFT_RIGHT && axis == Direction.Axis.X) {
            return state.setValue(PART, swapPart(part));
        }

        return state;
    }

    private BeamPart swapPart(BeamPart part) {
        if (part == BeamPart.NEGATIVE) {
            return BeamPart.POSITIVE;
        }

        if (part == BeamPart.POSITIVE) {
            return BeamPart.NEGATIVE;
        }

        return part;
    }

    public enum BeamPart implements StringRepresentable {
        SINGLE("single"),
        NEGATIVE("negative"),
        CENTER("center"),
        POSITIVE("positive"),
        CROSS("cross");

        private final String name;

        BeamPart(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}