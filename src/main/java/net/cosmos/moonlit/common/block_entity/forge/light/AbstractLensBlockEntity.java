package net.cosmos.moonlit.common.block_entity.forge.light;

import net.cosmos.moonlit.common.block.forge.AbstractLensBlock;
import net.cosmos.moonlit.util.NBTHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntity;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntityType;

public abstract class AbstractLensBlockEntity extends LodestoneBlockEntity {
    private float xAngle;
    private float yAngle;
    private float range = 8;
    private Vec3 lastReached;

    private LightBeam lightBeam;

    public AbstractLensBlockEntity(LodestoneBlockEntityType<? extends AbstractLensBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public Direction getFacing() {
        BlockState blockState = this.getBlockState();
        if (blockState.getBlock() instanceof AbstractLensBlock<?>) {
            return blockState.getValue(AbstractLensBlock.FACING);
        }
        return Direction.NORTH;
    }

    public @Nullable LightBeam getLightBeam() {
        return lightBeam;
    }

    public void setLightBeam(@Nullable LightBeam lightBeam) {
        this.lightBeam = lightBeam;
        this.markUpdated();
    }

    public Vec3 getLastReachedPosition() {
        return this.lastReached;
    }

    public void setLastReachedPosition(Vec3 position) {
        this.lastReached = position;
        this.markUpdated();
    }

    public float getRange() {
        return this.range;
    }

    public void setRange(float range) {
        this.range = range;
        this.markUpdated();
    }

    public float getXAngle() {
        return xAngle;
    }

    public void setXAngle(float xAngle) {
        this.xAngle = xAngle;
        this.markUpdated();
    }

    public float getYAngle() {
        return yAngle;
    }

    public void setYAngle(float yAngle) {
        this.yAngle = yAngle;
        this.markUpdated();
    }

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    @Override
    public void commonTick(Level level) {
        super.commonTick(level);
        if (this.lightBeam != null) {
            this.lightBeam.setXRot(this.xAngle);
            this.lightBeam.setYRot(this.yAngle);
            this.lightBeam.setLength(this.range);
            this.lightBeam.tick(false);
            this.setLastReachedPosition(this.lightBeam.getLastReachedPosition());
        }
    }

    @Override
    public void clientTick(Level level) {
        super.clientTick(level);
        if (this.lightBeam != null) {
            this.lightBeam.tick(true);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        markUpdated();
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.xAngle = tag.getFloat("xAngle");
        this.yAngle = tag.getFloat("yAngle");
        this.lastReached = NBTHelpers.safeReadUsingCodec(tag, "LastReached", Vec3.CODEC);
        if (tag.contains("LightBeam")) {
            this.lightBeam = new LightBeam(this.getBlockPos(), this.getLevel());
            this.lightBeam.read(tag.getCompound("LightBeam"), this.getBlockPos(), this.getLevel());
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putFloat("xAngle", this.xAngle);
        tag.putFloat("yAngle", this.yAngle);
        if (this.lastReached != null) {
            NBTHelpers.safeWriteUsingCodec(tag, "LastReached", this.lastReached, Vec3.CODEC);
        }
        if (this.lightBeam != null) {
            this.lightBeam.write(tag);
        }
    }
}
