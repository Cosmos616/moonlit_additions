package net.cosmos.moonlit.common.block_entity.forge.light;

import net.cosmos.moonlit.Moonlit;
import net.cosmos.moonlit.common.block.forge.AbstractLensBlock;
import net.cosmos.moonlit.util.NBTHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
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

    private Vec2 previousAngle = Vec2.ZERO;
    private Vec2 targetAngle = Vec2.ZERO;

    /**
     * Client-only visual angle used while the player is dragging the lens.
     *
     * This lets the renderer move immediately without fighting server sync.
     * The real angle is still owned by the server.
     */
    private Vec2 clientPreviewAngle = null;

    private float range = 8;
    private Vec3 lastReached;

    private LightBeam lightBeam;

    public AbstractLensBlockEntity(
            LodestoneBlockEntityType<? extends AbstractLensBlockEntity> type,
            BlockPos pos,
            BlockState state
    ) {
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

        // Do not send a block update every tick for beam endpoint changes.
        this.setChanged();
    }

    public float getRange() {
        return this.range;
    }

    public void setRange(float range) {
        this.range = range;
        this.markUpdated();
    }

    public Vec2 getAngle() {
        return this.angle == null ? Vec2.ZERO : this.angle;
    }

    public Vec2 getPreviousAngle() {
        return this.previousAngle == null ? Vec2.ZERO : this.previousAngle;
    }

    public Vec2 getTargetAngle() {
        return this.targetAngle == null ? Vec2.ZERO : this.targetAngle;
    }

    /**
     * Use this from render transforms.
     *
     * While dragging, the client preview is preferred.
     * Otherwise it interpolates between previousAngle and angle.
     */
    public Vec2 getRenderAngle(float partialTick) {
        if (this.level != null && this.level.isClientSide && this.clientPreviewAngle != null) {
            return this.clientPreviewAngle;
        }

        Vec2 safePrevious = getPreviousAngle();
        Vec2 safeAngle = getAngle();

        float pitch = Mth.rotLerp(partialTick, safePrevious.x, safeAngle.x);
        float yaw = Mth.rotLerp(partialTick, safePrevious.y, safeAngle.y);

        return new Vec2(pitch, yaw);
    }

    /**
     * Server-side target angle. The server tick moves angle toward this.
     */
    public void setTargetAngle(float pitch, float yaw) {
        this.targetAngle = new Vec2(pitch, yaw);

        if (this.level != null && !this.level.isClientSide) {
            this.setChanged();
        }
    }

    /**
     * Sets the real angle immediately.
     *
     * Useful for placement/setup, loading, or debugging.
     */
    public void setAngle(Vec2 angle) {
        Vec2 safeAngle = angle == null ? Vec2.ZERO : angle;

        this.angle = safeAngle;
        this.previousAngle = safeAngle;
        this.targetAngle = safeAngle;

        this.markUpdated();
    }

    /**
     * Client-only visual preview while dragging.
     */
    public void setClientPreviewAngle(float pitch, float yaw) {
        if (this.level != null && !this.level.isClientSide) {
            return;
        }

        this.clientPreviewAngle = new Vec2(pitch, yaw);
    }

    public void clearClientPreviewAngle() {
        this.clientPreviewAngle = null;
    }

    private void markUpdated() {
        this.setChanged();

        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(
                    this.getBlockPos(),
                    this.getBlockState(),
                    this.getBlockState(),
                    3
            );
        }
    }

    public void setupRotation(Direction direction) {
        // Always face North if direction is up/down because why not.
        if (direction.getAxis() == Direction.Axis.Y) {
            this.setAngle(new Vec2(0, 0));
            return;
        }

        switch (direction) {
            case NORTH -> this.setAngle(new Vec2(90, 0));
            case SOUTH -> this.setAngle(new Vec2(90, 0));
            case EAST -> this.setAngle(new Vec2(90, 0));
            case WEST -> this.setAngle(new Vec2(90, 0));
        }
    }

    @Override
    public void commonTick(Level level) {
        super.commonTick(level);

        /*
         * The server owns the real angle and beam state.
         *
         * Do not tickAngle() on the client unless you intentionally want
         * unsynced client-side simulation. Otherwise the client can fight
         * the server and cause the lens to stutter.
         */
        if (!level.isClientSide) {
            tickAngle();

            if (this.lightBeam != null) {
                this.lightBeam.setAngle(this.angle);
                this.lightBeam.setLength(this.range);
                this.lightBeam.tick(false);
                this.setLastReachedPosition(this.lightBeam.getLastReachedPosition());
            } else {
                this.lightBeam = new LightBeam(
                        worldPosition,
                        level,
                        this.range,
                        this.angle,
                        new ArrayList<>()
                );
            }
        }
    }

    public void tickAngle() {
        Vec2 safeAngle = getAngle();
        Vec2 safeTarget = getTargetAngle();

        this.previousAngle = safeAngle;

        float pitch = Mth.approachDegrees(
                safeAngle.x,
                safeTarget.x,
                0.1F
        );

        float yaw = Mth.approachDegrees(
                safeAngle.y,
                safeTarget.y,
                0.1F
        );

        this.angle = new Vec2(pitch, yaw);
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

        if (this.level != null && !this.level.isClientSide) {
            this.setChanged();
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        Vec2 loadedAngle = NBTHelpers.safeReadUsingCodec(
                tag,
                "angle",
                NBTHelpers.VEC2_CODEC
        );

        this.angle = loadedAngle == null ? Vec2.ZERO : loadedAngle;
        this.previousAngle = this.angle;
        this.targetAngle = this.angle;

        this.lastReached = NBTHelpers.safeReadUsingCodec(
                tag,
                "LastReached",
                Vec3.CODEC
        );

        this.lightBeam = LightBeam.read(
                tag,
                this.getBlockPos(),
                this.getLevel()
        );
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        NBTHelpers.safeWriteUsingCodec(
                tag,
                "angle",
                this.getAngle(),
                NBTHelpers.VEC2_CODEC
        );

        if (this.lastReached != null) {
            NBTHelpers.safeWriteUsingCodec(
                    tag,
                    "LastReached",
                    this.lastReached,
                    Vec3.CODEC
            );
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