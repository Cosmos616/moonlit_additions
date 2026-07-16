package net.cosmos.moonlit.common.block_entity.dream.reflection;

import net.cosmos.moonlit.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntity;

public class PuddleBlockEntity extends LodestoneBlockEntity {
    public PuddleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PUDDLE.get(), pos, state);
    }
}
