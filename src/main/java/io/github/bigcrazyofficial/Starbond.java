package io.github.bigcrazyofficial;

import io.github.bigcrazyofficial.item.event.ChannelPreventDamageEvent;
import io.github.bigcrazyofficial.item.Items;
import io.github.bigcrazyofficial.item.data.Components;
import io.github.bigcrazyofficial.menu.MenuTypes;
import net.fabricmc.api.ModInitializer;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Starbond implements ModInitializer {
	public static final String MOD_ID = "starbond";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ResourceKey<DamageType> CHANNELED = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(MOD_ID, "channeled"));

	public static final Holder<MobEffect> CAMARADERIE =
			Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,
					Identifier.fromNamespaceAndPath(MOD_ID, "camaraderie"),
					new CamaraderieEffect(MobEffectCategory.BENEFICIAL, 13458603)
							.addAttributeModifier(
									Attributes.MOVEMENT_SPEED,
									Identifier.fromNamespaceAndPath(MOD_ID, "effect.camaraderie.movement_speed"),
									0.075F,
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
		Sounds.initialize();
		Components.initialize();
		MenuTypes.initialize();
		ChannelPreventDamageEvent.illSplitYouFourWays();

	}
}