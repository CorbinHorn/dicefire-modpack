package de.spieletreff_gera.dicefire_core.client;

import java.util.HashSet;
import java.util.Set;

public final class ClientProgressionData {

    private static long fame = 0;

    private static final Set<String> unlockedMods =
            new HashSet<>();

    private ClientProgressionData() {
    }

    public static long getFame() {
        return fame;
    }

    public static boolean isUnlocked(String unlockId) {
        return unlockedMods.contains(unlockId);
    }

    public static void update(
            long newFame,
            Iterable<String> unlocks
    ) {
        fame = newFame;

        unlockedMods.clear();

        for (String unlock : unlocks) {
            unlockedMods.add(unlock);
        }

        refreshScreen();
    }

    public static void refreshScreen() {

        var minecraft =
                net.minecraft.client.Minecraft.getInstance();

        if (minecraft.screen instanceof
                de.spieletreff_gera.dicefire_core.client.screen
                        .ProgressionScreen screen) {

            screen.refresh();
        }
    }
}