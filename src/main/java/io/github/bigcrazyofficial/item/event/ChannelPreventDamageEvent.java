package io.github.bigcrazyofficial.item.event;

import io.github.bigcrazyofficial.Starbond;
import io.github.bigcrazyofficial.data.CardinalComponents;
import io.github.bigcrazyofficial.data.base.BondData;
import io.github.bigcrazyofficial.item.Items;
import io.github.bigcrazyofficial.item.StarbondPendantItem;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Set;
import java.util.UUID;

public class ChannelPreventDamageEvent {
    public static void illSplitYouFourWays(){
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, v) -> {
            Level level = entity.level();
            if(entity instanceof Player && !level.isClientSide() && ((Player) entity).getInventory().hasAnyOf(Set.of(Items.STARBOND_PENDANT))) {
                BondData data = level.getScoreboard().getComponent(CardinalComponents.BOND).getBondEntry(entity.getComponent(CardinalComponents.BOND_REFERENCE).getReference());
                if (data != null) {
                    UUID uuid = entity.getUUID();
                    if(data.otherPlayerChanneling(uuid)) {
                        Player other = level.getPlayerInAnyDimension(StarbondPendantItem.whom(data.playerA(), data.playerB(), entity).get(1));
                        assert other != null;
                        other.hurtServer((ServerLevel) level, new DamageSource(
                                level.registryAccess()
                                        .lookupOrThrow(Registries.DAMAGE_TYPE)
                                        .get(Starbond.CHANNELED.identifier()).orElseThrow()
                        ), v);
                        return false;
                    }
                }
            }
            return true;
    });

        ServerLivingEntityEvents.ALLOW_DEATH.register(((entity, damageSource, damageAmount) -> {
            Level level = entity.level();
            if(entity instanceof Player && !level.isClientSide() && ((Player) entity).getInventory().hasAnyOf(Set.of(Items.STARBOND_PENDANT))) {
                BondData data = level.getScoreboard().getComponent(CardinalComponents.BOND).getBondEntry(entity.getComponent(CardinalComponents.BOND_REFERENCE).getReference());
                if (data != null) {
                    UUID uuid = entity.getUUID();
                    if (data.otherPlayerChanneling(uuid)) {
                        entity.setHealth(5.0f);
                        entity.setAbsorptionAmount(10f);
                        Player other = level.getPlayerInAnyDimension(StarbondPendantItem.whom(data.playerA(), data.playerB(), entity).get(1));
                        assert other != null;
                        other.hurtServer((ServerLevel) level, new DamageSource(
                                level.registryAccess()
                                        .lookupOrThrow(Registries.DAMAGE_TYPE)
                                        .get(Starbond.CHANNELED.identifier()).orElseThrow()
                        ), damageAmount * 2);
                        return false;
                    }
                }
            }
            return true;
        }));
    }
}
