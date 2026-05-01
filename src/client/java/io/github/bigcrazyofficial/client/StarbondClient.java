package io.github.bigcrazyofficial.client;

import io.github.bigcrazyofficial.client.screen.LocketInventoryScreen;
import io.github.bigcrazyofficial.item.data.Components;
import io.github.bigcrazyofficial.item.helper.LocketTooltipHelper;
import io.github.bigcrazyofficial.menu.MenuTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;

public class StarbondClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(MenuTypes.LOCKET_INVENTORY, LocketInventoryScreen::new);
		ItemTooltipCallback.EVENT.register((stack, context, type, tooltip) -> {
			String texture = stack.get(Components.LOCKET_TEXTURE);
			if (texture != null) {
				tooltip.add(Component.translatable("item.starbond.locket.texture." + texture)
						.withColor(LocketTooltipHelper.LOCKET_TOOLTIP_COLORS.get(texture)));
			}
		});
	}
}