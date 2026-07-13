package net.cosmos.moonlit.network;

import net.cosmos.moonlit.Moonlit;
import net.cosmos.moonlit.mixin.accessor.GameRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleReflectionShaderPayload(boolean remove) implements CustomPacketPayload {
    public static final Type<ToggleReflectionShaderPayload> TYPE = new Type<>(Moonlit.moonlitPath("toggle_reflection_shader"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleReflectionShaderPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            ToggleReflectionShaderPayload::remove,
            ToggleReflectionShaderPayload::new
    );

    public void handle(IPayloadContext context) {
        GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
        GameRendererAccessor accessor = (GameRendererAccessor) gameRenderer;
        ResourceLocation reflectionShader = Moonlit.moonlitPath("shaders/post/reflection.json");
        PostChain postEffect = accessor.getPostEffect();
        if (remove) {
            if (postEffect != null && postEffect.getName().equals(reflectionShader.toString())) {
                gameRenderer.shutdownEffect();
            }
        } else {
            gameRenderer.loadEffect(reflectionShader);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
