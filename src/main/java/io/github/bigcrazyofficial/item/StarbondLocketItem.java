package io.github.bigcrazyofficial.item;

import io.github.bigcrazyofficial.Sounds;
import io.github.bigcrazyofficial.Starbond;
import io.github.bigcrazyofficial.data.CardinalComponents;
import io.github.bigcrazyofficial.data.base.BondData;
import io.github.bigcrazyofficial.item.data.Components;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StarbondLocketItem extends Item  {
    private static String[] UPGRADES = {
            "store",
            "teleport",
            "channel"
    };
    public StarbondLocketItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(!player.getCooldowns().isOnCooldown(stack)) {
            Scoreboard board = level.getScoreboard();
            BondData data;
            if (player.getComponent(CardinalComponents.BOND_REFERENCE).getReference() == 0) {
                return InteractionResult.FAIL;
            }
            data = board.getComponent(CardinalComponents.BOND).getBondEntry(player.getComponent(CardinalComponents.BOND_REFERENCE).getReference());
            if (!player.isCrouching()) {
                switch (stack.get(Components.LOCKET_MODE)) {
                    case "store":
                        this.locketStorage(level, player, stack, data);
                    case "teleport":
                        this.locketTP(level, player, stack, data);
                    case "channel":
                        if (!data.otherPlayerChanneling(player.getUUID())) {
                            player.startUsingItem(hand);
                        }
                    case null:
                    default:
                }
            } else {
                swapUpgrade(stack, stack.get(Components.LOCKET_MODE));
                level.playPlayerSound(SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 1f, 0.5f);
            }
            player.getCooldowns().addCooldown(stack, 10);
        }
        return InteractionResult.PASS;
    }
    public void swapUpgrade(ItemStack stack, String active){
        int pos = Arrays.asList(UPGRADES).indexOf(active);
        if(pos++ < UPGRADES.length + 1){
            pos = 0;
        }
        stack.set(Components.LOCKET_MODE, Arrays.asList(UPGRADES).get(pos--));

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
                level.playLocalSound(player, SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
                player.resetFallDistance();
                player.resetCurrentImpulseContext();
            }
        }
    }

    @Override
    public void inventoryTick(final ItemStack itemStack, final ServerLevel level, final Entity owner, @Nullable final EquipmentSlot slot) {
        if (owner.getComponent(CardinalComponents.BOND_REFERENCE).getReference() != 0) {
            BondData data = level.getScoreboard().getComponent(CardinalComponents.BOND).getBondEntry(owner.getComponent(CardinalComponents.BOND_REFERENCE).getReference());
            if (data.suicideMode()) {
                killItselfFancily(owner, (Player) owner, level, owner.getComponent(CardinalComponents.BOND_REFERENCE).getReference());
                itemStack.shrink(1);
                return;
            }
            UUID playerA = data.playerA();
            UUID playerB = data.playerB();
            if (owner instanceof Player &&
                    level.getPlayerInAnyDimension(playerA) != null &&
                    level.getPlayerInAnyDimension(playerB) != null) {
                UUID other = whom(playerA, playerB, owner).get(1);
                float dist = owner.distanceTo(level.getPlayerInAnyDimension(other));
                if (dist <= 50.0f && level.getPlayerInAnyDimension(other) != null) {
                    ((Player) owner).addEffect(new MobEffectInstance(Starbond.CAMARADERIE, MobEffectInstance.INFINITE_DURATION, 0));
                    data.setTickCamaraderie();
                    itemStack.set(Components.LOCKET_TICKS, data.camaraderieTicks());
                } else {
                    ((Player) owner).removeEffect(Starbond.CAMARADERIE);
                }
            } else {
                assert owner instanceof Player;
                ((Player) owner).removeEffect(Starbond.CAMARADERIE);
            }
        }
    }

    @Override
    public void onUseTick(final Level level, final LivingEntity livingEntity, final ItemStack itemStack, final int ticksRemaining) {
        if (!level.isClientSide()) {
            BondData data = level.getScoreboard().getComponent(CardinalComponents.BOND).getBondEntry(livingEntity.getComponent(CardinalComponents.BOND_REFERENCE).getReference());
            if (whom(data.playerA(), data.playerB(), livingEntity).getFirst() == data.playerA()) {
                data.setPlayerAChanneling(true);
            } else {
                data.setPlayerBChanneling(true);
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(final ItemStack itemStack, final Level level, final LivingEntity entity) {
        if (!level.isClientSide()){
            BondData data = level.getScoreboard().getComponent(CardinalComponents.BOND).getBondEntry(entity.getComponent(CardinalComponents.BOND_REFERENCE).getReference());
            if (whom(data.playerA(), data.playerB(), entity).getFirst() == data.playerA()) {
                data.setPlayerAChanneling(false);
            } else {
                data.setPlayerBChanneling(false);
            }
        }
        return itemStack;
    }

    @Override
    public void onDestroyed(final ItemEntity itemEntity) {
        Entity entity = itemEntity.getOwner();
        Player owner = (entity instanceof Player) ? (Player) entity : null;
        Level level = itemEntity.level();
        level.playSound(itemEntity, itemEntity.getOnPos(), Sounds.ITEM_LOCKET_SHATTER, SoundSource.NEUTRAL);
        if(owner != null && !level.isClientSide()){
            int ref = owner.getComponent(CardinalComponents.BOND_REFERENCE).getReference();
            if(ref != 0){
                killItselfFancily(itemEntity, owner, level, ref);
            }
        }
    }

    public void killItselfFancily(Entity entity, Player owner, Level level, int ref){
        BondData data = level.getScoreboard().getComponent(CardinalComponents.BOND).getBondEntry(ref);
        owner.getComponent(CardinalComponents.BOND_REFERENCE).postReference(0);
        owner.removeEffect(Starbond.CAMARADERIE);
        level.playSound(null, owner.getOnPos(), Sounds.ITEM_LOCKET_SHATTER, SoundSource.NEUTRAL);
        data.activateSuicideMode();
    }

    public static List<UUID> whom(UUID playerA, UUID playerB, Entity user){
        if(user.getUUID() == playerA){
            return List.of(playerA, playerB);
        } else {
            return List.of(playerB, playerA);

        }
    }

    @Override
    public ItemUseAnimation getUseAnimation(final ItemStack itemStack) {
        return ItemUseAnimation.BLOCK;
    }

    @Override
    public boolean allowComponentsUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }


}
