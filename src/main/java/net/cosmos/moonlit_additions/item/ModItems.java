package net.cosmos.moonlit_additions.item;

import com.farcr.nomansland.client.renderer.FriendMoonRenderer;
import com.farcr.nomansland.common.block.moonlight.MoonlightBasinBlock;
import com.farcr.nomansland.common.blockentity.MoonlightBasinBlockEntity;
import com.farcr.nomansland.common.friend.FriendMoon;
import net.cosmos.moonlit_additions.MoonLitAdditions;
import net.cosmos.moonlit_additions.item.BronzeMaskItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MoonLitAdditions.MOD_ID);

    public static final DeferredItem<Item> MOONLIT_BRONZE = ITEMS.register("moonlit_bronze",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MOONLIT_WAX = ITEMS.register("moonlit_wax",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MOONLIT_ASH = ITEMS.register("moonlit_ash",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> STARDUST = ITEMS.register("stardust",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> STARDUST_BOTTLE = ITEMS.register("stardust_bottle",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BRONZE_MASK_ALLOMANCER =
            ITEMS.register(
                    "bronze_mask_allomancer",
                    () -> new BronzeMaskItem(
                            ModArmorMaterials.BRONZE_MASK_ALLOMANCER,
                            ArmorItem.Type.HELMET,
                            new Item.Properties()
                                    .durability(ArmorItem.Type.HELMET.getDurability(220))
                    )
            );

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
