package net.cosmos.moonlit_additions.common.block;

import com.mojang.serialization.MapCodec;
import net.cosmos.moonlit_additions.common.block_entity.BronzeBellBlockEntity;
import net.cosmos.moonlit_additions.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public class BronzeBellBlock extends BaseEntityBlock {
    public static final MapCodec<BellBlock> CODEC = simpleCodec(BellBlock::new);
    public static final DirectionProperty FACING;
    public static final EnumProperty<BellAttachType> ATTACHMENT;
    public static final BooleanProperty POWERED;
    private static final VoxelShape NORTH_SOUTH_FLOOR_SHAPE;
    private static final VoxelShape EAST_WEST_FLOOR_SHAPE;
    private static final VoxelShape BELL_SHAPE;
    private static final VoxelShape NORTH_SOUTH_BETWEEN;
    private static final VoxelShape EAST_WEST_BETWEEN;
    private static final VoxelShape TO_WEST;
    private static final VoxelShape TO_EAST;
    private static final VoxelShape TO_NORTH;
    private static final VoxelShape TO_SOUTH;
    private static final VoxelShape CEILING_SHAPE;
    public static final int EVENT_BELL_RING = 1;

    public MapCodec<BellBlock> codec() {
        return CODEC;
    }

    public BronzeBellBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((((this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(ATTACHMENT, BellAttachType.FLOOR)).setValue(POWERED, false));
    }

    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        boolean flag = level.hasNeighborSignal(pos);
        if (flag != state.getValue(POWERED)) {
            if (flag) {
                this.attemptToRing(level, pos, null);
            }

            level.setBlock(pos, state.setValue(POWERED, flag), 3);
        }

    }

    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        Entity entity = projectile.getOwner();
        Player player = entity instanceof Player ? (Player)entity : null;
        this.onHit(level, state, hit, player, true);
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return this.onHit(level, state, hitResult, player, true) ? InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.PASS;
    }



    public boolean onHit(Level level, BlockState state, BlockHitResult result, @Nullable Player player, boolean canRingBell) {
        Direction direction = result.getDirection();
        BlockPos blockpos = result.getBlockPos();
        boolean flag = !canRingBell || this.isProperHit(state, direction, result.getLocation().y - (double)blockpos.getY());
        if (flag) {
            boolean flag1 = this.attemptToRing(player, level, blockpos, direction);
            if (flag1 && player != null) {
                player.awardStat(Stats.BELL_RING);
            }

            return true;
        } else {
            return false;
        }
    }

    private boolean isProperHit(BlockState pos, Direction p_direction, double distanceY) {
        if (p_direction.getAxis() != Direction.Axis.Y && !(distanceY > (double)0.8124F)) {
            Direction direction = pos.getValue(FACING);
            BellAttachType bellattachtype = pos.getValue(ATTACHMENT);
            switch (bellattachtype) {
                case FLOOR:
                    return direction.getAxis() == p_direction.getAxis();
                case SINGLE_WALL:
                case DOUBLE_WALL:
                    return direction.getAxis() != p_direction.getAxis();
                case CEILING:
                    return true;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }
    public boolean attemptToRing(Level level, BlockPos pos, @Nullable Direction direction) {
        return this.attemptToRing(null, level, pos, direction);
    }

    public boolean attemptToRing(@Nullable Entity entity, Level level, BlockPos pos, @Nullable Direction direction) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (!level.isClientSide && blockentity instanceof BronzeBellBlockEntity) {
            if (direction == null) {
                direction = level.getBlockState(pos).getValue(FACING);
            }

            ((BronzeBellBlockEntity)blockentity).onHit(direction);
            level.playSound(null, pos, SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 2.0F, 0.7F);
            level.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
            return true;
        } else {
            return false;
        }
    }

    private VoxelShape getVoxelShape(BlockState state) {
        Direction direction = state.getValue(FACING);
        BellAttachType bellattachtype = state.getValue(ATTACHMENT);
        if (bellattachtype == BellAttachType.FLOOR) {
            return direction != Direction.NORTH && direction != Direction.SOUTH ? EAST_WEST_FLOOR_SHAPE : NORTH_SOUTH_FLOOR_SHAPE;
        } else if (bellattachtype == BellAttachType.CEILING) {
            return CEILING_SHAPE;
        } else if (bellattachtype != BellAttachType.DOUBLE_WALL) {
            if (direction == Direction.NORTH) {
                return TO_NORTH;
            } else if (direction == Direction.SOUTH) {
                return TO_SOUTH;
            } else {
                return direction == Direction.EAST ? TO_EAST : TO_WEST;
            }
        } else {
            return direction != Direction.NORTH && direction != Direction.SOUTH ? EAST_WEST_BETWEEN : NORTH_SOUTH_BETWEEN;
        }
    }

    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.getVoxelShape(state);
    }

    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.getVoxelShape(state);
    }

    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getClickedFace();
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        Direction.Axis direction$axis = direction.getAxis();
        if (direction$axis == Direction.Axis.Y) {
            BlockState blockstate = this.defaultBlockState().setValue(ATTACHMENT, direction == Direction.DOWN ? BellAttachType.CEILING : BellAttachType.FLOOR).setValue(FACING, context.getHorizontalDirection());
            if (blockstate.canSurvive(context.getLevel(), blockpos)) {
                return blockstate;
            }
        } else {
            boolean flag = direction$axis == Direction.Axis.X && level.getBlockState(blockpos.west()).isFaceSturdy(level, blockpos.west(), Direction.EAST) && level.getBlockState(blockpos.east()).isFaceSturdy(level, blockpos.east(), Direction.WEST) || direction$axis == Direction.Axis.Z && level.getBlockState(blockpos.north()).isFaceSturdy(level, blockpos.north(), Direction.SOUTH) && level.getBlockState(blockpos.south()).isFaceSturdy(level, blockpos.south(), Direction.NORTH);
            BlockState blockstate1 = this.defaultBlockState().setValue(FACING, direction.getOpposite()).setValue(ATTACHMENT, flag ? BellAttachType.DOUBLE_WALL : BellAttachType.SINGLE_WALL);
            if (blockstate1.canSurvive(context.getLevel(), context.getClickedPos())) {
                return blockstate1;
            }

            boolean flag1 = level.getBlockState(blockpos.below()).isFaceSturdy(level, blockpos.below(), Direction.UP);
            blockstate1 = blockstate1.setValue(ATTACHMENT, flag1 ? BellAttachType.FLOOR : BellAttachType.CEILING);
            if (blockstate1.canSurvive(context.getLevel(), context.getClickedPos())) {
                return blockstate1;
            }
        }

        return null;
    }

    protected void onExplosionHit(BlockState state, Level level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> dropConsumer) {
        if (explosion.canTriggerBlocks()) {
            this.attemptToRing(level, pos, null);
        }

        super.onExplosionHit(state, level, pos, explosion, dropConsumer);
    }

    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        BellAttachType bellattachtype = state.getValue(ATTACHMENT);
        Direction direction = getConnectedDirection(state).getOpposite();
        if (direction == facing && !state.canSurvive(level, currentPos) && bellattachtype != BellAttachType.DOUBLE_WALL) {
            return Blocks.AIR.defaultBlockState();
        } else {
            if (facing.getAxis() == state.getValue(FACING).getAxis()) {
                if (bellattachtype == BellAttachType.DOUBLE_WALL && !facingState.isFaceSturdy(level, facingPos, facing)) {
                    return state.setValue(ATTACHMENT, BellAttachType.SINGLE_WALL).setValue(FACING, facing.getOpposite());
                }

                if (bellattachtype == BellAttachType.SINGLE_WALL && direction.getOpposite() == facing && facingState.isFaceSturdy(level, facingPos, state.getValue(FACING))) {
                    return state.setValue(ATTACHMENT, BellAttachType.DOUBLE_WALL);
                }
            }

            return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        }
    }

    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = getConnectedDirection(state).getOpposite();
        return direction == Direction.UP ? Block.canSupportCenter(level, pos.above(), Direction.DOWN) : FaceAttachedHorizontalDirectionalBlock.canAttach(level, pos, direction);
    }

    private static Direction getConnectedDirection(BlockState state) {
        switch (state.getValue(ATTACHMENT)) {
            case FLOOR -> {
                return Direction.UP;
            }
            case CEILING -> {
                return Direction.DOWN;
            }
            default -> {
                return state.getValue(FACING).getOpposite();
            }
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ATTACHMENT, POWERED);
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BronzeBellBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> blockEntityType
    ) {
        return createTickerHelper(
                blockEntityType,
                ModBlockEntities.BRONZE_BELL_BLOCK_ENTITY.get(),
                BronzeBellBlockEntity::tick
        );
    }


    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        ATTACHMENT = BlockStateProperties.BELL_ATTACHMENT;
        POWERED = BlockStateProperties.POWERED;
        NORTH_SOUTH_FLOOR_SHAPE = Block.box(0.0F, 0.0F, 4.0F, 16.0F, 16.0F, 12.0F);
        EAST_WEST_FLOOR_SHAPE = Block.box(4.0F, 0.0F, 0.0F, 12.0F, 16.0F, 16.0F);
        BELL_SHAPE = Shapes.or(Block.box(2, 1, 2, 14, 3, 14), Block.box(3, 3, 3, 13, 12, 13), Block.box(4, 12, 4, 12, 13, 12));
        NORTH_SOUTH_BETWEEN = Shapes.or(BELL_SHAPE, Block.box(7.0F, 13.0F, 0.0F, 9.0F, 15.0F, 16.0F));
        EAST_WEST_BETWEEN = Shapes.or(BELL_SHAPE, Block.box(0.0F, 13.0F, 7.0F, 16.0F, 15.0F, 9.0F));
        TO_WEST = Shapes.or(BELL_SHAPE, Block.box(0.0F, 13.0F, 7.0F, 13.0F, 15.0F, 9.0F));
        TO_EAST = Shapes.or(BELL_SHAPE, Block.box(3.0F, 13.0F, 7.0F, 16.0F, 15.0F, 9.0F));
        TO_NORTH = Shapes.or(BELL_SHAPE, Block.box(7.0F, 13.0F, 0.0F, 9.0F, 15.0F, 13.0F));
        TO_SOUTH = Shapes.or(BELL_SHAPE, Block.box(7.0F, 13.0F, 3.0F, 9.0F, 15.0F, 16.0F));
        CEILING_SHAPE = Shapes.or(BELL_SHAPE, Block.box(7.0F, 13.0F, 7.0F, 9.0F, 16.0F, 9.0F));
    }
}
