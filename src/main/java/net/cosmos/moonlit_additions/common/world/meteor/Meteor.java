package net.cosmos.moonlit_additions.common.world.meteor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Meteor(int age) {

    public static final Codec<Meteor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("depth").forGetter(Meteor::age)
    ).apply(instance, Meteor::new));
}
