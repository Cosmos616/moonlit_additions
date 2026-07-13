package net.cosmos.moonlit.common.block.dream.reflection;

import net.cosmos.moonlit.common.attachment.Reflection;
import net.cosmos.moonlit.common.block_entity.dream.reflection.PuddleBlockEntity;
import net.cosmos.moonlit.init.ModAttachmentTypes;
import net.cosmos.moonlit.network.ToggleReflectionShaderPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import team.lodestar.lodestone.modules.toolkit.block.LodestoneEntityBlock;

import java.util.Optional;

public class PuddleBlock extends LodestoneEntityBlock<PuddleBlockEntity> {

    public PuddleBlock(Properties properties) {
        super(properties);
    }

    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (player.hasData(ModAttachmentTypes.REFLECTION)) {
                PacketDistributor.sendToPlayer(serverPlayer, new ToggleReflectionShaderPayload(true));
                player.removeData(ModAttachmentTypes.REFLECTION);
            } else {
                player.setData(ModAttachmentTypes.REFLECTION, Reflection.create());
                PacketDistributor.sendToPlayer(serverPlayer, new ToggleReflectionShaderPayload(false));
            }
        }

        return InteractionResult.SUCCESS;
    }
}
