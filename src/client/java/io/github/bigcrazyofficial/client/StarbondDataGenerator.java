package io.github.bigcrazyofficial.client;

import io.github.bigcrazyofficial.client.datagen.StarbondLootTableProvider;
import io.github.bigcrazyofficial.client.datagen.StarbondModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.world.Container;

public class StarbondDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(StarbondModelProvider::new);
		pack.addProvider(StarbondLootTableProvider::new);
	}
}
