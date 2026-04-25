package io.github.bigcrazyofficial;

import io.github.bigcrazyofficial.block.Blocks;
import io.github.bigcrazyofficial.item.Items;
import io.github.bigcrazyofficial.item.data.Components;
import io.netty.util.AttributeMap;
import net.fabricmc.api.ModInitializer;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Starbond implements ModInitializer {
	public static final String MOD_ID = "starbond";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Holder<MobEffect> CAMARADERIE =
			Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,
					Identifier.fromNamespaceAndPath(MOD_ID, "camaraderie"),
					new CamaraderieEffect(MobEffectCategory.BENEFICIAL, 13458603)
							.addAttributeModifier(
									Attributes.MOVEMENT_SPEED,
									Identifier.fromNamespaceAndPath(MOD_ID, "effect.camaraderie.movement_speed"),
									0.1F,
									AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
									)
							.addAttributeModifier(Attributes.MINING_EFFICIENCY,
									Identifier.fromNamespaceAndPath(MOD_ID, "effect.camaraderie.mining_efficiency"),
									0.08F,
									AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
							.addAttributeModifier(Attributes.MAX_HEALTH,
									Identifier.fromNamespaceAndPath(MOD_ID, "effect.camaraderie.max_health"),
									2F,
									AttributeModifier.Operation.ADD_VALUE));
	@Override
	public void onInitialize() {
		Items.initialize();
		Blocks.initialize();
		Components.initialize();
	}
}