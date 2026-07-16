package net.cosmos.moonlit.common.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record Reflection(Optional<ResourceLocation> previousShader) {
    public static final Codec<Reflection> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("previous_shader").forGetter(Reflection::previousShader)
    ).apply(instance, Reflection::new));

    public static Reflection create() {
        return new Reflection(Optional.empty());
    }

    public static Reflection create(ResourceLocation previousShader) {
        return new Reflection(Optional.ofNullable(previousShader));
    }
}
