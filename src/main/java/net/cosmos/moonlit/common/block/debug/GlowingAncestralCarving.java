package net.cosmos.moonlit.common.block.debug;

import com.farcr.nomansland.common.block.AncestralCarvingBlock;
import com.farcr.nomansland.common.block.CarvingFormation;
import com.mojang.serialization.MapCodec;
import net.cosmos.moonlit.common.block_entity.debug.GlowingAncestralCarvingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import team.lodestar.lodestone.modules.toolkit.block.LodestoneEntityBlock;

public class GlowingAncestralCarving extends LodestoneEntityBlock<GlowingAncestralCarvingBlockEntity> {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public GlowingAncestralCarving(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    public static final EnumProperty<CarvingFormation> FORMATION = EnumProperty.create("formation", CarvingFormation.class);
    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 3);

    protected MapCodec<GlowingAncestralCarving> codec() {
        return simpleCodec(GlowingAncestralCarving::new);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, FORMATION, ROTATION});
    }

    protected BlockState rotate(BlockState state, Rotation rotation) {
        if (rotation == Rotation.NONE) {
            return state;
        } else {
            Direction facing = (Direction)state.getValue(FACING);
            int rotValue = (Integer)state.getValue(ROTATION);
            if (facing.getAxis() == Direction.Axis.Y) {
                byte var10000;
                switch (rotation) {
                    case CLOCKWISE_90 -> var10000 = 1;
                    case CLOCKWISE_180 -> var10000 = 2;
                    case COUNTERCLOCKWISE_90 -> var10000 = 3;
                    default -> var10000 = 0;
                }

                int delta = var10000;
                rotValue = rotValue + delta & 3;
            }

            return (BlockState)((BlockState)state.setValue(FACING, rotation.rotate(facing))).setValue(ROTATION, rotValue);
        }
    }

    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos placedPos = context.getClickedPos();
        BlockState clicked = level.getBlockState(context.getClickedPos().relative(context.getClickedFace().getOpposite()));
        Direction facing;
        int rotation;
        if (clicked.getBlock() instanceof GlowingAncestralCarving) {
            facing = (Direction)clicked.getValue(FACING);
            rotation = (Integer)clicked.getValue(ROTATION);
        } else {
            float pitch = context.getPlayer() != null ? context.getPlayer().getXRot() : 0.0F;
            if (pitch > 60.0F) {
                facing = Direction.UP;
                rotation = this.getRotationForPlayer(context, facing);
            } else if (pitch < -60.0F) {
                facing = Direction.DOWN;
                rotation = this.getRotationForPlayer(context, facing);
            } else {
                facing = context.getHorizontalDirection().getOpposite();
                rotation = 0;
            }
        }

        return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, facing)).setValue(FORMATION, CarvingFormation.SINGLE)).setValue(ROTATION, rotation);
    }

    protected int getRotationForPlayer(BlockPlaceContext context, Direction facing) {
        if (context.getPlayer() == null) {
            return 0;
        } else if (facing == Direction.DOWN) {
            byte var3;
            switch (context.getHorizontalDirection()) {
                case NORTH -> var3 = 0;
                case EAST -> var3 = 1;
                case SOUTH -> var3 = 2;
                case WEST -> var3 = 3;
                default -> var3 = 0;
            }

            return var3;
        } else {
            byte var10000;
            switch (context.getHorizontalDirection()) {
                case NORTH -> var10000 = 2;
                case EAST -> var10000 = 3;
                case SOUTH -> var10000 = 0;
                case WEST -> var10000 = 1;
                default -> var10000 = 0;
            }

            return var10000;
        }
    }

    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide && !oldState.is(this)) {
            Direction facing = (Direction)state.getValue(FACING);
            int rotation = (Integer)state.getValue(ROTATION);
            Direction right = getPlaneRight(facing, rotation);
            Direction down = getPlaneDown(facing, rotation);
            if (!this.tryFormSize(level, pos, facing, rotation, right, down, 3)) {
                this.tryFormSize(level, pos, facing, rotation, right, down, 2);
            }
        }

    }

    private boolean tryFormSize(Level level, BlockPos origin, Direction facing, int rotation, Direction right, Direction down, int size) {
        for(int col = 0; col < size; ++col) {
            for(int row = 0; row < size; ++row) {
                BlockPos p = origin.relative(right, col).relative(down, row);
                BlockState s = level.getBlockState(p);
                if (!(s.getBlock() instanceof GlowingAncestralCarving)) {
                    return false;
                }

                if (s.getValue(FACING) != facing) {
                    return false;
                }

                if ((Integer)s.getValue(ROTATION) != rotation) {
                    return false;
                }
            }
        }

        for(int col = 0; col < size; ++col) {
            for(int row = 0; row < size; ++row) {
                BlockPos p = origin.relative(right, col).relative(down, row);
                CarvingFormation formation = CarvingFormation.getForPosition(size, col, row);
                level.setBlock(p, (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, facing)).setValue(FORMATION, formation)).setValue(ROTATION, rotation), 2);
            }
        }

        return true;
    }

    public static int[] rotateFormationCoords(int col, int row, int size, int rotation) {
        int[] var10000;
        switch (rotation) {
            case 0 -> var10000 = new int[]{size - 1 - col, size - 1 - row};
            case 1 -> var10000 = new int[]{size - 1 - row, col};
            case 2 -> var10000 = new int[]{col, row};
            case 3 -> var10000 = new int[]{row, size - 1 - col};
            default -> var10000 = new int[]{col, row};
        }

        return var10000;
    }

    private static Direction rotateCW(Direction dir) {
        Direction var10000;
        switch (dir) {
            case NORTH -> var10000 = Direction.EAST;
            case EAST -> var10000 = Direction.SOUTH;
            case SOUTH -> var10000 = Direction.WEST;
            case WEST -> var10000 = Direction.NORTH;
            default -> var10000 = dir;
        }

        return var10000;
    }

    public static Direction getPlaneRight(Direction facing, int rotation) {
        Direction var10000;
        switch (facing) {
            case NORTH -> var10000 = Direction.WEST;
            case EAST -> var10000 = Direction.NORTH;
            case SOUTH -> var10000 = Direction.EAST;
            case WEST -> var10000 = Direction.SOUTH;
            case UP -> var10000 = Direction.WEST;
            case DOWN -> var10000 = Direction.EAST;
            default -> throw new MatchException((String)null, (Throwable)null);
        }

        Direction right = var10000;

        for(int i = 0; i < rotation; ++i) {
            right = rotateCW(right);
        }

        return right;
    }

    public static Direction getPlaneDown(Direction facing, int rotation) {
        Direction var10000;
        switch (facing) {
            case NORTH:
            case EAST:
            case SOUTH:
            case WEST:
                var10000 = Direction.DOWN;
                break;
            case UP:
                var10000 = Direction.NORTH;
                break;
            case DOWN:
                var10000 = Direction.NORTH;
                break;
            default:
                throw new MatchException((String)null, (Throwable)null);
        }

        Direction down = var10000;

        for(int i = 0; i < rotation; ++i) {
            down = rotateCW(down);
        }

        return down;
    }

}