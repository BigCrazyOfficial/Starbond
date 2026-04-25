package io.github.bigcrazyofficial.data.global;

import io.github.bigcrazyofficial.data.base.BondData;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v8.component.CardinalComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface BondComponent extends CardinalComponent, AutoSyncedComponent {
    HashMap<Integer, BondData> getBondData();
    void putBondData(HashMap<Integer, BondData> bond);
    void addBondEntry(int i, UUID playerA, UUID playerB);
    void removeBondEntry(int i);
    BondData getBondEntry(int i);
}