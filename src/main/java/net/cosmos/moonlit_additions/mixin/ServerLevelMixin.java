package net.cosmos.moonlit_additions.mixin;

import net.cosmos.moonlit_additions.common.world.meteor.Meteor;
import net.cosmos.moonlit_additions.init.ModAttachmentTypes;
import net.cosmos.moonlit_additions.network.ArouraParticlesPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Inject(method = "tickChunk(Lnet/minecraft/world/level/chunk/LevelChunk;I)V", at = @At("TAIL"))
    private void tickChunk(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        RandomSource random = chunk.getLevel().getRandom();
        if (chunk.hasData(ModAttachmentTypes.METEOR)) {
            Meteor meteor = chunk.getData(ModAttachmentTypes.METEOR);
            int age = meteor.age();
            int y = chunk.getLevel().getMaxBuildHeight();
            for (int i = 0; i < 6; i++) {
                if (random.nextFloat() > 0.4f) continue;
                int x = random.nextIntBetweenInclusive(chunk.getPos().getMinBlockX(), chunk.getPos().getMaxBlockX());
                int z = random.nextIntBetweenInclusive(chunk.getPos().getMinBlockZ(), chunk.getPos().getMaxBlockZ());
                PacketDistributor.sendToAllPlayers(new ArouraParticlesPayload(new BlockPos(x, y, z), age));
            }
            for (int x : new int[]{-1, 1}) {
                for (int z : new int[]{-1, 1}) {
                    LevelChunk neighbor = chunk.getLevel().getChunk(chunk.getPos().x + x, chunk.getPos().z + z);
                    if (!neighbor.hasData(ModAttachmentTypes.METEOR)) {
                        for (int i = 0; i < 6; i++) {
                            if (random.nextFloat() > 0.4f) continue;
                            int pX = random.nextIntBetweenInclusive(neighbor.getPos().getMinBlockX(), neighbor.getPos().getMaxBlockX());
                            int pZ = random.nextIntBetweenInclusive(neighbor.getPos().getMinBlockZ(), neighbor.getPos().getMaxBlockZ());
                            PacketDistributor.sendToAllPlayers(new ArouraParticlesPayload(new BlockPos(pX, y, pZ), age));
                        }
                    }
                }
            }
        }
    }
}
