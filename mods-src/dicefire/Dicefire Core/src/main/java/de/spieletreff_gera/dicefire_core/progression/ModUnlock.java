package de.spieletreff_gera.dicefire_core.progression;

import java.util.List;

public record ModUnlock(
        String id,
        String displayName,
        String mainMod,
        List<String> addons
) {
}