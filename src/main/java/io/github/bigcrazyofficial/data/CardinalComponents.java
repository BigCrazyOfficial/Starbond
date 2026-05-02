package io.github.bigcrazyofficial.data;

import io.github.bigcrazyofficial.data.entity.BondReference;
import io.github.bigcrazyofficial.data.entity.BondReferenceComponent;
import io.github.bigcrazyofficial.data.global.Bond;
import io.github.bigcrazyofficial.data.global.BondComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Scoreboard;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import org.ladysnake.cca.api.v8.level.LevelComponentFactoryRegistry;
import org.ladysnake.cca.api.v8.level.LevelComponentInitializer;

public class CardinalComponents implements EntityComponentInitializer, ScoreboardComponentInitializer {
    //not to be confused with Components
    public static final ComponentKey<BondReferenceComponent> BOND_REFERENCE =
            ComponentRegistry.getOrCreate(
                    Identifier.fromNamespaceAndPath("starbond", "bond_id"),
                    BondReferenceComponent.class);

    public static final ComponentKey<BondComponent> BOND =
            ComponentRegistry.getOrCreate(
                    Identifier.fromNamespaceAndPath("starbond", "bond"),
                    BondComponent.class);
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.beginRegistration(Player.class, BOND_REFERENCE)
                .respawnStrategy(RespawnCopyStrategy.CHARACTER)
                .end(e -> new BondReference());
    }

    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
        registry.registerScoreboardComponent(BOND, Bond::new);
    }

}

