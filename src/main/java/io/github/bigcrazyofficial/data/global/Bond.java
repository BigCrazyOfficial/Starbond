package io.github.bigcrazyofficial.data.global;

import com.mojang.serialization.Codec;
import io.github.bigcrazyofficial.Starbond;
import io.github.bigcrazyofficial.data.base.BondData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class Bond implements BondComponent{
    private HashMap<Integer, BondData> bondData = new HashMap<>();
    private final Scoreboard provider;

    public Bond(Scoreboard provider, @Nullable MinecraftServer server) {
        this.provider = provider;
    }

    @Override
    public HashMap<Integer, BondData> getBondData() {
        return this.bondData;
    }

    @Override
    public void putBondData(HashMap<Integer, BondData> bond) {
        this.bondData = bond;
    }

    @Override
    public void addBondEntry(int i, UUID playerA, UUID playerB){
        if(!bondData.containsKey(i)){
            bondData.put(i, new BondData(playerA, playerB, 0, false, false, false, false));
        } else {
            Starbond.LOGGER.info("[Starbond] Bond exists with this ID already!");
        }
    }

    @Override
    public void removeBondEntry(int i){
        if(bondData.containsKey(i)){
            bondData.remove(i);
        } else {
            Starbond.LOGGER.info("[Starbond] No such bond exists with that ID!");
        }
    }

    @Override
    public BondData getBondEntry(int i){
        if(bondData.containsKey(i)){ return bondData.get(i); } else {
            Starbond.LOGGER.info("[Starbond] No such bond exists with that ID!");
            return null;
        }
    }

    @Override
    public void readData(ValueInput readView) {
        HashMap<Integer, BondData> result =
                new HashMap<>((readView.read("bondData",
                        Codec.unboundedMap(Codec.STRING, BondData.MAP_CODEC.codec()))
                .orElseGet(HashMap::new))
                        .entrySet().stream().collect(Collectors.toMap(
                                entry -> Integer.parseInt(entry.getKey()),
                                Map.Entry::getValue
                        )));
        this.putBondData(result);
    }

    @Override
    public void writeData(ValueOutput writeView) {
        //If it's stupid, but it works...
        if(this.bondData.isEmpty())
            return;
        HashMap<String, BondData> toWrite =
                new HashMap<>(bondData.entrySet().stream().collect(Collectors.toMap(
            entry -> String.valueOf(entry.getKey()),
                Map.Entry::getValue
        )));
        writeView.store("bondData",
                Codec.unboundedMap(Codec.STRING, BondData.MAP_CODEC.codec()),
                toWrite
        );
    }

    public static int getRandomIdentifier(HashMap<Integer, BondData> data){
        Random random = new Random(data.hashCode());
        int i;
        while(true) {
            i = random.nextInt(0, 1000000000);
            if(!data.containsKey(i)){
                return i;
            }
        }
    }

    @Override
    public void tick() {
        for(BondData data : bondData.values()){
            data.tick();
        }
    }
}
