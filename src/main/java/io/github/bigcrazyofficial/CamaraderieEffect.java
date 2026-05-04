package io.github.bigcrazyofficial;

import io.github.bigcrazyofficial.item.Items;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Set;

public class CamaraderieEffect extends MobEffect {
    protected CamaraderieEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(final ServerLevel serverLevel, final LivingEntity mob, final int amplification) {
        Player player = (Player) mob;
        //Don't question it.
        if(!player.getInventory().hasAnyOf(Set.of(Items.STARBOND_PENDANT))){
            player.removeEffect(Starbond.CAMARADERIE);
        }
        return super.applyEffectTick(serverLevel, mob, amplification);
    }
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the effect every tick
        return true;
    }
}
