package net.cosmos.moonlit.common.block_entity.forge.light;

import net.cosmos.moonlit.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BronzeMirrorBlockEntity extends AbstractLensBlockEntity {

    public BronzeMirrorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BRONZE_MIRROR.get(), pos, state);
    }

    @Override
    public void commonTick(Level level) {
        super.commonTick(level);
        if (getLightBeam() == null) {
            setLightBeam(new LightBeam(worldPosition, level));
        }
    }
}
