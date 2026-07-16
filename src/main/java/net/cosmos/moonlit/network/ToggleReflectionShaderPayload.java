package net.cosmos.moonlit.network;

import net.cosmos.moonlit.Moonlit;
import net.cosmos.moonlit.client.reflection.ReflectionCausticsPostProcessor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleReflectionShaderPayload(boolean active)
        implements CustomPacketPayload {

    public static final Type<ToggleReflectionShaderPayload> TYPE =
            new Type<>(Moonlit.moonlitPath("toggle_reflection_shader"));

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            ToggleReflectionShaderPayload
            > STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            ToggleReflectionShaderPayload::active,
            ToggleReflectionShaderPayload::new
    );

    public void handle(IPayloadContext context) {
        context.enqueueWork(() ->
                ReflectionCausticsPostProcessor.setActive(active)
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}