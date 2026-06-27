package net.cosmos.moonlit.common.entity;

import net.cosmos.moonlit.Moonlit;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Moonlit.MOD_ID);

    public static final Supplier<EntityType<ShockwaveProjectorEntity>> SHOCKWAVE_PROJECTOR =
            ENTITY_TYPES.register(
                    "shockwave_projector",
                    () -> EntityType.Builder.<ShockwaveProjectorEntity>of(
                                    ShockwaveProjectorEntity::new,
                                    MobCategory.MISC
                            )
                            .sized(0.1F, 0.1F)
                            .clientTrackingRange(96)
                            .updateInterval(1)
                            .build("shockwave_projector")
            );
}
