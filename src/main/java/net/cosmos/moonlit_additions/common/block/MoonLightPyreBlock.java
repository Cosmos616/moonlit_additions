package net.cosmos.moonlit_additions.common.block;

import com.farcr.nomansland.common.registry.NMLParticleTypes;
import com.mojang.serialization.MapCodec;
import net.cosmos.moonlit_additions.common.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MoonLightPyreBlock extends Block {
    public static final MapCodec<MoonLightPyreBlock> CODEC = simpleCodec(MoonLightPyreBlock::new);

    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, 5);

    private static final int BURN_INTERVAL_TICKS = 20 * 10; // 20 seconds per layer

    private static final VoxelShape ONE_LAYER_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
    private static final VoxelShape TWO_LAYER_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 8.0D, 14.0D);
    private static final VoxelShape THREE_LAYER_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);
    private static final VoxelShape FOUR_LAYER_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    public MoonLightPyreBlock(BlockBehaviour.Properties properties) {
        super(properties);

        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(LIT, false)
                        .setValue(LAYERS, 5)
        );
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, LAYERS);
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return switch (state.getValue(LAYERS)) {
            case 1 -> ONE_LAYER_SHAPE;
            case 2 -> TWO_LAYER_SHAPE;
            case 3 -> THREE_LAYER_SHAPE;
            default -> FOUR_LAYER_SHAPE;
        };
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        if (state.getValue(LIT)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!(stack.getItem() instanceof FlintAndSteelItem) && !(stack.getItem() instanceof FireChargeItem)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // Only the top exposed pyre can be lit.
        if (!isTopExposed(level, pos)) {
            return ItemInteractionResult.CONSUME;
        }

        level.setBlock(pos, state.setValue(LIT, true).setValue(LAYERS, 4), Block.UPDATE_ALL);
        level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);

        if (!level.isClientSide) {
            level.scheduleTick(pos, this, BURN_INTERVAL_TICKS);
        }

        if (!player.getAbilities().instabuild) {
            if (stack.getItem() instanceof FlintAndSteelItem) {
                stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(stack));
            } else if (stack.getItem() instanceof FireChargeItem) {
                stack.shrink(1);
            }
        }

        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected void tick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random
    ) {
        if (!state.getValue(LIT)) {
            return;
        }

        // If another pyre/block is above it now, stop burning.
        if (!isTopExposed(level, pos)) {
            level.setBlock(pos, state.setValue(LIT, false), Block.UPDATE_ALL);
            return;
        }

        burnOneLayer(state, level, pos);
    }

    private void burnOneLayer(BlockState state, ServerLevel level, BlockPos pos) {
        int layers = state.getValue(LAYERS);

        Block.popResource(
                level,
                pos,
                new ItemStack(ModItems.MOONLIT_ASH.get())
        );

        level.playSound(
                null,
                pos,
                SoundEvents.CANDLE_EXTINGUISH,
                SoundSource.BLOCKS,
                0.7F,
                0.8F + level.random.nextFloat() * 0.2F
        );

        lightSparkAnimation(state, level, pos, level.random);

        if (layers > 1) {
            BlockState newState = state.setValue(LAYERS, layers - 1);

            level.setBlock(pos, newState, Block.UPDATE_ALL);
            level.scheduleTick(pos, this, BURN_INTERVAL_TICKS);
        } else {
            level.removeBlock(pos, false);

            BlockPos belowPos = pos.below();
            BlockState belowState = level.getBlockState(belowPos);

            if (belowState.is(this) && !belowState.getValue(LIT) && isTopExposed(level, belowPos)) {
                level.setBlock(belowPos, belowState.setValue(LIT, true).setValue(LAYERS, 4), Block.UPDATE_ALL);
                level.scheduleTick(belowPos, this, BURN_INTERVAL_TICKS);

                level.playSound(
                        null,
                        belowPos,
                        SoundEvents.CANDLE_EXTINGUISH,
                        SoundSource.BLOCKS,
                        0.8F,
                        1.2F
                );
            }
        }
    }

    public void lightSparkAnimation(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {

        for(int i = 0; i < random.nextInt(4, 8); ++i) {
            level.sendParticles(
                    NMLParticleTypes.MOONLIGHT_SPARK.get(),
                    pos.getX() + 0.5D,
                    pos.getY() + 0.5D,
                    pos.getZ() + 0.5D,
                    8,      // count
                    0.15D,  // x spread
                    0.08D,  // y spread
                    0.15D,  // z spread
                    0.01D   // speed
            );
        }
    }


    private static boolean isTopExposed(BlockGetter level, BlockPos pos) {
        BlockState aboveState = level.getBlockState(pos.above());

        // This makes only the highest pyre in a vertical stack burn.
        // It also prevents burning if any solid-ish block is directly above.
        return aboveState.isAir() || !aboveState.isFaceSturdy(level, pos.above(), Direction.DOWN);
    }

    @Override
    public void animateTick(
            BlockState state,
            Level level,
            BlockPos pos,
            RandomSource random
    ) {
        if (!state.getValue(LIT)) {
            return;
        }

        if (!isTopExposed(level, pos)) {
            return;
        }

        int layers = state.getValue(LAYERS);

        double x = pos.getX() + 0.5D;
        double z = pos.getZ() + 0.5D;

        // Wick height moves down as layers burn away.
        double y = pos.getY() + switch (layers) {
            case 1 -> 0.58D;
            case 2 -> 0.73D;
            case 3 -> 0.98D;
            default -> 1.23D;
        };

        if (random.nextFloat() < 0.25F) {
            level.addParticle(
                    net.minecraft.core.particles.ParticleTypes.SMOKE,
                    x,
                    y,
                    z,
                    0.0D,
                    0.02D,
                    0.0D
            );
        }
        level.addParticle(
                NMLParticleTypes.MOONLIGHT_FLAME.get(),
                x,
                y,
                z,
                0.0D,
                0.0D,
                0.0D
        );
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
        if (state.getValue(LIT) && direction == Direction.UP && !isTopExposed(level, pos)) {
            return state.setValue(LIT, false);
        }

        return state;
    }
}
