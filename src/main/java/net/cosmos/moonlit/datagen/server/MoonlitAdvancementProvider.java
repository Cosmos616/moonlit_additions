package net.cosmos.moonlit.datagen.server;

import com.mojang.datafixers.util.Pair;
import net.cosmos.moonlit.Moonlit;
import net.cosmos.moonlit.init.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

import static net.cosmos.moonlit.Moonlit.moonlitPath;

public class MoonlitAdvancementProvider implements AdvancementProvider.AdvancementGenerator {
    public static final Set<Pair<String, String>> titles = new LinkedHashSet<>();
    public static final Set<Pair<String, String>> descriptions = new LinkedHashSet<>();

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {
        HolderGetter<Biome> biomes = registries.lookupOrThrow(Registries.BIOME);
        HolderGetter<Structure> structures = registries.lookupOrThrow(Registries.STRUCTURE);
    }

    private ResourceLocation path(String name) {
        return moonlitPath("main/" + name);
    }

    private Component title(String name, String translation) {
        String key = "advancements." + Moonlit.MOD_ID + "." + name + ".title";
        titles.add(Pair.of(key, translation));
        return Component.translatable(key);
    }

    private Component description(String name, String translation) {
        String key = "advancements." + Moonlit.MOD_ID + "." + name + ".description";
        descriptions.add(Pair.of(key, translation));
        return Component.translatable(key);
    }

    private Advancement.Builder create(String id, String title, String description, AdvancementHolder parent, AdvancementType type, boolean toast, boolean announce, boolean hidden) {
        return Advancement.Builder.advancement().parent(parent).display(ModItems.STARDUST, title(id, title), description(id, description), null, type, toast, announce, hidden);
    }

    private Advancement.Builder create(String id, String title, String description, Item display, AdvancementHolder parent, AdvancementType type, boolean toast, boolean announce, boolean hidden) {
        return Advancement.Builder.advancement().parent(parent).display(display, title(id, title), description(id, description), null, type, toast, announce, hidden);
    }
}
