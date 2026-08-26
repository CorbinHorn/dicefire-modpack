package de.spieletreff_gera.dicefire_core.api;

import de.spieletreff_gera.dicefire_core.economy.EconomySavedData;
import de.spieletreff_gera.dicefire_core.economy.PlayerEconomyData;
import net.minecraft.server.MinecraftServer;

import java.util.UUID;

public final class DicefireApi {

    private DicefireApi() {
    }

    private static PlayerEconomyData getPlayer(
            MinecraftServer server,
            UUID uuid
    ) {
        return EconomySavedData
                .get(server)
                .getPlayer(uuid);
    }

    // =========================
    // Splitter
    // =========================

    public static long getShards(
            MinecraftServer server,
            UUID uuid
    ) {
        return getPlayer(server, uuid).getShards();
    }

    public static void setShards(
            MinecraftServer server,
            UUID uuid,
            long amount
    ) {
        getPlayer(server, uuid).setShards(amount);

        EconomySavedData
                .get(server)
                .markChanged();
    }

    public static void addShards(
            MinecraftServer server,
            UUID uuid,
            long amount
    ) {
        getPlayer(server, uuid).addShards(amount);

        EconomySavedData
                .get(server)
                .markChanged();
    }

    public static boolean withdrawShards(
            MinecraftServer server,
            UUID uuid,
            long amount
    ) {
        boolean success =
                getPlayer(server, uuid)
                        .removeShards(amount);

        if (success) {
            EconomySavedData
                    .get(server)
                    .markChanged();
        }

        return success;
    }

    // =========================
    // Ruhm
    // =========================

    public static long getFame(
            MinecraftServer server,
            UUID uuid
    ) {
        return getPlayer(server, uuid).getFame();
    }

    public static void setFame(
            MinecraftServer server,
            UUID uuid,
            long amount
    ) {
        getPlayer(server, uuid).setFame(amount);

        EconomySavedData
                .get(server)
                .markChanged();
    }

    public static void addFame(
            MinecraftServer server,
            UUID uuid,
            long amount
    ) {
        getPlayer(server, uuid).addFame(amount);

        EconomySavedData
                .get(server)
                .markChanged();
    }

    public static boolean withdrawFame(
            MinecraftServer server,
            UUID uuid,
            long amount
    ) {
        boolean success =
                getPlayer(server, uuid)
                        .removeFame(amount);

        if (success) {
            EconomySavedData
                    .get(server)
                    .markChanged();
        }

        return success;
    }

    // =========================
    // Mod-Unlocks
    // =========================

    public static boolean hasUnlockedMod(
            MinecraftServer server,
            UUID uuid,
            String unlockId
    ) {
        return getPlayer(server, uuid)
                .hasUnlockedMod(unlockId);
    }

    public static boolean unlockMod(
            MinecraftServer server,
            UUID uuid,
            String unlockId
    ) {
        boolean changed =
                getPlayer(server, uuid)
                        .unlockMod(unlockId);

        if (changed) {
            EconomySavedData
                    .get(server)
                    .markChanged();
        }

        return changed;
    }

    public static void lockMod(
            MinecraftServer server,
            UUID uuid,
            String unlockId
    ) {
        getPlayer(server, uuid).lockMod(unlockId);

        EconomySavedData
                .get(server)
                .markChanged();
    }
}