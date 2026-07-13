package net.cosmos.moonlit.common.block.dream.reflection;

import net.cosmos.moonlit.common.attachment.Reflection;
import net.cosmos.moonlit.common.block_entity.dream.reflection.PuddleBlockEntity;
import net.cosmos.moonlit.init.ModAttachmentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import team.lodestar.lodestone.modules.toolkit.block.LodestoneEntityBlock;

public class PuddleBlock extends LodestoneEntityBlock<PuddleBlockEntity> {

    public PuddleBlock(Properties properties) {
        super(properties);
    }

    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.hasData(ModAttachmentTypes.REFLECTION)) {
            player.removeData(ModAttachmentTypes.REFLECTION);
        } else {
            player.setData(ModAttachmentTypes.REFLECTION, Reflection.create());
        }
        return InteractionResult.SUCCESS;
    }
}
