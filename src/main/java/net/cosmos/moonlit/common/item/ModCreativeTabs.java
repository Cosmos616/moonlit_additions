package net.cosmos.moonlit.common.item;

import net.cosmos.moonlit.Moonlit;
import net.cosmos.moonlit.init.ModBlocks;
import net.cosmos.moonlit.init.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Moonlit.MOD_ID);

    public static final Supplier<CreativeModeTab> MOONLIT_DECOR = CREATIVE_MODE_TAB.register("moonlit_decor",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.MOONLIT_BRONZE_INGOT.get()))
                    .title(Component.translatable("itemGroup.moonlit"))
                    .displayItems((itemDisplayParameters, output) -> {
                        for (var registry : ModItems.ITEMS.getEntries()) {
                            if (registry.get() instanceof BlockItem) continue;
                            output.accept(registry.get());
                        }
                        for (var registry : ModBlocks.BLOCKS.getEntries()) {
                            if (registry.get().asItem() == Items.AIR) continue;
                            output.accept(registry.get());
                        }
                    }).build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
