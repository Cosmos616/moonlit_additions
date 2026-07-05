package net.cosmos.moonlit.client.rendering;

import net.minecraft.world.phys.BlockHitResult;

public record LensPartHit(
        LensHitPart part,
        BlockHitResult hitResult,
        double distanceSq
) {
}