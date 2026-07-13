package net.cosmos.moonlit.common.attachment;

import com.mojang.serialization.Codec;

public record Reflection() {
    public static final Codec<Reflection> CODEC = Codec.unit(new Reflection());

    public static Reflection create() {
        return new Reflection();
    }
}
