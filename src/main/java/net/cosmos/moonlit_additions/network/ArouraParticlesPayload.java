package net.cosmos.moonlit_additions.network;

import net.cosmos.moonlit_additions.MoonlitAdditions;
import net.cosmos.moonlit_additions.client.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.awt.*;

public record ArouraParticlesPayload(BlockPos position, int age) implements CustomPacketPayload {

    public static final Type<ArouraParticlesPayload> TYPE = new Type<>(MoonlitAdditions.moonlitPath("aroura_particles"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ArouraParticlesPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            ArouraParticlesPayload::position,
            ByteBufCodecs.INT,
            ArouraParticlesPayload::age,
            ArouraParticlesPayload::new
    );

    public void handle(IPayloadContext context) {
        double xSpeed = (context.player().level().random.nextDouble() - 0.5D) * 0.015D;
        double ySpeed = 0.015D + context.player().level().random.nextDouble() * 0.025D;
        double zSpeed = (context.player().level().random.nextDouble() - 0.5D) * 0.015D;
        context.player().level().addParticle(ModParticles.METEOR_AURORA.get(), position.getX(), position.getY(), position.getZ(), xSpeed, ySpeed, zSpeed);
    }

    @Override
    public Type<ArouraParticlesPayload> type() {
        return TYPE;
    }
}
