package net.cosmos.moonlit.network;

import net.cosmos.moonlit.Moonlit;
import net.cosmos.moonlit.common.block_entity.forge.light.AbstractLensBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SetLensAnglePayload(
        BlockPos pos,
        float pitch,
        float yaw
) implements CustomPacketPayload {
    public static final Type<SetLensAnglePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Moonlit.MOD_ID, "set_lens_angle"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetLensAnglePayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC,
                    SetLensAnglePayload::pos,
                    ByteBufCodecs.FLOAT,
                    SetLensAnglePayload::pitch,
                    ByteBufCodecs.FLOAT,
                    SetLensAnglePayload::yaw,
                    SetLensAnglePayload::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SetLensAnglePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            Level level = player.level();

            if (!level.isLoaded(payload.pos())) {
                return;
            }

            // Basic anti-cheat / validation.
            // Player must be close enough to interact with the lens.
            double maxDistance = player.blockInteractionRange() + 1.0D;
            double maxDistanceSq = maxDistance * maxDistance;

            if (player.distanceToSqr(
                    payload.pos().getX() + 0.5D,
                    payload.pos().getY() + 0.5D,
                    payload.pos().getZ() + 0.5D
            ) > maxDistanceSq) {
                return;
            }

            BlockEntity blockEntity = level.getBlockEntity(payload.pos());

            if (!(blockEntity instanceof AbstractLensBlockEntity lensBlockEntity)) {
                return;
            }

            float pitch = payload.pitch();
            float yaw = payload.yaw();

            // Use target angle for smooth server-owned movement.
            Moonlit.LOGGER.info(
                    "Server received SetLensAnglePayload at {} pitch={} yaw={}",
                    payload.pos(),
                    pitch,
                    yaw
            );

            lensBlockEntity.setAngle(new Vec2(pitch, yaw));

            // Optional: if you want it to lock instantly on release instead:
            // lensBlockEntity.setAngle(new Vec2(pitch, yaw));
        });
    }
}