package io.github.bigcrazyofficial;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class Sounds {
    private static SoundEvent registerSound(String id) {
        Identifier identifier = Identifier.fromNamespaceAndPath(Starbond.MOD_ID, id);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, identifier, SoundEvent.createVariableRangeEvent(identifier));
    }
    public static final SoundEvent ITEM_LOCKET_SHATTER = registerSound("locket_shatter");
    public static final SoundEvent UI_CLICK_FANCY = registerSound("click_fancy");


    public static void initialize() {
    }
}
