package net.cosmos.moonlit.init;

import com.farcr.nomansland.common.dreams.DreamType;
import com.farcr.nomansland.common.registry.NMLRegistries;
import net.cosmos.moonlit.Moonlit;
import net.cosmos.moonlit.common.dreams.dream_types.TestDreamType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModDreamTypes {
    public static final DeferredRegister<DreamType> DREAM_TYPES_REGISTRY = DeferredRegister.create(NMLRegistries.DREAM_TYPE, Moonlit.MOD_ID);

    public static final Supplier<DreamType> TEST_DREAM =
            DREAM_TYPES_REGISTRY.register("test_dream", TestDreamType::new);

    public static void register(IEventBus eventBus){
        DREAM_TYPES_REGISTRY.register(eventBus);
    }
}
