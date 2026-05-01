package io.github.bigcrazyofficial.item.event;

import io.github.bigcrazyofficial.Starbond;
import io.github.bigcrazyofficial.data.CardinalComponents;
import io.github.bigcrazyofficial.data.base.BondData;
import io.github.bigcrazyofficial.item.StarbondLocketItem;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class ChannelPreventDamageEvent {
    public static void illSplitYouFourWays(){
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, v) -> {
            Level level = entity.level();
            if(entity instanceof Player && !level.isClientSide()){
                UUID uuid = entity.getUUID();
                BondData data = level.getScoreboard().getComponent(CardinalComponents.BOND).getBondEntry(entity.getComponent(CardinalComponents.BOND_REFERENCE).getReference());
                Starbond.LOGGER.info(String.valueOf(data.otherPlayerChanneling(uuid)));
                if(data.otherPlayerChanneling(uuid)){
                    Player other = level.getPlayerInAnyDimension(StarbondLocketItem.whom(data.playerA(), data.playerB(), entity).get(1));
                    assert other != null;
                    other.hurtServer((ServerLevel) level, new DamageSource(
                            level.registryAccess()
                                    .lookupOrThrow(Registries.DAMAGE_TYPE)
                                    .get(Starbond.CHANNELED.identifier()).orElseThrow()
                    ), v);
                    return false;
                }
            }
            return true;
    });
    }
}
