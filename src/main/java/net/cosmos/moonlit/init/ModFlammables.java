package net.cosmos.moonlit.init;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;

public class ModFlammables {
    public static void register() {
        FireBlock fireBlock = (FireBlock) Blocks.FIRE;

        ModBlocks.WOODSETS.forEach(ModBlocks.Woodset::setFlammables);
    }
}
