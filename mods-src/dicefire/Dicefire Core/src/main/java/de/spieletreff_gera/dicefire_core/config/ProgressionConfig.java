package de.spieletreff_gera.dicefire_core.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import de.spieletreff_gera.dicefire_core.Dicefire_core;
import de.spieletreff_gera.dicefire_core.progression.ModCategory;
import de.spieletreff_gera.dicefire_core.progression.ModUnlock;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ProgressionConfig {

    private static final List<ModCategory> CATEGORIES = new ArrayList<>();

    private ProgressionConfig() {
    }

    public static void load() {

        Path path = FMLPaths.CONFIGDIR.get()
                .resolve("dicefire-progression.toml");

        if (!Files.exists(path)) {
            createDefaultConfig(path);
        }

        try (CommentedFileConfig config =
                     CommentedFileConfig.builder(path)
                             .sync()
                             .build()) {

            config.load();

            CATEGORIES.clear();

            List<?> categories = config.get("categories");

            if (categories == null) {
                Dicefire_core.LOGGER.warn(
                        "Keine Progression-Kategorien konfiguriert."
                );
                return;
            }

            for (Object categoryObject : categories) {

                if (!(categoryObject instanceof UnmodifiableConfig categoryConfig)) {
                    Dicefire_core.LOGGER.warn(
                            "Ungültiger Kategorie-Eintrag in dicefire-progression.toml"
                    );
                    continue;
                }

                String id = categoryConfig.get("id");
                String displayName = categoryConfig.get("display_name");

                List<ModUnlock> unlocks = new ArrayList<>();

                List<?> unlockList = categoryConfig.get("unlocks");

                if (unlockList != null) {

                    for (Object unlockObject : unlockList) {

                        if (!(unlockObject instanceof UnmodifiableConfig unlockConfig)) {
                            continue;
                        }

                        String unlockId =
                                unlockConfig.get("id");

                        String unlockDisplayName =
                                unlockConfig.get("display_name");

                        String mainMod =
                                unlockConfig.get("main_mod");

                        List<String> addons = new ArrayList<>();

                        List<?> addonList =
                                unlockConfig.get("addons");

                        if (addonList != null) {
                            for (Object addon : addonList) {
                                addons.add(String.valueOf(addon));
                            }
                        }

                        unlocks.add(
                                new ModUnlock(
                                        unlockId,
                                        unlockDisplayName,
                                        mainMod,
                                        List.copyOf(addons)
                                )
                        );
                    }
                }

                CATEGORIES.add(
                        new ModCategory(
                                id,
                                displayName,
                                List.copyOf(unlocks)
                        )
                );
            }
        }

        Dicefire_core.LOGGER.info(
                "{} Dicefire-Progression-Kategorien geladen.",
                CATEGORIES.size()
        );
    }

    public static List<ModCategory> getCategories() {
        return List.copyOf(CATEGORIES);
    }

    private static void createDefaultConfig(Path path) {

        String defaultConfig = """
                # ==========================================================
                # Dicefire Core - Mod Progression
                # ==========================================================
                #
                # Nur Hauptmods, die hier eingetragen sind, werden gesperrt.
                # Nicht aufgeführte Mods sind für alle Spieler frei verfügbar.
                #
                # Addons kosten keinen zusätzlichen Ruhm.
                # Sie werden zusammen mit ihrer Hauptmod freigeschaltet.
                #
                # Kosten innerhalb einer Kategorie:
                # 1. Mod = 1 Ruhm
                # 2. Mod = 3 Ruhm
                # 3. Mod = 5 Ruhm
                # 4. Mod = 7 Ruhm
                # usw.
                
                
                [[categories]]
                id = "technology"
                display_name = "Technik"
                
                [[categories.unlocks]]
                id = "modern_industrialization"
                display_name = "Modern Industrialization"
                main_mod = "modern_industrialization"
                addons = [
                    "modern_industrialization_sound_addon",
                    "modern_industrial_routers"
                ]
                
                [[categories.unlocks]]
                id = "mekanism"
                display_name = "Mekanism"
                main_mod = "mekanism"
                addons = [
                    "mekanismgenerators",
                    "mekanismtools",
                    "mekanismadditions"
                ]
                
                [[categories.unlocks]]
                id = "create"
                display_name = "Create"
                main_mod = "create"
                addons = []
                """;

        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, defaultConfig);

            Dicefire_core.LOGGER.info(
                    "Standard-Progressionsconfig erstellt: {}",
                    path
            );

        } catch (IOException e) {
            throw new RuntimeException(
                    "dicefire-progression.toml konnte nicht erstellt werden.",
                    e
            );
        }
    }
}