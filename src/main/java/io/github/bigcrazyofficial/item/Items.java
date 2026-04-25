package io.github.bigcrazyofficial.item;

import io.github.bigcrazyofficial.Starbond;
import io.github.bigcrazyofficial.item.data.Components;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class Items {

    public static void initialize(){}

    public static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Starbond.MOD_ID, name));
        T item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }
    public static final Item STARBOND_LOCKET  = register("starbond_locket", StarbondLocketItem::new, new Item.Properties()
            .component(Components.LOCKET_MODE, "teleport"));
}
