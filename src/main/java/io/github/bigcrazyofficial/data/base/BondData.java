package io.github.bigcrazyofficial.data.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.bigcrazyofficial.Starbond;
import io.github.bigcrazyofficial.menu.PendantInventoryMenu;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class BondData implements MenuProvider {
    private static final int MAX_EFFECT_BUILDUP_DISTANCE = 50;
    UUID playerA;
    UUID playerB;
    SimpleContainer inventory;
    int camaraderieTicks;
    boolean tickCamaraderie;
    int playerATeleportTimer;
    int playerBTeleportTimer;
    int teleportCooldown;
    boolean suicideMode;
    boolean playerAChanneling;
    boolean playerBChanneling;

    public BondData(UUID playerA, UUID playerB, List<ItemStack> items, int camaraderieTicks, boolean tickCamaraderie, int playerATeleportTimer, int playerBTeleportTimer, int teleportCooldown, boolean suicideMode, boolean playerAChanneling, boolean playerBChanneling) {
        this.playerA = playerA;
        this.playerB = playerB;
        this.inventory = new SimpleContainer(5);
        int i = 0;
        for(ItemStack item : items) {
            inventory.setItem(i, item);
            i++;
        }
        this.camaraderieTicks = camaraderieTicks;
        this.tickCamaraderie = tickCamaraderie;
        this.playerATeleportTimer = playerATeleportTimer;
        this.playerBTeleportTimer = playerBTeleportTimer;
        this.teleportCooldown = teleportCooldown;
        this.suicideMode = suicideMode;
        this.playerAChanneling = playerAChanneling;
        this.playerBChanneling = playerBChanneling;
    }

    public BondData(UUID playerA, UUID playerB, int camaraderieTicks, boolean tickCamaraderie,  int playerATeleportTimer, int playerBTeleportTimer, int teleportCooldown, boolean suicideMode, boolean playerAChanneling, boolean playerBChanneling){
        this.playerA = playerA;
        this.playerB = playerB;
        this.inventory = new SimpleContainer(5);
        this.camaraderieTicks = camaraderieTicks;
        this.tickCamaraderie = tickCamaraderie;
        this.playerATeleportTimer = playerATeleportTimer;
        this.playerBTeleportTimer = playerBTeleportTimer;
        this.teleportCooldown = teleportCooldown;
        this.suicideMode = suicideMode;
        this.playerAChanneling = playerAChanneling;
        this.playerBChanneling = playerBChanneling;
    }

    public UUID playerA(){ return playerA; }
    public UUID playerB(){ return playerB; }
    public SimpleContainer inventory(){ return this.inventory; }
    public NonNullList<ItemStack> items(){ return this.inventory.getItems(); }
    public int camaraderieTicks(){ return this.camaraderieTicks; }
    public int playerATeleportTimer(){ return this.playerATeleportTimer; }
    public int playerBTeleportTimer(){ return this.playerBTeleportTimer; }
    public int teleportCooldown(){ return this.teleportCooldown; }
    public boolean shouldTickCamaraderie(){ return this.tickCamaraderie; }
    public boolean playerAChanneling(){ return this.playerAChanneling; }
    public boolean playerBChanneling(){ return this.playerBChanneling; }
    public boolean suicideMode(){ return this.suicideMode; }

    public static final MapCodec<BondData> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    UUIDUtil.CODEC.fieldOf("playerA").forGetter(BondData::playerA),
                    UUIDUtil.CODEC.fieldOf("playerB").forGetter(BondData::playerB),
                    ItemStack.OPTIONAL_CODEC.sizeLimitedListOf(5).fieldOf("items").forGetter(BondData::items),
                    Codec.INT.fieldOf("camaraderieTicks").forGetter(BondData::camaraderieTicks),
                    Codec.BOOL.fieldOf("shouldTickCamaraderie").forGetter(BondData::shouldTickCamaraderie),
                    Codec.INT.fieldOf("playerATeleportTimer").forGetter(BondData::playerATeleportTimer),
                    Codec.INT.fieldOf("playerBTeleportTimer").forGetter(BondData::playerBTeleportTimer),
                    Codec.INT.fieldOf("teleportCooldown").forGetter(BondData::teleportCooldown),
                    Codec.BOOL.fieldOf("suicideMode").forGetter(BondData::suicideMode),
                    Codec.BOOL.fieldOf("playerAChanneling").forGetter(BondData::playerAChanneling),
                    Codec.BOOL.fieldOf("playerBChanneling").forGetter(BondData::playerBChanneling)
            ).apply(i, BondData::new)

    );

    public void tick(){
        if(shouldTickCamaraderie()){
            this.camaraderieTicks += 1;
            this.tickCamaraderie = false;
        } else {
            this.camaraderieTicks--;
        }

        if(playerATeleportTimer > -1  || playerBTeleportTimer > -1){
            this.tickTeleportTimer();
        }

        if(teleportCooldown > 0){
            this.teleportCooldown--;
        }
        this.playerAChanneling = false;
        this.playerBChanneling = false;
    }

    public void tickTeleportTimer(){
        if(playerATeleportTimer > -1){
            playerATeleportTimer--;
        } else if(playerBTeleportTimer > -1){
            playerBTeleportTimer--;
        }
    }

    public void activateSuicideMode(){
        suicideMode = true;
    }
    public void setTickCamaraderie(){
        tickCamaraderie = true;
    }

    public void setPlayerAChanneling(boolean bool){
        playerAChanneling = bool;
    }
    public void setPlayerBChanneling(boolean bool){
        playerBChanneling = bool;
    }

    public void setPlayerATeleportTimer(int i){
        playerATeleportTimer = i;
    }
    public void setPlayerBTeleportTimer(int i){
        playerBTeleportTimer = i;
    }
    public void setTeleportCooldown(int i){
        teleportCooldown = i;
    }
    public boolean otherPlayerChanneling(UUID uuid){
        return(uuid == playerA && playerBChanneling()) || (uuid == playerB && playerAChanneling());
    }

    public boolean otherPlayerTeleporting(UUID uuid){
        return(uuid == playerA && playerBTeleportTimer() > -1) || (uuid == playerB && playerATeleportTimer() > -1);
    }
    @Override
    public Component getDisplayName() {
        return Component.empty();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new PendantInventoryMenu(containerId, inventory, this.inventory);
    }
}
