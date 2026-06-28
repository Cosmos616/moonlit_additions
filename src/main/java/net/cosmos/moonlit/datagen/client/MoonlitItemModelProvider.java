package net.cosmos.moonlit.datagen.client;

import com.farcr.nomansland.common.definitions.ItemDefinition;
import net.cosmos.moonlit.Moonlit;
import net.cosmos.moonlit.init.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.TieredItem;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.loaders.SeparateTransformsModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Set;
import java.util.function.UnaryOperator;

import static net.cosmos.moonlit.Moonlit.moonlitPath;

public class MoonlitItemModelProvider extends ItemModelProvider {

    public MoonlitItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Moonlit.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        Set<ItemDefinition<?>> overrides = Set.of(
                ModItems.STARDUST,
                ModItems.BOTTLE_OF_STARDUST
        );

        for (ItemDefinition<?> definition : ModItems.ITEM_DEFINITIONS) {
            if (overrides.contains(definition) || definition.isBlockItem()) {
                continue;
            }
            if (definition.item() instanceof TieredItem) {
                handheld(definition);
            } else {
                basicItem(definition);
            }
        }

    }

    private ItemModelBuilder customModel(ItemDefinition<?> item, ResourceLocation parent) {
        return getBuilder(item.getKey().location().getPath()).parent(new ModelFile.UncheckedModelFile(parent));
    }

    private ItemModelBuilder customModel(ItemDefinition<?> item) {
        return getBuilder(item.getKey().location().getPath()).parent(new ModelFile.UncheckedModelFile(moonlitPath("item/" + item.getKey().location().getPath() + "/item")));
    }

    private void basicItem(ItemDefinition<?> item) {
        super.basicItem(item.get());
    }

    private ItemModelBuilder basicItem(ItemDefinition<?> item, String texture, String nameSuffix) {
        String name = BuiltInRegistries.ITEM.getKey(item.get()).getPath();
        return getBuilder(name + nameSuffix)
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", moonlitPath("item/%s".formatted(texture)));
    }

    private ItemModelBuilder basicItem(ItemDefinition<?> item, String texture) {
        return basicItem(item, texture, "");
    }

    private ItemModelBuilder basicItem(ItemDefinition<?> item, UnaryOperator<ResourceLocation> modelLocationModifier) {
        ResourceLocation name = item.getKey().location().withPrefix("item/");

        return getBuilder(modelLocationModifier.apply(name).getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", moonlitPath(modelLocationModifier.apply(name).getPath()));
    }

    private ResourceLocation handheld32(ItemDefinition<?> item) {
        return handheld(item, 32);
    }

    private ResourceLocation handheld48(ItemDefinition<?> item) {
        return handheld(item, 48);
    }

    private ResourceLocation handheld64(ItemDefinition<?> item) {
        return handheld(item, 64);
    }

    private ResourceLocation handheld32(ItemDefinition<?> item, String guiLocationModifier, String handheldLocationModifier) {
        return handheld(item, 32, loc -> loc.withSuffix("_" + guiLocationModifier), loc -> loc.withSuffix("_" + handheldLocationModifier));
    }

    private ResourceLocation handheld48(ItemDefinition<?> item, String guiLocationModifier, String handheldLocationModifier) {
        return handheld(item, 48, loc -> loc.withSuffix("_" + guiLocationModifier), loc -> loc.withSuffix("_" + handheldLocationModifier));
    }

    private ResourceLocation handheld64(ItemDefinition<?> item, String guiLocationModifier, String handheldLocationModifier) {
        return handheld(item, 64, loc -> loc.withSuffix("_" + guiLocationModifier), loc -> loc.withSuffix("_" + handheldLocationModifier));
    }

    private ItemModelBuilder handheld(ItemDefinition<?> item) {
        ResourceLocation name = item.getKey().location().withPrefix("item/");
        return getBuilder(name.getPath())
                .parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .texture("layer0", moonlitPath(name.getPath()));
    }

    private ResourceLocation handheld(ItemDefinition<?> item, int x) {
        return handheld(item, x, UnaryOperator.identity(), UnaryOperator.identity());
    }

    private ResourceLocation handheld(ItemDefinition<?> item, int x, UnaryOperator<ResourceLocation> guiLocationModifier, UnaryOperator<ResourceLocation> handheldLocationModifier) {
        ResourceLocation name = item.getKey().location().withPrefix("item/");
        super.withExistingParent(handheldLocationModifier.apply(name).getPath(), moonlitPath("item/templates/handheld%sx".formatted(x)))
                .texture("layer0", name);
        separateTransform(item);
        basicItem(item, guiLocationModifier);
        return name;
    }

    private void separateTransform(ItemDefinition<?> item) {
        item.unwrapKey().ifPresent(
                itemName -> {
                    ResourceLocation itemModelLoc = itemName.location().withPrefix("item/");
                    ItemModelBuilder gui = super.nested().parent(new ModelFile.UncheckedModelFile(itemModelLoc.withSuffix("_gui")));
                    ItemModelBuilder twoDim = super.nested().parent(new ModelFile.UncheckedModelFile(itemModelLoc.withSuffix("_handheld")));
                    super.withExistingParent(itemModelLoc.getPath(), mcLoc("item/handheld"))
                            .customLoader(SeparateTransformsModelBuilder::begin)
                            .perspective(ItemDisplayContext.GUI, gui)
                            .perspective(ItemDisplayContext.FIXED, twoDim)
                            .base(twoDim);
                });
    }

    public String itemName(Item item) {
        ResourceLocation location = BuiltInRegistries.ITEM.getKey(item);
        return location.getPath();
    }
}
