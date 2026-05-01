package io.github.bigcrazyofficial.item.data;

import com.mojang.serialization.Codec;
import io.github.bigcrazyofficial.Starbond;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class Components {
    //not to be confused with CardinalComponents
    public static final DataComponentType<String> LOCKET_MODE = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(Starbond.MOD_ID, "locket_mode"),
            DataComponentType.<String>builder().persistent(Codec.STRING).build()
    );
    public static final DataComponentType<String> LOCKET_TEXTURE = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(Starbond.MOD_ID, "locket_texture"),
            DataComponentType.<String>builder().persistent(Codec.STRING).build()
    );
    public static final DataComponentType<Integer> LOCKET_TICKS = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(Starbond.MOD_ID, "locket_ticks"),
            DataComponentType.<Integer>builder().persistent(Codec.INT).build()
    );
    public static void initialize(){}
}
