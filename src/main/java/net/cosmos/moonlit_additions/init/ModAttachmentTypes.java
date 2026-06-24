package net.cosmos.moonlit_additions.init;

import com.mojang.serialization.Codec;
import net.cosmos.moonlit_additions.MoonlitAdditions;
import net.cosmos.moonlit_additions.common.world.meteor.Meteor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachmentTypes {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MoonlitAdditions.MOD_ID);

    public static final Supplier<AttachmentType<Meteor>> METEOR = ATTACHMENT_TYPES.register(
            "meteor", () -> AttachmentType.builder(() -> new Meteor(0)).serialize(Meteor.CODEC).build()
    );

    public static void register(IEventBus eventBus){
        ATTACHMENT_TYPES.register(eventBus);
    }
}
