package net.cosmos.moonlit.util;

import com.mojang.serialization.Codec;
import net.cosmos.moonlit.Moonlit;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.phys.Vec2;

import java.util.List;

import static team.lodestar.lodestone.LodestoneLib.LOGGER;

public class NBTHelpers {
    public static <T> T readUsingCodec(CompoundTag tag, String key, Codec<T> codec) {
        return codec.parse(NbtOps.INSTANCE, tag.get(key)).resultOrPartial(Moonlit.LOGGER::error).orElse(null);
    }

    public static <T> T safeReadUsingCodec(CompoundTag tag, String key, Codec<T> codec) {
        if (tag.contains(key)) {
            return codec.parse(NbtOps.INSTANCE, tag.get(key)).resultOrPartial(Moonlit.LOGGER::error).orElse(null);
        }
        return null;
    }

    public static <T> T safeReadUsingCodec(CompoundTag tag, String key, Codec<T> codec, T defaultValue) {
        if (tag.contains(key)) {
            return codec.parse(NbtOps.INSTANCE, tag.get(key)).resultOrPartial(Moonlit.LOGGER::error).orElse(defaultValue);
        }
        return null;
    }

    public static <T> CompoundTag writeUsingCodec(CompoundTag tag, String key, T value, Codec<T> codec) {
        codec.encodeStart(NbtOps.INSTANCE, value)
                .resultOrPartial(LOGGER::error)
                .ifPresent(p -> tag.put(key, p));
        return tag;
    }

    public static <T> CompoundTag safeWriteUsingCodec(CompoundTag tag, String key, T value, Codec<T> codec) {
        if (value == null) return tag;
        codec.encodeStart(NbtOps.INSTANCE, value)
                .resultOrPartial(LOGGER::error)
                .ifPresent(p -> tag.put(key, p));
        return tag;
    }

    public static final Codec<Vec2> VEC2_CODEC = Codec.FLOAT.listOf().comapFlatMap((list) -> Util.fixedSize(list, 2).map((values) -> new Vec2(values.getFirst(), values.get(1))), (vec) -> List.of(vec.x, vec.y));

}
