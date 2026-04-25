package io.github.bigcrazyofficial.client.datagen;

import io.github.bigcrazyofficial.block.Blocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class StarbondLootTableProvider extends FabricBlockLootSubProvider {
    public StarbondLootTableProvider(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(packOutput, registriesFuture);
    }

    @Override
    public void generate() {
        dropSelf(Blocks.BINDING_TABLE);
    }
}
