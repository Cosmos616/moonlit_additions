package net.cosmos.moonlit.init;

import com.farcr.nomansland.common.definitions.ItemDefinition;
import com.google.common.collect.Sets;
import net.cosmos.moonlit.Moonlit;
import net.cosmos.moonlit.common.item.BronzeMaskItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Moonlit.MOD_ID);
    public static List<ItemDefinition<?>> ITEM_DEFINITIONS = new ArrayList<>();
    public BuildCreativeModeTabContentsEvent event = null;

    public static LinkedHashSet<ItemDefinition<?>> CREATIVE_TAB_ITEMS = Sets.newLinkedHashSet();

    public static final ItemDefinition<Item> MOONLIT_BRONZE_INGOT = register("moonlit_bronze_ingot", () -> new Item(new Item.Properties()));
    public static final ItemDefinition<Item> MOONLIT_WAX = register("moonlit_wax", () -> new Item(new Item.Properties()));
    public static final ItemDefinition<Item> MOONLIT_ASH = register("moonlit_ash", () -> new Item(new Item.Properties()));
    public static final ItemDefinition<Item> STARDUST = register("stardust", () -> new Item(new Item.Properties()));
    public static final ItemDefinition<Item> BOTTLE_OF_STARDUST = register("bottle_of_stardust", () -> new Item(new Item.Properties()));
    public static final ItemDefinition<Item> METEORIC_IRON_INGOT = register("meteoric_iron_ingot", () -> new Item(new Item.Properties()));
    public static final ItemDefinition<Item> RAW_METEORIC_IRON = register("raw_meteoric_iron", () -> new Item(new Item.Properties()));

    public static final ItemDefinition<Item> FAILED_SUN = register("failed_sun", () -> new Item(new Item.Properties()));

    public static final ItemDefinition<Item> BRONZE_MASK_ALLOMANCER = register(
                    "bronze_mask_allomancer",
                    () -> new BronzeMaskItem(
                            ModArmorMaterials.BRONZE_MASK_ALLOMANCER,
                            ArmorItem.Type.HELMET,
                            new Item.Properties()
                                    .durability(ArmorItem.Type.HELMET.getDurability(220))
                    ), true
            );

    public static <T extends Item> ItemDefinition<T> registerWithoutTab(String name, Supplier<T> item, boolean customLang) {
        DeferredItem<T> deferred = ITEMS.register(name, item);
        ItemDefinition<T> definition = ItemDefinition.fromHolder(deferred, customLang);
        ITEM_DEFINITIONS.add(definition);
        return definition;
    }

    public static <T extends Item> ItemDefinition<T> register(String name, Supplier<T> item, boolean customLang) {
        ItemDefinition<T> definition = registerWithoutTab(name, item, customLang);
        CREATIVE_TAB_ITEMS.add(definition);
        return definition;
    }

    public static <T extends Item> ItemDefinition<T> registerWithoutTab(String name, Supplier<T> item) {
        return registerWithoutTab(name, item, false);
    }

    public static <T extends Item> ItemDefinition<T> register(String name, Supplier<T> item) {
        return register(name, item, false);
    }

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
