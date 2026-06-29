package net.cosmos.moonlit.common.block_entity.forge.light;

import net.cosmos.moonlit.util.NBTHelpers;
import net.cosmos.moonlit.util.OrientedBoundingBox;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class LightBeam {
    private final BlockPos sourcePos;
    private final Level world;

    private Vec3 angle = Vec3.ZERO;
    private float length = 0;

    public List<BlockPos> availablePositionCache = new ArrayList<>();
    public boolean forceUpdate = false;

    public boolean canRender() {
        return this.angle != null;
    }

    public LightBeam(BlockPos sourcePos, Level world, Vec3 angle, float length, List<BlockPos> posCache) {
        this.sourcePos = sourcePos;
        this.world = world;
        this.angle = angle;
        this.length = length;
        this.availablePositionCache = posCache;
    }

    public LightBeam(BlockPos sourcePos, Level world) {
        this.sourcePos = sourcePos;
        this.world = world;
    }

    public LightBeam(BlockPos sourcePos, Level world, Vec3 angle, float length) {
        this.sourcePos = sourcePos;
        this.world = world;
        this.angle = angle;
        this.length = length;
    }

    public Vec3 getLastPossiblePosition() {
        return BeamHelpers.locate3DPos(angle, sourcePos.getCenter(), length);
    }

    public Vec3 position() {
        return this.sourcePos.getCenter();
    }

    public Vec3 getLastReachedPosition() {
        return BeamHelpers.getActualEndpoint(world, sourcePos.getCenter(), getLastPossiblePosition());
    }

    public OrientedBoundingBox getBoundingBox() {
        Vec3 size = new Vec3(0, length, 0);
        if (angle == null)
            return new OrientedBoundingBox(sourcePos.getCenter(), size, BeamHelpers.yaw(Vec3.ZERO), BeamHelpers.pitch(Vec3.ZERO));
        return new OrientedBoundingBox(sourcePos.getCenter(), size, BeamHelpers.yaw(angle), BeamHelpers.pitch(angle));
    }

    public void gatherPositions() {
        if ((availablePositionCache == null || availablePositionCache.isEmpty()) || forceUpdate) {
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
            availablePositionCache = posList;
            forceUpdate = false;
        }
    }

    public List<BlockPos> getAvailablePositions() {
        return availablePositionCache;
    }

    public void tick(boolean client) {
        gatherPositions();

    }

    public LightBeam setAngle(Vec3 angle) {
        this.angle = angle;
        update();
        return this;
    }

    public LightBeam setLength(float length) {
        this.length = length;
        update();
        return this;
    }

    public Vec3 getAngle() {
        return angle;
    }

    public float getLength() {
        return length;
    }

    public void update() {
        this.forceUpdate = true;
    }

    public void write(CompoundTag tag) {
        CompoundTag spiritTag = tag.getCompound("light_beam");
        NBTHelpers.safeWriteUsingCodec(spiritTag, "angle", getAngle(), Vec3.CODEC);
        NBTHelpers.safeWriteUsingCodec(spiritTag, "cached_positions", availablePositionCache, BlockPos.CODEC.listOf());
        spiritTag.putFloat("length", getLength());
    }

    public static LightBeam read(CompoundTag tag, BlockPos sourcePos, Level world) {
        CompoundTag spiritTag = tag.getCompound("spirit_beam");
        Vec3 angle = NBTHelpers.safeReadUsingCodec(spiritTag, "angle", Vec3.CODEC);
        List<BlockPos> posCache = NBTHelpers.safeReadUsingCodec(spiritTag, "cached_positions", BlockPos.CODEC.listOf(), List.of());
        float length = spiritTag.getFloat("length");
        return new LightBeam(sourcePos, world, angle, length, posCache);
    }
}
