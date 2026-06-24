package net.cosmos.moonlit_additions.init;

import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Collections;
import java.util.Set;

import static net.cosmos.moonlit_additions.MoonlitAdditions.moonlitPath;

public class MoonlitLootTables {
    private static final Set<ResourceKey<LootTable>> LOOT_TABLES = Sets.newHashSet();
    private static final Set<ResourceKey<LootTable>> IMMUTABLE_LOCATIONS = Collections.unmodifiableSet(LOOT_TABLES);
    public static final int DEFAULT_PLACE_FLAG = Block.UPDATE_CLIENTS;

    public static void generateChest(WorldGenLevel world, BlockPos pos, Direction dir, boolean trapped, ResourceKey<LootTable> lootTable) {
        generateLootContainer(world, pos, (trapped ? Blocks.TRAPPED_CHEST : Blocks.CHEST).defaultBlockState().setValue(ChestBlock.FACING, dir), DEFAULT_PLACE_FLAG, lootTable);
    }

    public static void generateLootContainer(WorldGenLevel world, BlockPos pos, BlockState state, int flags, ResourceKey<LootTable> lootTable) {
        world.setBlock(pos, state, flags);
        generateChestContents(world, pos, lootTable);
    }

    public static void generateLootContainer(LevelAccessor world, BlockPos pos, BlockState state, int flags, long seed, ResourceKey<LootTable> lootTable) {
        world.setBlock(pos, state, flags);
        generateChestContents(world, pos, seed, lootTable);
    }

    public static void generateChestContents(WorldGenLevel level, BlockPos pos, ResourceKey<LootTable> lootTable) {
        generateChestContents(level, pos, level.getSeed() * pos.getX() + pos.getY() ^ pos.getZ(), lootTable);
    }

    public static void generateChestContents(LevelAccessor level, BlockPos pos, long seed, ResourceKey<LootTable> lootTable) {
        if (level.getBlockEntity(pos) instanceof RandomizableContainerBlockEntity lootContainer) lootContainer.setLootTable(lootTable, seed);
    }

    private static ResourceKey<LootTable> register(String id) {
        return register(ResourceKey.create(Registries.LOOT_TABLE, moonlitPath(id)));
    }

    private static ResourceKey<LootTable> register(ResourceKey<LootTable> id) {
        if (LOOT_TABLES.add(id)) {
            return id;
        } else {
            throw new IllegalArgumentException(id + " is already a registered built-in loot table");
        }
    }

    public static LootParams.Builder createLootParams(LivingEntity entity, boolean checkPlayerKill, DamageSource source) {
        LootParams.Builder lootcontext$builder = (new LootParams.Builder((ServerLevel) entity.level())).withParameter(LootContextParams.THIS_ENTITY, entity).withParameter(LootContextParams.ORIGIN, entity.position()).withParameter(LootContextParams.DAMAGE_SOURCE, source).withOptionalParameter(LootContextParams.ATTACKING_ENTITY, source.getEntity()).withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, source.getDirectEntity());
        if (checkPlayerKill && entity.getKillCredit() instanceof Player player) {
            lootcontext$builder = lootcontext$builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player).withLuck(player.getLuck());
        }

        return lootcontext$builder;
    }

    public static Set<ResourceKey<LootTable>> allBuiltin() {
        return IMMUTABLE_LOCATIONS;
    }
}
