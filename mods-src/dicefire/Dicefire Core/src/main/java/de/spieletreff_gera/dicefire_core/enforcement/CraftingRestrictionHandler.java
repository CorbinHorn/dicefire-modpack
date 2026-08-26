package de.spieletreff_gera.dicefire_core.enforcement;

import de.spieletreff_gera.dicefire_core.progression.ProgressionManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class CraftingRestrictionHandler {

    private CraftingRestrictionHandler() {
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ItemStack crafted = event.getCrafting();

        if (crafted.isEmpty()) {
            return;
        }

        ResourceLocation itemId =
                BuiltInRegistries.ITEM.getKey(crafted.getItem());

        String modId = itemId.getNamespace();

        boolean allowed =
                ProgressionManager.canUseMod(
                        player.getServer(),
                        player.getUUID(),
                        modId
                );

        if (allowed) {
            return;
        }

        crafted.setCount(0);

        player.displayClientMessage(
                net.minecraft.network.chat.Component.literal(
                        "Du hast diese Mod noch nicht freigeschaltet."
                ),
                false
        );
    }
}