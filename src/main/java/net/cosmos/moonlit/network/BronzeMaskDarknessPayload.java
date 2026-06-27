package net.cosmos.moonlit.network;

import net.cosmos.moonlit.Moonlit;
import net.cosmos.moonlit.client.rendering.BronzeMaskDarknessRenderer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record BronzeMaskDarknessPayload(boolean wearingMask) implements CustomPacketPayload {

    public static final Type<BronzeMaskDarknessPayload> TYPE = new Type<>(Moonlit.moonlitPath("bronze_mask_darkness"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BronzeMaskDarknessPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            BronzeMaskDarknessPayload::wearingMask,
            BronzeMaskDarknessPayload::new
    );

    public void handle(IPayloadContext context) {
        BronzeMaskDarknessRenderer.getInstance().tick(wearingMask);
    }

    @Override
    public Type<BronzeMaskDarknessPayload> type() {
        return TYPE;
    }
}
