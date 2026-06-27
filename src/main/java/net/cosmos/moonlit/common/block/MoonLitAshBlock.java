package net.cosmos.moonlit.common.block;

import com.mojang.serialization.MapCodec;
import net.cosmos.moonlit.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ItemAbilities;

public class MoonLitAshBlock extends Block {
    public static final MapCodec<MoonLitAshBlock> CODEC = simpleCodec(MoonLitAshBlock::new);

    public MoonLitAshBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }


    @Override
    public boolean isFireSource(BlockState state, LevelReader level, BlockPos pos, Direction side) {
        // Makes it act like netherrack/soul sand as a permanent fire source.
        return side == Direction.UP;
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
        if (hitResult.getDirection() != Direction.UP) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!stack.canPerformAction(ItemAbilities.FIRESTARTER_LIGHT)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        BlockPos firePos = pos.above();

        if (!level.getBlockState(firePos).canBeReplaced()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        level.setBlock(
                firePos,
                ModBlocks.MOONLIT_FIRE.get().defaultBlockState(),
                Block.UPDATE_ALL
        );

        level.playSound(
                null,
                firePos,
                SoundEvents.FLINTANDSTEEL_USE,
                SoundSource.BLOCKS,
                1.0F,
                level.random.nextFloat() * 0.4F + 0.8F
        );

        if (!level.isClientSide && !player.getAbilities().instabuild) {
            if (stack.isDamageableItem()) {
                stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(stack));
            } else {
                // This covers fire-charge-like consumable starters.
                stack.shrink(1);
            }
        }

        return ItemInteractionResult.SUCCESS;
    }
}
