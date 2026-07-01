package net.cosmos.moonlit.common.block_entity.forge.light;

import net.cosmos.moonlit.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BronzeLensBlockEntity extends AbstractLensBlockEntity {

    public BronzeLensBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BRONZE_LENS.get(), pos, state);
    }
}
