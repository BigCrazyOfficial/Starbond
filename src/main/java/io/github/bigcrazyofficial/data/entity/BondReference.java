package io.github.bigcrazyofficial.data.entity;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class BondReference implements BondReferenceComponent{
    private int bondReference;

    public BondReference(){
        this(0);
    }

    public BondReference(int i){
        this.bondReference = i;
    }
    @Override
    public int getReference() { return bondReference; }

    @Override
    public void postReference(int reference) {
        this.bondReference = reference;
    }

    @Override
    public void readData(ValueInput readView) {
        readView.getInt("bond_id").ifPresentOrElse(
                this::postReference,
                () -> postReference(0)
        );
    }

    @Override
    public void writeData(ValueOutput writeView) {
        writeView.putInt("bond_id", this.bondReference);
    }
}
