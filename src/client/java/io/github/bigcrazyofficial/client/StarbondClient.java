package io.github.bigcrazyofficial.client;

import io.github.bigcrazyofficial.client.screen.LocketInventoryScreen;
import io.github.bigcrazyofficial.menu.MenuTypes;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class StarbondClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(MenuTypes.LOCKET_INVENTORY, LocketInventoryScreen::new);
	}
}