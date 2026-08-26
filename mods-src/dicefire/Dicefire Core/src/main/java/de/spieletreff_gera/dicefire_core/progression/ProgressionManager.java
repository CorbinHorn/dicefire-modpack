package de.spieletreff_gera.dicefire_core.progression;

import de.spieletreff_gera.dicefire_core.api.DicefireApi;
import de.spieletreff_gera.dicefire_core.config.ProgressionConfig;
import net.minecraft.server.MinecraftServer;

import java.util.Optional;
import java.util.UUID;

public final class ProgressionManager {

    private ProgressionManager() {
    }

    public static Optional<ModUnlock> findUnlock(
            String unlockId
    ) {

        return ProgressionConfig.getCategories()
                .stream()
                .flatMap(category ->
                        category.unlocks().stream()
                )
                .filter(unlock ->
                        unlock.id().equalsIgnoreCase(unlockId)
                )
                .findFirst();
    }

    public static Optional<ModCategory> findCategory(
            String unlockId
    ) {

        return ProgressionConfig.getCategories()
                .stream()
                .filter(category ->
                        category.unlocks()
                                .stream()
                                .anyMatch(unlock ->
                                        unlock.id()
                                                .equalsIgnoreCase(unlockId)
                                )
                )
                .findFirst();
    }

    public static int getUnlockedCount(
            MinecraftServer server,
            UUID uuid,
            ModCategory category
    ) {

        int count = 0;

        for (ModUnlock unlock : category.unlocks()) {

            if (DicefireApi.hasUnlockedMod(
                    server,
                    uuid,
                    unlock.id()
            )) {
                count++;
            }
        }

        return count;
    }

    public static long getNextUnlockCost(
            MinecraftServer server,
            UUID uuid,
            ModCategory category
    ) {

        int unlocked =
                getUnlockedCount(
                        server,
                        uuid,
                        category
                );

        return 1L + (unlocked * 2L);
    }

    public static UnlockResult purchaseUnlock(
            MinecraftServer server,
            UUID uuid,
            String unlockId
    ) {

        Optional<ModUnlock> unlockOptional =
                findUnlock(unlockId);

        if (unlockOptional.isEmpty()) {
            return UnlockResult.UNKNOWN_MOD;
        }

        ModUnlock unlock =
                unlockOptional.get();

        if (DicefireApi.hasUnlockedMod(
                server,
                uuid,
                unlock.id()
        )) {
            return UnlockResult.ALREADY_UNLOCKED;
        }

        Optional<ModCategory> categoryOptional =
                findCategory(unlock.id());

        if (categoryOptional.isEmpty()) {
            return UnlockResult.UNKNOWN_MOD;
        }

        ModCategory category =
                categoryOptional.get();

        long cost =
                getNextUnlockCost(
                        server,
                        uuid,
                        category
                );

        boolean paid =
                DicefireApi.withdrawFame(
                        server,
                        uuid,
                        cost
                );

        if (!paid) {
            return UnlockResult.NOT_ENOUGH_FAME;
        }

        DicefireApi.unlockMod(
                server,
                uuid,
                unlock.id()
        );

        return UnlockResult.SUCCESS;
    }

    public static Optional<ModUnlock> findUnlockByModId(String modId) {

        for (ModCategory category : ProgressionConfig.getCategories()) {

            for (ModUnlock unlock : category.unlocks()) {

                if (unlock.mainMod().equalsIgnoreCase(modId)) {
                    return Optional.of(unlock);
                }

                for (String addon : unlock.addons()) {
                    if (addon.equalsIgnoreCase(modId)) {
                        return Optional.of(unlock);
                    }
                }
            }
        }

        return Optional.empty();
    }

    public static boolean canUseMod(
            MinecraftServer server,
            UUID uuid,
            String modId
    ) {

        Optional<ModUnlock> unlockOptional =
                findUnlockByModId(modId);

        // Nicht in Config = frei
        if (unlockOptional.isEmpty()) {
            return true;
        }

        ModUnlock unlock = unlockOptional.get();

        return DicefireApi.hasUnlockedMod(
                server,
                uuid,
                unlock.id()
        );
    }
}