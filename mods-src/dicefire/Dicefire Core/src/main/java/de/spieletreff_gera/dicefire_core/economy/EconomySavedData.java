package de.spieletreff_gera.dicefire_core.economy;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EconomySavedData extends SavedData {

    private final Map<UUID, PlayerEconomyData> players = new HashMap<>();

    public PlayerEconomyData getPlayer(UUID uuid) {
        PlayerEconomyData data = players.get(uuid);

        if (data == null) {
            data = new PlayerEconomyData();
            players.put(uuid, data);
            setDirty();
        }

        return data;
    }

    public void markChanged() {
        setDirty();
    }

    @Override
    public CompoundTag save(
            CompoundTag tag,
            HolderLookup.Provider provider
    ) {
        ListTag playerList = new ListTag();

        for (Map.Entry<UUID, PlayerEconomyData> entry : players.entrySet()) {
            CompoundTag playerTag = new CompoundTag();

            playerTag.putUUID("uuid", entry.getKey());

            PlayerEconomyData data = entry.getValue();

            playerTag.putLong("shards", data.getShards());
            playerTag.putLong("fame", data.getFame());

            ListTag unlockList = new ListTag();

            for (String unlock : data.getUnlockedMods()) {
                CompoundTag unlockTag = new CompoundTag();
                unlockTag.putString("id", unlock);
                unlockList.add(unlockTag);
            }

            playerTag.put("unlocks", unlockList);
            playerList.add(playerTag);
        }

        tag.put("players", playerList);

        return tag;
    }

    public static EconomySavedData load(
            CompoundTag tag,
            HolderLookup.Provider provider
    ) {
        EconomySavedData savedData = new EconomySavedData();

        ListTag playerList = tag.getList(
                "players",
                Tag.TAG_COMPOUND
        );

        for (Tag baseTag : playerList) {
            CompoundTag playerTag = (CompoundTag) baseTag;

            UUID uuid = playerTag.getUUID("uuid");

            PlayerEconomyData data = new PlayerEconomyData();

            data.setShards(playerTag.getLong("shards"));
            data.setFame(playerTag.getLong("fame"));

            ListTag unlockList = playerTag.getList(
                    "unlocks",
                    Tag.TAG_COMPOUND
            );

            for (Tag unlockBaseTag : unlockList) {
                CompoundTag unlockTag =
                        (CompoundTag) unlockBaseTag;

                data.unlockMod(
                        unlockTag.getString("id")
                );
            }

            savedData.players.put(uuid, data);
        }

        return savedData;
    }

    public static EconomySavedData get(
            MinecraftServer server
    ) {
        return server
                .overworld()
                .getDataStorage()
                .computeIfAbsent(
                        new SavedData.Factory<>(
                                EconomySavedData::new,
                                EconomySavedData::load,
                                null
                        ),
                        "dicefire_economy"
                );
    }
}