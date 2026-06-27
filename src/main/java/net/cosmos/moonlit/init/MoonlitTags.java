package net.cosmos.moonlit.init;

import com.farcr.nomansland.common.definitions.SharedTag;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;

import static net.cosmos.moonlit.Moonlit.moonlitPath;

public class MoonlitTags {
    public static final TagKey<Item> MOONLIT_ASH = createItemTag("c", "dusts/moonlit_ash");
    public static final SharedTag MOONLIT_ASH_BLOCKS = createSharedTag("c", "storage_blocks/moonlit_ash");

    public static final TagKey<Item> MOONLIT_BRONZE_INGOTS = createItemTag("c", "ingots/moonlit_bronze");
    public static final TagKey<Item> METEORIC_IRON_INGOTS = createItemTag("c", "ingots/meteoric_iron");
    public static final TagKey<Item> RAW_METEORIC_IRON = createItemTag("c", "raw_materials/meteoric_iron");

    public MoonlitTags() {
    }

    private static SharedTag createSharedTag(String name) {
        return new SharedTag(name);
    }

    private static SharedTag createSharedTag(String namespace, String name) {
        return new SharedTag(namespace, name);
    }

    private static TagKey<Item> createItemTag(String name) {
        return TagKey.create(Registries.ITEM, moonlitPath(name));
    }

    private static TagKey<Item> createItemTag(String namespace, String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(namespace, name));
    }

    private static TagKey<Block> createBlockTag(String name) {
        return TagKey.create(Registries.BLOCK, moonlitPath(name));
    }

    private static TagKey<Biome> createBiomeTag(String name) {
        return TagKey.create(Registries.BIOME, moonlitPath(name));
    }

    private static TagKey<EntityType<?>> createEntityTag(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, moonlitPath(name));
    }

    private static TagKey<DamageType> createDamageTypeTag(String name) {
        return TagKey.create(Registries.DAMAGE_TYPE, moonlitPath(name));
    }

    private static TagKey<Structure> createStructureTag(String name) {
        return TagKey.create(Registries.STRUCTURE, moonlitPath(name));
    }

    public static class FeatureAddition {

        public FeatureAddition() {
        }
    }

    public static class FeatureRemoval {

        public FeatureRemoval() {
        }
    }
}
