package io.github.bigcrazyofficial.item.helper;

import net.minecraft.ChatFormatting;
import net.minecraft.util.Mth;

import java.util.Map;

public class LocketTooltipHelper {
    public static final Map<String, Integer> LOCKET_TOOLTIP_COLORS = Map.of(
            "default", ChatFormatting.GRAY.getColor(),
            "blue", Mth.hsvToRgb(0.98f, 0.9f, 0.95f),
            "red", Mth.hsvToRgb(0.63f, 0.75f, 0.95f)
    );
}
