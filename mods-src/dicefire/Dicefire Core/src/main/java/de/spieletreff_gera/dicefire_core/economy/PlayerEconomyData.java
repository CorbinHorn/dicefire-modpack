package de.spieletreff_gera.dicefire_core.economy;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PlayerEconomyData {

    private long shards;
    private long fame;

    private final Set<String> unlockedMods = new HashSet<>();

    public PlayerEconomyData() {
        this.shards = 0;
        this.fame = 0;
    }

    // --------------------
    // Splitter
    // --------------------

    public long getShards() {
        return shards;
    }

    public void setShards(long amount) {
        this.shards = Math.max(0, amount);
    }

    public void addShards(long amount) {
        if (amount <= 0) {
            return;
        }

        this.shards += amount;
    }

    public boolean removeShards(long amount) {
        if (amount <= 0 || this.shards < amount) {
            return false;
        }

        this.shards -= amount;
        return true;
    }

    // --------------------
    // Ruhm
    // --------------------

    public long getFame() {
        return fame;
    }

    public void setFame(long amount) {
        this.fame = Math.max(0, amount);
    }

    public void addFame(long amount) {
        if (amount <= 0) {
            return;
        }

        this.fame += amount;
    }

    public boolean removeFame(long amount) {
        if (amount <= 0 || this.fame < amount) {
            return false;
        }

        this.fame -= amount;
        return true;
    }

    // --------------------
    // Mod-Unlocks
    // --------------------

    public boolean hasUnlockedMod(String unlockId) {
        return unlockedMods.contains(unlockId);
    }

    public boolean unlockMod(String unlockId) {
        if (unlockId == null || unlockId.isBlank()) {
            return false;
        }

        return unlockedMods.add(unlockId);
    }

    public void lockMod(String unlockId) {
        unlockedMods.remove(unlockId);
    }

    public Set<String> getUnlockedMods() {
        return Collections.unmodifiableSet(unlockedMods);
    }
}