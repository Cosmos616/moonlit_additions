package net.cosmos.moonlit_additions.init;

import com.farcr.nomansland.NoMansLand;
import com.farcr.nomansland.common.dreams.DreamType;
import com.farcr.nomansland.common.dreams.dreamtypes.MoonlightDreamType;
import net.cosmos.moonlit_additions.common.dreams.dream_types.TestDreamType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModDreamTypes {
    public static final DeferredRegister<DreamType> DREAM_TYPES_REGISTRY =
            DeferredRegister.create(ModRegistries.DREAM_TYPE, NoMansLand.MODID);

    public static final Supplier<DreamType> TEST_DREAM =
            DREAM_TYPES_REGISTRY.register("test_dream", TestDreamType::new);
}