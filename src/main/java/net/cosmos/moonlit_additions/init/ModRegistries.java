package net.cosmos.moonlit_additions.init;

import com.farcr.nomansland.NoMansLand;
import com.farcr.nomansland.common.dreams.DreamType;
import net.cosmos.moonlit_additions.MoonlitAdditions;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ModRegistries {
    public static final ResourceKey<Registry<DreamType>> DREAM_TYPE_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MoonlitAdditions.MOD_ID,"dreams/dream_types"));
    public static final Registry<DreamType> DREAM_TYPE = new RegistryBuilder<>(DREAM_TYPE_KEY).create();
}
