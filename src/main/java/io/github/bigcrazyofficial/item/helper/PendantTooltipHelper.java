package io.github.bigcrazyofficial.item.helper;

import net.minecraft.ChatFormatting;
import net.minecraft.util.Mth;

import java.util.Map;

public class PendantTooltipHelper {
    public static final Map<String, Integer> PENDANT_TOOLTIP_COLORS = Map.of(
            "default", ChatFormatting.GRAY.getColor(),
            "red", Mth.hsvToRgb(0.98f, 0.9f, 0.95f),
            "blue", Mth.hsvToRgb(0.63f, 0.75f, 0.95f),
            "magenta", Mth.hsvToRgb(0.88f, 0.8f, 0.9f),
            "lime", Mth.hsvToRgb(0.394f, 0.95f, 1f)
            );
}
