package net.cosmos.moonlit.common.block_entity.forge.light;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.cosmos.moonlit.Moonlit;
import net.cosmos.moonlit.util.NBTHelpers;
import net.cosmos.moonlit.util.OrientedBoundingBox;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import team.lodestar.lodestone.helpers.NBTHelper;

import java.util.ArrayList;
import java.util.List;

public class LightBeam {
    private final BlockPos sourcePos;
    private final Level world;

    public float xAngle;
    public float yAngle;

    private float length;

    public List<BlockPos> availablePositionCache = new ArrayList<>();
    public boolean forceUpdate = false;

    public LightBeam(BlockPos sourcePos, Level world, float length, float xAngle, float yAngle, List<BlockPos> posCache) {
        this.sourcePos = sourcePos;
        this.world = world;
        this.length = length;
        this.xAngle = xAngle;
        this.yAngle = yAngle;
        this.availablePositionCache = posCache;
    }

    public LightBeam(BlockPos sourcePos, Level world) {
        this.sourcePos = sourcePos;
        this.world = world;
    }

    public Vec3 getLastPossiblePosition() {
        return BeamHelpers.locate3DPos(getAngle(), this.sourcePos.getCenter(), this.length);
    }

    public Vec3 position() {
        return this.sourcePos.getCenter();
    }

    public Vec3 getLastReachedPosition() {
        return BeamHelpers.getActualEndpoint(this.world, this.sourcePos.getCenter(), this.getLastPossiblePosition());
    }

    public OrientedBoundingBox getBoundingBox() {
        Vec3 size = new Vec3(0, this.length, 0);
        if (getAngle() == null)
            return new OrientedBoundingBox(this.sourcePos.getCenter(), size, BeamHelpers.yaw(Vec3.ZERO), BeamHelpers.pitch(Vec3.ZERO));
        return new OrientedBoundingBox(this.sourcePos.getCenter(), size, BeamHelpers.yaw(getAngle()), BeamHelpers.pitch(getAngle()));
    }

    public void gatherPositions() {
        if ((this.availablePositionCache == null || this.availablePositionCache.isEmpty()) || this.forceUpdate) {
            OrientedBoundingBox boundingBox = getBoundingBox().updateVertex();
            int bound = 8;
            BlockPos origin = BlockPos.ZERO;
            Vec3i lower = origin.offset(-bound,-bound,-bound);
            Vec3i upper = origin.offset(bound,bound,bound);
            List<BlockPos> posList = new ArrayList<>();
            for (int x = lower.getX(); x <= upper.getX(); x++) {
                for (int y = lower.getY(); y <= upper.getY(); y++) {
                    for (int z = lower.getZ(); z <= upper.getZ(); z++) {
                        posList.add(origin.offset(x, y, z));
                    }
                }
            }
            posList.removeIf(pos -> !boundingBox.intersects(new AABB(pos)));
            this.availablePositionCache = posList;
            this.forceUpdate = false;
        }
    }

    public List<BlockPos> getAvailablePositions() {
        return availablePositionCache;
    }

    public void tick(boolean client) {
        gatherPositions();
    }

    public void setXRot(float xAngle) {
        this.xAngle = xAngle;
        update();
    }

    public void setYRot(float yAngle) {
        this.yAngle = yAngle;
        update();
    }

    public void setLength(float length) {
        this.length = length;
        update();
    }

    public Vec3 getAngle() {
        return BeamHelpers.getVectorFromAngles(this.xAngle, this.yAngle);
    }

    public float getLength() {
        return this.length;
    }

    public void update() {
        this.forceUpdate = true;
    }

    public void write(CompoundTag compound) {
        CompoundTag lightCompound = new CompoundTag();
        lightCompound.putFloat("xAngle", this.xAngle);
        lightCompound.putFloat("yAngle", this.yAngle);
        lightCompound.putFloat("length", this.length);
        NBTHelpers.safeWriteUsingCodec(lightCompound, "cached_positions", this.availablePositionCache, BlockPos.CODEC.listOf());
        compound.put("LightBeam", lightCompound);
    }

    public static LightBeam read(CompoundTag compound, BlockPos sourcePos, Level world) {
        CompoundTag lightCompound = compound.getCompound("LightBeam");
        float xAngle = lightCompound.getFloat("xAngle");
        float yAngle = lightCompound.getFloat("yAngle");
        float length = lightCompound.getFloat("length");
        List<BlockPos> posCache = NBTHelpers.safeReadUsingCodec(lightCompound, "cached_positions", BlockPos.CODEC.listOf());
        return new LightBeam(sourcePos, world, length, xAngle, yAngle, posCache);
    }
}
