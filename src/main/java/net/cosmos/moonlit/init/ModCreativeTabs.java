package net.cosmos.moonlit.init;

import net.cosmos.moonlit.Moonlit;
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
                        var blocks = ModBlocks.BLOCK_DEFINITIONS;
                        for (var woodSet : ModBlocks.WOODSETS) {
                            blocks.removeIf(blockDef -> blockDef == woodSet.ornate() || blockDef == woodSet.ornatePillar() || blockDef == woodSet.pillar() || blockDef == woodSet.wrappedPillarBase() || blockDef == woodSet.wrappedBeam());
                        }
                        for (var block : blocks) {
                            if (block.get().asItem() == Items.AIR) continue;
                            output.accept(block.get());
                        }
                    }).build());

    public static final Supplier<CreativeModeTab> MOONLIT_WOODWORKING = CREATIVE_MODE_TAB.register("moonlit_woodworking",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.WALNUT.ornate()))
                    .title(Component.translatable("itemGroup.moonlit.woodworking"))
                    .displayItems((itemDisplayParameters, output) -> {
                        for (var woodSet : ModBlocks.WOODSETS) {
                            output.accept(woodSet.ornate());
                            output.accept(woodSet.ornatePillar());
                            output.accept(woodSet.pillar());
                            output.accept(woodSet.wrappedPillarBase());
                            output.accept(woodSet.wrappedBeam());
                        }
                    }).build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
