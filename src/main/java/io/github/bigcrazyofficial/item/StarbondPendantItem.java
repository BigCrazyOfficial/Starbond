package io.github.bigcrazyofficial.item;

import io.github.bigcrazyofficial.Sounds;
import io.github.bigcrazyofficial.Starbond;
import io.github.bigcrazyofficial.data.CardinalComponents;
import io.github.bigcrazyofficial.data.base.BondData;
import io.github.bigcrazyofficial.item.data.Components;
import io.github.bigcrazyofficial.item.helper.PendantTooltipHelper;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.ResolutionContext;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
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

import java.util.*;

public class StarbondPendantItem extends Item  {
    private static String[] UPGRADES = {
            "store",
            "teleport",
            "channel"
    };
    public StarbondPendantItem(Properties properties) {
        super(properties);
    }

    //at this point, even I'm not sure all how this works
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
            if(player.isCrouching()) {
                if(!level.isClientSide()) {
                    swapUpgrade(stack, stack.get(Components.PENDANT_MODE), player, level);
                    level.playSound(null, player.getOnPos(), Sounds.UI_CLICK_FANCY, SoundSource.PLAYERS, 1f, 0.9f);
                    return InteractionResult.PASS;
                }
            } else {
                if (!level.isClientSide()) {
                    switch (stack.get(Components.PENDANT_MODE)) {
                        case "store":
                            this.pendantStorage(level, player, stack, data);
                            break;
                        case "teleport":
                            if (!data.otherPlayerTeleporting(player.getUUID())) {
                                if(data.teleportCooldown() <= 0){
                                    this.initTP(level, player, data);
                                } else {
                                    if(!level.isClientSide() && player instanceof ServerPlayer serverPlayer){
                                        serverPlayer.connection
                                                .send(new ClientboundSetActionBarTextPacket(Component.translatable("item.starbond.starbond_pendant.teleportCooldown")
                                                        .withColor(PendantTooltipHelper.PENDANT_TOOLTIP_COLORS.get(stack.get(Components.PENDANT_TEXTURE)))));
                                    }
                                }
                            }
                            break;
                        case "channel":
                            if (!data.otherPlayerChanneling(player.getUUID())) {
                                player.startUsingItem(hand);
                            }
                            break;
                        case null, default:
                            break;
                    }
                }
            }
            player.getCooldowns().addCooldown(stack, 10);
            return InteractionResult.PASS;
        }
        return InteractionResult.FAIL;
    }

    public void swapUpgrade(ItemStack stack, String active, Player player, Level level){
        int pos = Arrays.asList(UPGRADES).indexOf(active);
        String current = stack.get(Components.PENDANT_MODE);
        if(Arrays.asList(UPGRADES).getLast().equals(current)){
            pos = 0;
        } else {
            pos++;
        }
        stack.set(Components.PENDANT_MODE, Arrays.asList(UPGRADES).get(pos));
        if(!level.isClientSide() && player instanceof ServerPlayer serverPlayer){
            serverPlayer.connection
                    .send(new ClientboundSetActionBarTextPacket(Component.translatable("item.starbond.starbond_pendant.modeSwap", Objects.requireNonNull(stack.get(Components.PENDANT_MODE)).toUpperCase())));
        }
    }
    public void pendantStorage(Level level, Player player, ItemStack stack, BondData data) {
        player.openMenu(data);
    }

    public void initTP(Level level, Player player, BondData data){
        if (whom(data.playerA(), data.playerB(), player).getFirst() == data.playerA()) {
            data.setPlayerATeleportTimer(120);
        } else {
            data.setPlayerBTeleportTimer(120);
        }
    }

    public void finishTP(Level level, Player player, BondData data){
        if (whom(data.playerA(), data.playerB(), player).getFirst() == data.playerA()) {
            data.setPlayerATeleportTimer(-1);
        } else {
            data.setPlayerBTeleportTimer(-1);
        }
        this.pendantTP(level, player, data);
        data.setTeleportCooldown(1200);
    }

    public void pendantTP(Level level, Player player, BondData data) {
        UUID playerA = data.playerA();
        UUID playerB = data.playerB();
        if (level.getPlayerInAnyDimension(playerA) != null && level.getPlayerInAnyDimension(playerB) != null) {
            if (level instanceof ServerLevel && player instanceof ServerPlayer) {
                Player teleportTo = level.getPlayerInAnyDimension(whom(playerA, playerB, player).get(1));
                player.teleport(new TeleportTransition(
                        (ServerLevel) teleportTo.level(),
                        teleportTo.position(),
                        Vec3.ZERO,
                        0.0f, 0.0f,
                        Relative.union(Relative.ROTATION, Relative.DELTA), TeleportTransition.DO_NOTHING
                ));
                level.playSound(null, teleportTo.getOnPos(), SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
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
                if(data.playerATeleportTimer() == 0 || data.playerBTeleportTimer() == 0){
                    if(!data.otherPlayerTeleporting(owner.getUUID())) {
                        finishTP(level, (Player) owner, data);
                    }
                } else if((data.playerATeleportTimer() % 20 == 0 || data.playerBTeleportTimer() % 20 == 0) && data.otherPlayerTeleporting(owner.getUUID())){
                    level.playSound(null, owner.getOnPos(), Sounds.UI_CLICK_FANCY, SoundSource.PLAYERS, 0.5f, 0.9f );
                    if(owner instanceof ServerPlayer player){
                        Player other = level.getPlayerInAnyDimension(whom(playerA, playerB, player).get(1));
                        player.connection
                                .send(new ClientboundSetActionBarTextPacket(Component.translatable("item.starbond.starbond_pendant.friendIncoming", other.getName())
                                        .withColor(PendantTooltipHelper.PENDANT_TOOLTIP_COLORS.get(itemStack.get(Components.PENDANT_TEXTURE)))));
                    }
                }
                UUID other = whom(playerA, playerB, owner).get(1);
                float dist = owner.distanceTo(level.getPlayerInAnyDimension(other));
                if (dist <= 50.0f && level.getPlayerInAnyDimension(other) != null) {
                    int amp = this.getCamaraderieAmp(itemStack.get(Components.PENDANT_TICKS));
                    ((Player) owner).addEffect(new MobEffectInstance(Starbond.CAMARADERIE, MobEffectInstance.INFINITE_DURATION, amp));
                    data.setTickCamaraderie();
                    itemStack.set(Components.PENDANT_TICKS, data.camaraderieTicks());
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
        level.playSound(itemEntity, itemEntity.getOnPos(), Sounds.ITEM_PENDANT_SHATTER, SoundSource.NEUTRAL);
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
        level.playSound(null, owner.getOnPos(), Sounds.ITEM_PENDANT_SHATTER, SoundSource.NEUTRAL);
        data.activateSuicideMode();
    }

    public static List<UUID> whom(UUID playerA, UUID playerB, Entity user){
        if(user.getUUID() == playerA){
            return List.of(playerA, playerB);
        } else {
            return List.of(playerB, playerA);

        }
    }
    public int getCamaraderieAmp(int ticks){
        if(ticks <= 12000){
            return 0;
        } else if(ticks <= 36000){
            return 1;
        } else if(ticks <= 60000){
            return 2;
        } else if(ticks <= 96000){
            return 4;
        } else {
            return 0;
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
