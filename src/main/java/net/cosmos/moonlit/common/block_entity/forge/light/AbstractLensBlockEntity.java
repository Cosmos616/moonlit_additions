package net.cosmos.moonlit.common.block_entity.forge.light;

import net.cosmos.moonlit.Moonlit;
import net.cosmos.moonlit.common.block.forge.AbstractLensBlock;
import net.cosmos.moonlit.util.NBTHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntity;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntityType;

import java.util.ArrayList;

public abstract class AbstractLensBlockEntity extends LodestoneBlockEntity {
    public Vec2 angle = Vec2.ZERO;
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
        return this.lightBeam;
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

    public Vec2 getAngle() {
        if (this.angle == null) {
            return Vec2.ZERO;
        }
        return this.angle;
    }

    public void setAngle(Vec2 angle) {
        this.angle = angle;
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
            this.lightBeam.setAngle(this.angle);
            this.lightBeam.setLength(this.range);
            this.lightBeam.tick(false);
            this.setLastReachedPosition(this.lightBeam.getLastReachedPosition());
        } else {
            this.lightBeam = new LightBeam(worldPosition, level, this.range, this.angle, new ArrayList<>());
        }
    }

    @Override
    public void clientTick(Level level) {
        super.clientTick(level);
        if (this.lightBeam != null) {
            this.lightBeam.tick(true);
        } else {
            Moonlit.LOGGER.info("Client Light Beam is null at {}", getBlockPos());
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
        this.angle = NBTHelpers.safeReadUsingCodec(tag, "angle", NBTHelpers.VEC2_CODEC);
        this.lastReached = NBTHelpers.safeReadUsingCodec(tag, "LastReached", Vec3.CODEC);
        this.lightBeam = LightBeam.read(tag, this.getBlockPos(), this.getLevel());
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        NBTHelpers.safeWriteUsingCodec(tag, "angle", this.angle, NBTHelpers.VEC2_CODEC);
        if (this.lastReached != null) {
            NBTHelpers.safeWriteUsingCodec(tag, "LastReached", this.lastReached, Vec3.CODEC);
        }
        if (this.lightBeam != null) {
            this.lightBeam.write(tag);
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
