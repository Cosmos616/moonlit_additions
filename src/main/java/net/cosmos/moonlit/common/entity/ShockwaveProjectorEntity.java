package net.cosmos.moonlit.common.entity;

import net.cosmos.moonlit.Moonlit;
import net.cosmos.moonlit.client.shockwave.ShockwaveClientData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class ShockwaveProjectorEntity extends Entity {
    private static final int MAX_AGE = 80;

    private int shockwaveAge = 0;

    public ShockwaveProjectorEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);

        this.noPhysics = true;
        this.noCulling = true;
    }


    @Override
    public void tick() {
        super.tick();

        this.shockwaveAge++;

        ShockwaveClientData.alpha = 1f - ((float) shockwaveAge / (float) MAX_AGE);
        Moonlit.LOGGER.info(String.valueOf(ShockwaveClientData.alpha));

        if (!this.level().isClientSide && this.shockwaveAge >= MAX_AGE) {
            this.discard();
        }
    }

    public float getProgress(float partialTick) {
        return Math.min((this.shockwaveAge + partialTick) / (float) MAX_AGE, 1.0F);
    }

    public float getRadius(float partialTick) {
        float progress = this.getProgress(partialTick);

        float startRadius = 0.25F;
        float endRadius = 100.0F;

        return startRadius + (endRadius - startRadius) * progress;
    }

    public float getAlpha(float partialTick) {
        float progress = this.getProgress(partialTick);

        // Fade out over lifetime.
        return 1.0F - progress;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        // No synced data needed yet.
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.shockwaveAge = tag.getInt("ShockwaveAge");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("ShockwaveAge", this.shockwaveAge);
    }
}
