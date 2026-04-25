package io.github.bigcrazyofficial.item;

import io.github.bigcrazyofficial.Starbond;
import io.github.bigcrazyofficial.data.CardinalComponents;
import io.github.bigcrazyofficial.data.base.BondData;
import io.github.bigcrazyofficial.item.data.Components;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StarbondLocketItem extends Item {
    public StarbondLocketItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        Scoreboard board = level.getScoreboard();
        BondData data;
        if (player.getComponent(CardinalComponents.BOND_REFERENCE).getReference() == 0) {
            return InteractionResult.FAIL;
        }
        data = board.getComponent(CardinalComponents.BOND).getBondEntry(player.getComponent(CardinalComponents.BOND_REFERENCE).getReference());
        switch (player.getItemInHand(hand).get(Components.LOCKET_MODE)) {
            case "store":
                this.locketStorage(level, player, player.getUseItem(), data);
            case "teleport":
                this.locketTP(level, player, player.getUseItem(), data);
            case null:
            default:
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult interactLivingEntity(final ItemStack itemStack, final Player player, final LivingEntity target, final InteractionHand type) {
        Level level = player.level();
        if (target instanceof Player && !level.isClientSide()) {
            if (player.getComponent(CardinalComponents.BOND_REFERENCE).getReference() == 0 &&
                    target.getComponent(CardinalComponents.BOND_REFERENCE).getReference() == 0) {
                int i = level.getRandom().nextInt(0, 10000);
                level.getScoreboard().getComponent(CardinalComponents.BOND).addBondEntry(i, player.getUUID(), target.getUUID());
                player.getComponent(CardinalComponents.BOND_REFERENCE).postReference(i);
                target.getComponent(CardinalComponents.BOND_REFERENCE).postReference(i);
                Starbond.LOGGER.info("Hooray!");
            }
        }
        return InteractionResult.PASS;
    }

    public void locketStorage(Level level, Player player, ItemStack stack, BondData data) {
        player.openMenu(data);
    }

    public void locketTP(Level level, Player player, ItemStack stack, BondData data) {
        UUID playerA = data.playerA();
        UUID playerB = data.playerB();

        if (level.getPlayerInAnyDimension(playerA) != null && level.getPlayerInAnyDimension(playerB) != null) {
            if (level instanceof ServerLevel && player instanceof ServerPlayer) {
                Player teleportTo = level.getPlayerInAnyDimension(whom(playerA, playerB, player).get(1));
                Starbond.LOGGER.info(String.valueOf(teleportTo.getUUID().toString()));
                player.teleport(new TeleportTransition(
                        (ServerLevel) level,
                        teleportTo.position(),
                        Vec3.ZERO,
                        0.0f, 0.0f,
                        Relative.union(Relative.ROTATION, Relative.DELTA), TeleportTransition.DO_NOTHING
                ));
                player.resetFallDistance();
                player.resetCurrentImpulseContext();
            }
        }
    }

    @Override
    public void inventoryTick(final ItemStack itemStack, final ServerLevel level, final Entity owner, @Nullable final EquipmentSlot slot) {
        if(owner.getComponent(CardinalComponents.BOND_REFERENCE).getReference() != 0) {
            BondData data = level.getScoreboard().getComponent(CardinalComponents.BOND).getBondEntry(owner.getComponent(CardinalComponents.BOND_REFERENCE).getReference());
            UUID playerA = data.playerA();
            UUID playerB = data.playerB();

            if(owner instanceof Player &&
                    level.getPlayerInAnyDimension(playerA) != null &&
                    level.getPlayerInAnyDimension(playerB) != null) {
                UUID other  = whom(playerA, playerB, owner).get(1);
                float dist = owner.distanceTo(level.getPlayerInAnyDimension(other));
                if(dist <= 50.0f){
                    ((Player) owner).addEffect(new MobEffectInstance(Starbond.CAMARADERIE, MobEffectInstance.INFINITE_DURATION, 0));
                } else {
                    ((Player) owner).removeEffect(Starbond.CAMARADERIE);
                }
            }
        }
    }

    public List<UUID> whom(UUID playerA, UUID playerB, Entity user){
        if(user.getUUID() == playerA){
            return List.of(playerA, playerB);
        } else {
            return List.of(playerB, playerA);

        }
    }
}
