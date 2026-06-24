package net.cosmos.moonlit_additions.block;

import com.mojang.serialization.MapCodec;
import net.cosmos.moonlit_additions.item.ModItems;
import net.cosmos.moonlit_additions.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class MeteorBlock extends Block {
    public static final MapCodec<MeteorBlock> CODEC = simpleCodec(MeteorBlock::new);

    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    public static final BooleanProperty PERSISTENT = BooleanProperty.create("persistent");

    private static final int COOL_DOWN_TICKS = 20 * 60 * 5; // 5 minutes

    public MeteorBlock(BlockBehaviour.Properties properties) {
        super(properties);

        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(LIT, false)
                        .setValue(PERSISTENT, false)
        );
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, PERSISTENT);
    }

    @Override
    protected void onPlace(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState oldState,
            boolean movedByPiston
    ) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        if (!level.isClientSide && state.getValue(LIT) && !state.getValue(PERSISTENT)) {
            level.scheduleTick(pos, this, COOL_DOWN_TICKS);
        }
    }

    @Override
    protected void tick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random
    ) {
        if (state.getValue(LIT) && !state.getValue(PERSISTENT)) {
            level.setBlock(
                    pos,
                    state.setValue(LIT, false),
                    Block.UPDATE_ALL
            );

            level.playSound(
                    null,
                    pos,
                    SoundEvents.FIRE_EXTINGUISH,
                    SoundSource.BLOCKS,
                    0.6F,
                    0.8F + random.nextFloat() * 0.3F
            );
        }
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
        boolean isStardust = stack.is(ModItems.STARDUST.get());
        boolean isStardustBottle = stack.is(ModItems.BOTTLE_OF_STARDUST.get());

        if (!isStardust && !isStardustBottle) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // Already permanently lit; don't waste the item.
        if (state.getValue(LIT) && state.getValue(PERSISTENT)) {
            return ItemInteractionResult.SUCCESS;
        }

        level.setBlock(
                pos,
                state.setValue(LIT, true).setValue(PERSISTENT, true),
                Block.UPDATE_ALL
        );

        level.playSound(
                null,
                pos,
                SoundEvents.AMETHYST_BLOCK_CHIME,
                SoundSource.BLOCKS,
                1.0F,
                0.7F + level.random.nextFloat() * 0.4F
        );

        if (!level.isClientSide && !player.getAbilities().instabuild) {
            if (isStardust) {
                stack.shrink(1);
            } else {
                consumeBottleAndReturnGlassBottle(stack, player, hand);
            }
        }

        return ItemInteractionResult.SUCCESS;
    }

    private static void consumeBottleAndReturnGlassBottle(
            ItemStack stack,
            Player player,
            InteractionHand hand
    ) {
        stack.shrink(1);

        ItemStack glassBottle = new ItemStack(Items.GLASS_BOTTLE);

        if (stack.isEmpty()) {
            player.setItemInHand(hand, glassBottle);
        } else if (!player.getInventory().add(glassBottle)) {
            player.drop(glassBottle, false);
        }
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

        if (random.nextFloat() > 0.45F) {
            return;
        }

        double x = pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 1.2D;
        double y = pos.getY() + 0.75D + random.nextDouble() * 0.6D;
        double z = pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 1.2D;

        double xSpeed = (random.nextDouble() - 0.5D) * 0.015D;
        double ySpeed = 0.015D + random.nextDouble() * 0.025D;
        double zSpeed = (random.nextDouble() - 0.5D) * 0.015D;

        level.addParticle(
                ModParticles.METEOR_AURORA.get(),
                x,
                y,
                z,
                xSpeed,
                ySpeed,
                zSpeed
        );
    }

    /**
     * Use this when your meteor crash logic places the block.
     * It starts lit, but not persistent, so it will cool down.
     */
    public static BlockState crashingMeteorState(BlockState baseState) {
        return baseState
                .setValue(LIT, true)
                .setValue(PERSISTENT, false);
    }
}
