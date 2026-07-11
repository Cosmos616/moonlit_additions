package net.cosmos.moonlit.common.block_entity.debug;

import net.cosmos.moonlit.init.ModBlockEntities;
import net.cosmos.moonlit.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntity;

import static net.cosmos.moonlit.common.block_entity.debug.GlowingAncestralCarvingBlockEntity.SunlightState.*;

public class GlowingAncestralCarvingBlockEntity extends LodestoneBlockEntity {

    public enum SunlightState {
        INACTIVE,
        VISUAL_ONLY,
        CHARGING,
        ACTIVE
    }

    protected SunlightState state = INACTIVE;
    protected int glow;

    public GlowingAncestralCarvingBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GLOWING_ANCESTRAL_CARVING.get(), pos, state);
    }

    public SunlightState getState() {
        return state;
    }

    public void setState(SunlightState state) {
        this.state = state;
        setDirty();
    }

    @Override
    public ItemInteractionResult onUseWithItem(Player player, ItemStack held, InteractionHand hand) {
        if (held.is(ModItems.FAILED_SUN)) {
            if (level instanceof ServerLevel serverLevel) {
                if (state.equals(CHARGING) || state.equals(ACTIVE)) {
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }
                toggleVisuals(serverLevel);
            }
            return ItemInteractionResult.SUCCESS;
        }
        return super.onUseWithItem(player, held, hand);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("state", state.ordinal());
        tag.putInt("glow", glow);
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        state = SunlightState.values()[tag.getInt("state")];
        glow = tag.getInt("glow");
        super.loadAdditional(tag, pRegistries);
    }

    @Override
    public void commonTick(Level level) {
        if (state.equals(INACTIVE)) {
            if (glow > 0) {
                glow--;
            }
        } else {
            int cap = state.equals(CHARGING) ? 10 : 20;
            if (glow < cap) {
                glow++;
            }
        }
    }

    public float getGlowDelta() {
        return glow / 20f;
    }

    public void toggleVisuals(ServerLevel level) {
        if (state.equals(VISUAL_ONLY)) {
            state = INACTIVE;
        } else if (state.equals(INACTIVE)) {
            state = VISUAL_ONLY;
        }
        setDirty();
    }
}
