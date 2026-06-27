package net.cosmos.moonlit.network;

import net.cosmos.moonlit.Moonlit;
import net.cosmos.moonlit.client.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ArouraParticlesPayload(BlockPos position, int age) implements CustomPacketPayload {

    public static final Type<ArouraParticlesPayload> TYPE = new Type<>(Moonlit.moonlitPath("aroura_particles"));

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
