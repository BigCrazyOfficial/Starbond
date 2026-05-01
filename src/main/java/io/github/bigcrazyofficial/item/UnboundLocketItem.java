package io.github.bigcrazyofficial.item;

import io.github.bigcrazyofficial.Starbond;
import io.github.bigcrazyofficial.data.CardinalComponents;
import io.github.bigcrazyofficial.data.global.Bond;
import io.github.bigcrazyofficial.item.data.Components;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.time.Instant;
import java.util.Date;

public class UnboundLocketItem extends Item {
    public UnboundLocketItem(Properties properties) {
        super(properties);
    }
    @Override
    public InteractionResult interactLivingEntity(final ItemStack itemStack, final Player player, final LivingEntity target, final InteractionHand type) {
        Level level = player.level();
        if (target instanceof Player && !level.isClientSide()) {
            if (player.getComponent(CardinalComponents.BOND_REFERENCE).getReference() == 0 &&
                    target.getComponent(CardinalComponents.BOND_REFERENCE).getReference() == 0) {
                if (player.getInventory().getFreeSlot() != -1 &&
                        ((Player) target).getInventory().getFreeSlot() != -1) {
                    int i = Bond.getRandomIdentifier(level.getScoreboard().getComponent(CardinalComponents.BOND).getBondData());
                    level.getScoreboard().getComponent(CardinalComponents.BOND).addBondEntry(i, player.getUUID(), target.getUUID());
                    player.getComponent(CardinalComponents.BOND_REFERENCE).postReference(i);
                    target.getComponent(CardinalComponents.BOND_REFERENCE).postReference(i);
                    player.addItem(new ItemStack(Items.STARBOND_LOCKET.builtInRegistryHolder(), 1,
                            DataComponentPatch.builder().set(Components.LOCKET_TEXTURE, "red").build()));
                    ((Player) target).addItem(new ItemStack(Items.STARBOND_LOCKET.builtInRegistryHolder(), 1,
                            DataComponentPatch.builder().set(Components.LOCKET_TEXTURE, "blue").build()));
                    itemStack.consume(1, player);
                    return InteractionResult.CONSUME;
                }
            }
        }
        return InteractionResult.PASS;
    }
}
