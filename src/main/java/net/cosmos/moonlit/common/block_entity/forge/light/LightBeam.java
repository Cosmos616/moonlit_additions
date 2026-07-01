package net.cosmos.moonlit.common.block_entity.forge.light;

import net.cosmos.moonlit.Moonlit;
import net.cosmos.moonlit.util.NBTHelpers;
import net.cosmos.moonlit.util.OrientedBoundingBox;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class LightBeam {
    private final BlockPos sourcePos;
    private final Level world;

    public Vec2 angle;
    public Vec2 cachedAngle;

    private float length;
    public float cachedLength;

    public List<BlockPos> availablePositionCache = new ArrayList<>();
    public boolean forceUpdate = false;

    public LightBeam(BlockPos sourcePos, Level world, float length, Vec2 angle, float cachedLength, Vec2 cachedAngle, List<BlockPos> posCache) {
        this.sourcePos = sourcePos;
        this.world = world;
        this.length = length;
        this.cachedLength = cachedLength;
        this.angle = angle;
        this.cachedAngle = cachedAngle;
        this.availablePositionCache = posCache;
    }

    public LightBeam(BlockPos sourcePos, Level world, float length, Vec2 angle, List<BlockPos> posCache) {
        this.sourcePos = sourcePos;
        this.world = world;
        this.length = length;
        this.cachedLength = length;
        this.angle = angle;
        this.cachedAngle = angle;
        this.availablePositionCache = posCache;
    }

    public LightBeam(BlockPos sourcePos, Level world) {
        this.sourcePos = sourcePos;
        this.world = world;
    }

    public Vec3 getLastPossiblePosition() {
        return BeamHelpers.locate3DPos(getAngle(), this.sourcePos.getCenter(), this.cachedLength);
    }

    public Vec3 position() {
        return this.sourcePos.getCenter();
    }

    public Vec3 getLastReachedPosition() {
        if (this.cachedAngle == null || this.length <= 0) return this.sourcePos.getCenter();
        return BeamHelpers.getActualEndpoint(this.world, this.sourcePos.getCenter(), this.getLastPossiblePosition());
    }

    public OrientedBoundingBox getBoundingBox() {
        Vec3 size = new Vec3(0, this.cachedLength, 0);
        if (getAngle() == null)
            return new OrientedBoundingBox(this.sourcePos.getCenter(), size, BeamHelpers.yaw(Vec3.ZERO), BeamHelpers.pitch(Vec3.ZERO));
        return new OrientedBoundingBox(this.sourcePos.getCenter(), size, BeamHelpers.yaw(getAngle()), BeamHelpers.pitch(getAngle()));
    }

    public void gatherPositions() {
        if ((this.availablePositionCache == null || this.availablePositionCache.isEmpty()) || this.forceUpdate) {
            OrientedBoundingBox boundingBox = getBoundingBox().updateVertex();
            int bound = Math.round(getLength());
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
        if (this.cachedAngle != this.angle && this.angle != null) {
            this.cachedAngle = this.angle;
        }
        if (this.cachedLength != this.length && this.length != 0) {
            this.cachedLength = this.length;
        }
        gatherPositions();
    }

    public void setAngle(Vec2 angle) {
        this.angle = angle;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public Vec2 cachedAngle() {
        if (this.cachedAngle == null) {
            Moonlit.LOGGER.error("Cached angle is null");
            return null;
        }
        return this.cachedAngle;
    }

    public Vec3 getAngle() {
        if (this.cachedAngle() == null) {
            return null;
        }
        return BeamHelpers.getVectorFromAngles(this.cachedAngle().x, this.cachedAngle().y);
    }

    public float getLength() {
        return this.cachedLength;
    }

    public void update() {
        this.forceUpdate = true;
    }

    public void write(CompoundTag compound) {
        CompoundTag lightCompound = new CompoundTag();
        lightCompound.putFloat("length", this.length);
        lightCompound.putFloat("cachedLength", this.cachedLength);
        NBTHelpers.safeWriteUsingCodec(lightCompound, "angle", this.angle, NBTHelpers.VEC2_CODEC);
        NBTHelpers.safeWriteUsingCodec(lightCompound, "cachedAngle", this.cachedAngle, NBTHelpers.VEC2_CODEC);
        NBTHelpers.safeWriteUsingCodec(lightCompound, "cached_positions", this.availablePositionCache, BlockPos.CODEC.listOf());
        compound.put("LightBeam", lightCompound);
    }

    public static LightBeam read(CompoundTag compound, BlockPos sourcePos, Level world) {
        CompoundTag lightCompound = compound.getCompound("LightBeam");
        Vec2 cachedAngle = NBTHelpers.safeReadUsingCodec(lightCompound, "cachedAngle", NBTHelpers.VEC2_CODEC);
        Vec2 angle = NBTHelpers.safeReadUsingCodec(lightCompound, "angle", NBTHelpers.VEC2_CODEC);
        float cachedLength = lightCompound.getFloat("cachedLength");
        float length = lightCompound.getFloat("length");
        List<BlockPos> posCache = NBTHelpers.safeReadUsingCodec(lightCompound, "cached_positions", BlockPos.CODEC.listOf());
        return new LightBeam(sourcePos, world, length, angle, cachedLength, cachedAngle, posCache);
    }
}
