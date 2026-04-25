package io.github.bigcrazyofficial.data.entity;

import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v8.component.CardinalComponent;

public interface BondReferenceComponent extends CardinalComponent, AutoSyncedComponent {
    int getReference();
    void postReference(int reference);
}
