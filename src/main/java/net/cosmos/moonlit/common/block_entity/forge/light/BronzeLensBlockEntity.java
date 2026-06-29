package net.cosmos.moonlit.common.block_entity.forge.light;

import net.cosmos.moonlit.common.block.forge.BronzeLensBlock;
import net.cosmos.moonlit.init.ModBlockEntities;
import net.cosmos.moonlit.util.NBTHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntity;

public class BronzeLensBlockEntity extends LodestoneBlockEntity {

    public Vec3 rotation = new Vec3(0, 0, 0);
    public Vec3 targetPos = Vec3.ZERO;
    public int beamRange = 8;
    public LightBeam lightBeam;

    public BronzeLensBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BRONZE_LENS.get(), pos, state);
    }

    @Override
    public void commonTick(Level level) {
        super.commonTick(level);
        BlockState state = this.getBlockState();
        if (state.getValue(BronzeLensBlock.ROTATE_X)) {
            setAngle(Direction.Axis.X, (int) (this.rotation.x + 1));
        }
        if (state.getValue(BronzeLensBlock.ROTATE_Y)) {
            setAngle(Direction.Axis.Y, (int) (this.rotation.y + 1));
        }
        if (state.getValue(BronzeLensBlock.ROTATE_Z)) {
            setAngle(Direction.Axis.Z, (int) (this.rotation.z + 1));
        }
        if (this.lightBeam == null) {
            this.lightBeam = new LightBeam(worldPosition, level)
                    .setAngle(rotation)
                    .setLength(beamRange);
        } else {
            lightBeam.setAngle(rotation);
            lightBeam.setLength(beamRange);
            this.targetPos = this.lightBeam.getLastReachedPosition();
            this.lightBeam.tick(false);
        }
    }

    @Override
    public void clientTick(Level level) {
        super.clientTick(level);
        if (this.lightBeam != null) {
            this.lightBeam.tick(true);
        }
    }

    private void setAngle(Direction.Axis axis, int angle) {
        switch (axis) {
            case X: rotation = new Vec3(angle, rotation.y, rotation.z); setChanged(); this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
            case Y: rotation = new Vec3(rotation.x, angle, rotation.z); setChanged(); this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
            case Z: rotation = new Vec3(rotation.x, rotation.y, angle); setChanged(); this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.rotation = NBTHelpers.safeReadUsingCodec(pTag, "rotation", Vec3.CODEC);
        this.targetPos = NBTHelpers.safeReadUsingCodec(pTag, "target", Vec3.CODEC);
        this.lightBeam = LightBeam.read(pTag, worldPosition, level);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        NBTHelpers.safeWriteUsingCodec(tag, "rotation", this.rotation, Vec3.CODEC);
        NBTHelpers.safeWriteUsingCodec(tag, "target", this.targetPos, Vec3.CODEC);
        if (lightBeam != null) {
            lightBeam.write(tag);
        }
    }
}
