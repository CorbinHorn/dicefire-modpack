package de.spieletreff_gera.dicefire_core.client;

import de.spieletreff_gera.dicefire_core.Dicefire_core;
import de.spieletreff_gera.dicefire_core.client.screen.ProgressionScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

@EventBusSubscriber(
        modid = Dicefire_core.MODID,
        value = Dist.CLIENT
)
public final class DicefireClient {

    private DicefireClient() {
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {

        if (event.getKey() == 80 && event.getAction() == 1) {
            Minecraft.getInstance()
                    .setScreen(new ProgressionScreen());
        }
    }
}