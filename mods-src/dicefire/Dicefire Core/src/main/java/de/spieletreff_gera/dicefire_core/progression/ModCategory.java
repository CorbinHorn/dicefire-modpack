package de.spieletreff_gera.dicefire_core.progression;

import java.util.List;

public record ModCategory(
        String id,
        String displayName,
        List<ModUnlock> unlocks
) {
}