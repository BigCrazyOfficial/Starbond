package io.github.bigcrazyofficial.item;

import io.github.bigcrazyofficial.Starbond;
import io.github.bigcrazyofficial.item.data.Components;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CustomModelData;

import java.util.List;
import java.util.function.Function;

public class Items {

    public static void initialize(){}

    public static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Starbond.MOD_ID, name));
        T item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }
    public static final Item UNBOUND_LOCKET = register("unbound_locket", UnboundLocketItem::new, new Item.Properties().stacksTo(1));
    public static final Item STARBOND_LOCKET  = register("starbond_locket", StarbondLocketItem::new, new Item.Properties()
            .stacksTo(1)
            .fireResistant()
            .useCooldown(0.5f)
            .component(Components.LOCKET_TICKS, 0)
            .component(Components.LOCKET_MODE, "channel")
            .component(Components.LOCKET_TEXTURE, "red"));
}
