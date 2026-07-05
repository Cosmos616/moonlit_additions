package net.cosmos.moonlit.client.rendering;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class LensSelectionShapes {
    private LensSelectionShapes() {}

    // The moving top piece: glass, frame, and side key handles.
    public static final VoxelShape LENS_HEAD = Shapes.or(
            // main tilted frame/lens region, before transform
            Block.box(-3D, 10D, 4D, 19D, 32D, 12D),

            // left side handle
            Block.box(-11.0D, 16D, 7D, -6D, 26D, 9D),

            // right side handle
            Block.box(22D, 16D, 7D, 27D, 26D, 9D)
    );

    // The yawing mount/body.
    public static final VoxelShape MIDDLE = Shapes.or(
            Block.box(-5D, 4D, 4D, 21D, 6.0D, 12.0D),
            Block.box(19D, 6D, 4D, 21D, 25D, 12D),
            Block.box(-5D, 6D, 4D, -3D, 25D, 12D)
    );
}