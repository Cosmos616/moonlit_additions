package net.cosmos.moonlit.common.block_entity.forge;

import net.cosmos.moonlit.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntity;

public class ManufacturedSunBlockEntity extends LodestoneBlockEntity {

    public ManufacturedSunBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MANUFACTURED_SUN.get(), pos, state);
    }
}
