package net.cosmos.moonlit_additions.item;

import net.cosmos.moonlit_additions.MoonLitAdditions;
import net.cosmos.moonlit_additions.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.farcr.nomansland.common.block.*;
import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MoonLitAdditions.MOD_ID);

    public static final Supplier<CreativeModeTab> MOONLIT_DECOR = CREATIVE_MODE_TAB.register("moonlit_decor",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.MOONLIT_BRONZE.get()))
                    .title(Component.translatable("creative_tab.moonlit_additions.moonlit_decor"))
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
