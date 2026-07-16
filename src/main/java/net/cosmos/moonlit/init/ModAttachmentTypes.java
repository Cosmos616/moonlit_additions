package net.cosmos.moonlit.init;

import net.cosmos.moonlit.Moonlit;
import net.cosmos.moonlit.common.attachment.Reflection;
import net.cosmos.moonlit.common.world.meteor.Meteor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Moonlit.MOD_ID);

    public static final Supplier<AttachmentType<Meteor>> METEOR = ATTACHMENT_TYPES.register(
            "meteor", () -> AttachmentType.builder(() -> new Meteor(0)).serialize(Meteor.CODEC).build()
    );

    public static final Supplier<AttachmentType<Reflection>> REFLECTION = ATTACHMENT_TYPES.register(
            "reflection", () -> AttachmentType.builder(() -> Reflection.create()).serialize(Reflection.CODEC).build()
    );

    public static void register(IEventBus eventBus){
        ATTACHMENT_TYPES.register(eventBus);
    }
}
