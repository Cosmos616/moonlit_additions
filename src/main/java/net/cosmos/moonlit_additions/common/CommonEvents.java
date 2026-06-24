package net.cosmos.moonlit_additions.common;

import net.cosmos.moonlit_additions.MoonlitAdditions;
import net.cosmos.moonlit_additions.common.commands.MeteorCommand;
import net.cosmos.moonlit_additions.network.ArouraParticlesPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = MoonlitAdditions.MOD_ID)
public class CommonEvents {

    @SubscribeEvent
    public static void registerListeners(RegisterCommandsEvent event) {
        MeteorCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MoonlitAdditions.MOD_ID).versioned("1.0");

        registrar.playToClient(ArouraParticlesPayload.TYPE, ArouraParticlesPayload.STREAM_CODEC, ArouraParticlesPayload::handle);
    }

}
