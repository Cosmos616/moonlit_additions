package net.cosmos.moonlit.common.block_entity.forge.light;

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
    private float length;

    public List<BlockPos> availablePositionCache;
    public boolean forceUpdate = false;

    public LightBeam(BlockPos sourcePos, Level world, float length, Vec2 angle, List<BlockPos> posCache) {
        this.sourcePos = sourcePos;
        this.world = world;
        this.length = length;
        this.angle = angle;
        this.availablePositionCache = posCache;
    }

    public Vec3 getLastPossiblePosition() {
        if (this.angle == null) {
            return this.position();
        }
        return BeamHelpers.locate3DPos(angle, this.position(), this.length);
    }

    public Vec3 position() {
        return this.sourcePos.getCenter();
    }

    public Vec3 getLastReachedPosition() {
        if (this.angle == null || this.length <= 0) return this.position();
        return BeamHelpers.getActualEndpoint(this.world, this.position(), this.getLastPossiblePosition());
    }

    public OrientedBoundingBox getBoundingBox() {
        Vec3 size = new Vec3(0, this.length, 0);
        if (this.angle == null)
            return new OrientedBoundingBox(this.position(), size, BeamHelpers.yaw(Vec3.ZERO), BeamHelpers.pitch(Vec3.ZERO));
        return new OrientedBoundingBox(this.position(), size, angle.y, angle.x);
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
        if (this.angle != null) {
            gatherPositions();
        }
    }

    public void setAngle(Vec2 angle) {
        this.angle = angle;
        update();
    }

    public void setLength(float length) {
        this.length = length;
        update();
    }

    public float getLength() {
        return this.length;
    }

    public void update() {
        this.forceUpdate = true;
    }

    public void write(CompoundTag compound) {
        CompoundTag lightCompound = new CompoundTag();
        lightCompound.putFloat("length", this.length);
        NBTHelpers.safeWriteUsingCodec(lightCompound, "angle", this.angle, NBTHelpers.VEC2_CODEC);
        NBTHelpers.safeWriteUsingCodec(lightCompound, "cached_positions", this.availablePositionCache, BlockPos.CODEC.listOf());
        compound.put("LightBeam", lightCompound);
    }

    public static LightBeam read(CompoundTag compound, BlockPos sourcePos, Level world) {
        CompoundTag lightCompound = compound.getCompound("LightBeam");
        Vec2 angle = NBTHelpers.safeReadUsingCodec(lightCompound, "angle", NBTHelpers.VEC2_CODEC);
        float length = lightCompound.getFloat("length");
        List<BlockPos> posCache = NBTHelpers.safeReadUsingCodec(lightCompound, "cached_positions", BlockPos.CODEC.listOf());
        return new LightBeam(sourcePos, world, length, angle, posCache);
    }
}
