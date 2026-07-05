package net.cosmos.moonlit.client.rendering;

import net.cosmos.moonlit.common.block_entity.forge.light.AbstractLensBlockEntity;
import net.cosmos.moonlit.network.SetLensAnglePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.network.PacketDistributor;

public final class LensClientSelection {
    private LensClientSelection() {}

    public static BlockPos hoveredPos;
    public static LensHitPart hoveredPart = LensHitPart.NONE;

    public static BlockPos draggingPos;

    public static void clientTick() {
        Minecraft minecraft = Minecraft.getInstance();

        hoveredPos = null;
        hoveredPart = LensHitPart.NONE;

        if (minecraft.level == null || minecraft.player == null || minecraft.gameMode == null) {
            draggingPos = null;
            return;
        }

        float partialTick = minecraft.getTimer().getGameTimeDeltaPartialTick(true);

        updateHover(minecraft, partialTick);
        updateDrag(minecraft, partialTick);
    }

    private static void updateHover(Minecraft minecraft, float partialTick) {
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }

        double reach = minecraft.player.blockInteractionRange();

        Vec3 eye = minecraft.player.getEyePosition(partialTick);
        Vec3 look = minecraft.player.getViewVector(partialTick);
        Vec3 end = eye.add(look.scale(reach));

        // Search along the player's look ray.
        // Inflate gives room for lens parts that stick outside their block space.
        AABB searchBox = new AABB(eye, end).inflate(2.0D);

        LensPartHit bestHit = null;
        BlockPos bestPos = null;

        for (BlockPos pos : BlockPos.betweenClosed(
                Mth.floor(searchBox.minX),
                Mth.floor(searchBox.minY),
                Mth.floor(searchBox.minZ),
                Mth.floor(searchBox.maxX),
                Mth.floor(searchBox.maxY),
                Mth.floor(searchBox.maxZ)
        )) {
            if (!(minecraft.level.getBlockEntity(pos) instanceof AbstractLensBlockEntity lensBlockEntity)) {
                continue;
            }

            LensPartHit hit = LensSelectionRaycast.pickLensPart(
                    lensBlockEntity,
                    minecraft.player,
                    partialTick,
                    reach
            );

            if (hit == null) {
                continue;
            }

            if (bestHit == null || hit.distanceSq() < bestHit.distanceSq()) {
                bestHit = hit;
                bestPos = pos.immutable();
            }
        }

        if (bestHit != null) {
            hoveredPos = bestPos;
            hoveredPart = bestHit.part();
        }
    }

    private static void updateDrag(Minecraft minecraft, float partialTick) {
        // Start dragging when right click is held while hovering the lens head.
        if (draggingPos == null) {
            if (
                    minecraft.options.keyUse.isDown()
                            && hoveredPos != null
                            && hoveredPart == LensHitPart.LENS_HEAD
            ) {
                draggingPos = hoveredPos;

                minecraft.gui.setOverlayMessage(
                        Component.literal("Started dragging lens"),
                        false
                );
            }
        }

        if (draggingPos == null) {
            return;
        }

        // Stop dragging when right click is released.
        if (!minecraft.options.keyUse.isDown()) {
            if (minecraft.level != null
                    && minecraft.level.getBlockEntity(draggingPos) instanceof AbstractLensBlockEntity lensBlockEntity) {
                lensBlockEntity.clearClientPreviewAngle();
            }

            draggingPos = null;
            return;
        }

        if (minecraft.level == null || minecraft.player == null) {
            draggingPos = null;
            return;
        }

        if (!(minecraft.level.getBlockEntity(draggingPos) instanceof AbstractLensBlockEntity lensBlockEntity)) {
            draggingPos = null;
            return;
        }

        LensAimTarget target = LensAimTarget.fromPlayerLook(
                lensBlockEntity,
                minecraft.player,
                partialTick
        );

        // This is where it goes.
        // It updates the visual client-side preview every client tick while dragging.
        lensBlockEntity.setClientPreviewAngle(target.pitch(), target.yaw());

        minecraft.gui.setOverlayMessage(
                Component.literal("Dragging pitch=" + target.pitch() + " yaw=" + target.yaw()),
                false
        );

        // Later, once visual dragging works:
        PacketDistributor.sendToServer(
                new SetLensAnglePayload(
                        draggingPos,
                        target.pitch(),
                        target.yaw()
                )
        );
    }
}