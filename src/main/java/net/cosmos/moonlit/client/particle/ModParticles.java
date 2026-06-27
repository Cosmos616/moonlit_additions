package net.cosmos.moonlit.client.particle;

import net.cosmos.moonlit.Moonlit;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, Moonlit.MOD_ID);

    public static final Supplier<SimpleParticleType> METEOR_AURORA = PARTICLES.register("meteor_aurora", () -> new SimpleParticleType(true));

}
