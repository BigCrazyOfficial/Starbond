package io.github.bigcrazyofficial.data.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.bigcrazyofficial.Starbond;
import io.github.bigcrazyofficial.menu.LocketInventoryMenu;
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

    public BondData(UUID playerA, UUID playerB, List<ItemStack> items, int camaraderieTicks) {
        this.playerA = playerA;
        this.playerB = playerB;
        this.inventory = new SimpleContainer(5);
        int i = 0;
        for(ItemStack item : items) {
            inventory.setItem(i, item);
            i++;
        }
        this.camaraderieTicks = camaraderieTicks;
    }

    public BondData(UUID playerA, UUID playerB, int camaraderieTicks){
        this.playerA = playerA;
        this.playerB = playerB;
        this.inventory = new SimpleContainer(5);
        this.camaraderieTicks = camaraderieTicks;
    }

    public UUID playerA(){ return playerA; }
    public UUID playerB(){ return playerB; }
    public SimpleContainer inventory(){ return this.inventory; }
    public NonNullList<ItemStack> items(){ return this.inventory.getItems(); }
    public int camaraderieTicks(){ return this.camaraderieTicks; }
    public static final MapCodec<BondData> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    UUIDUtil.CODEC.fieldOf("playerA").forGetter(BondData::playerA),
                    UUIDUtil.CODEC.fieldOf("playerB").forGetter(BondData::playerB),
                    ItemStack.OPTIONAL_CODEC.sizeLimitedListOf(5).fieldOf("items").forGetter(BondData::items),
                    Codec.INT.fieldOf("camaraderieTicks").forGetter(BondData::camaraderieTicks)
            ).apply(i, BondData::new)

    );

    @Override
    public Component getDisplayName() {
        return Component.empty();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new LocketInventoryMenu(containerId, inventory, this.inventory);
    }
}
