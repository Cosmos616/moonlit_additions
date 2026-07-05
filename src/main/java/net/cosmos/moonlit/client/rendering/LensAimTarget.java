package net.cosmos.moonlit.client.rendering;

import net.cosmos.moonlit.common.block_entity.forge.light.AbstractLensBlockEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public record LensAimTarget(float pitch, float yaw) {
    public static LensAimTarget fromPlayerLook(
            AbstractLensBlockEntity lensBlockEntity,
            Player player,
            float partialTick
    ) {
        Vec3 look = player.getViewVector(partialTick).normalize();

        float facingYaw = lensBlockEntity.getFacing().toYRot() +90;

        // Convert the player's world look direction into the lens's local space.
        Vec3 localLook = rotateY(look, facingYaw);

        float yaw = (float) -Math.toDegrees(Math.atan2(localLook.x, -localLook.z));

        float horizontalLength = (float) Math.sqrt(
                localLook.x * localLook.x + localLook.z * localLook.z
        );

        float pitch = (float) Math.toDegrees(Math.atan2(localLook.y, horizontalLength));

//        yaw = Mth.clamp(yaw, -70.0F, 70.0F);
//        pitch = Mth.clamp(pitch, -55.0F, 55.0F);

        return new LensAimTarget(pitch, yaw);
    }

    private static Vec3 rotateY(Vec3 vec, float degrees) {
        double radians = Math.toRadians(degrees);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        double x = vec.x * cos - vec.z * sin;
        double z = vec.x * sin + vec.z * cos;

        return new Vec3(x, vec.y, z);
    }
}