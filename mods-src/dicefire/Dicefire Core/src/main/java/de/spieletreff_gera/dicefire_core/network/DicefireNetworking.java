package de.spieletreff_gera.dicefire_core.network;

import de.spieletreff_gera.dicefire_core.api.DicefireApi;
import de.spieletreff_gera.dicefire_core.client.ClientProgressionData;
import de.spieletreff_gera.dicefire_core.economy.EconomySavedData;
import de.spieletreff_gera.dicefire_core.progression.ProgressionManager;
import de.spieletreff_gera.dicefire_core.progression.UnlockResult;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.List;

public final class DicefireNetworking {

    private DicefireNetworking() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {

        PayloadRegistrar registrar = event.registrar("1");


        // =========================================================
        // CLIENT -> SERVER
        // Spieler möchte eine Mod freischalten
        // =========================================================

        registrar.playToServer(
                UnlockModPayload.TYPE,
                UnlockModPayload.STREAM_CODEC,
                (payload, context) -> {

                    context.enqueueWork(() -> {

                        if (!(context.player() instanceof ServerPlayer player)) {
                            return;
                        }

                        MinecraftServer server = player.getServer();

                        if (server == null) {
                            return;
                        }

                        UnlockResult result =
                                ProgressionManager.purchaseUnlock(
                                        server,
                                        player.getUUID(),
                                        payload.unlockId()
                                );

                        switch (result) {

                            case SUCCESS ->
                                    player.sendSystemMessage(
                                            Component.literal(
                                                    "Mod erfolgreich freigeschaltet!"
                                            )
                                    );

                            case ALREADY_UNLOCKED ->
                                    player.sendSystemMessage(
                                            Component.literal(
                                                    "Diese Mod hast du bereits freigeschaltet."
                                            )
                                    );

                            case NOT_ENOUGH_FAME ->
                                    player.sendSystemMessage(
                                            Component.literal(
                                                    "Du hast nicht genug Ruhm."
                                            )
                                    );

                            case UNKNOWN_MOD ->
                                    player.sendSystemMessage(
                                            Component.literal(
                                                    "Unbekannte Mod."
                                            )
                                    );
                        }


                        // Nach dem Kauf schicken wir dem Client
                        // sofort seinen neuen Stand.
                        sendProgressionData(player);
                    });
                }
        );


        // =========================================================
        // CLIENT -> SERVER
        // Client fragt seinen aktuellen Progressionsstand an
        // =========================================================

        registrar.playToServer(
                RequestProgressionDataPayload.TYPE,
                RequestProgressionDataPayload.STREAM_CODEC,
                (payload, context) -> {

                    context.enqueueWork(() -> {

                        if (!(context.player() instanceof ServerPlayer player)) {
                            return;
                        }

                        sendProgressionData(player);
                    });
                }
        );


        // =========================================================
        // SERVER -> CLIENT
        // Server schickt Ruhm + freigeschaltete Mods
        // =========================================================

        registrar.playToClient(
                ProgressionDataPayload.TYPE,
                ProgressionDataPayload.STREAM_CODEC,
                (payload, context) -> {

                    context.enqueueWork(() -> {

                        ClientProgressionData.update(
                                payload.fame(),
                                payload.unlockedMods()
                        );
                    });
                }
        );
    }


    // =============================================================
    // Hilfsmethode:
    // aktuellen Spielerstand an den Client schicken
    // =============================================================

    private static void sendProgressionData(ServerPlayer player) {

        MinecraftServer server = player.getServer();

        if (server == null) {
            return;
        }


        // Ruhm aus unserer API holen
        long fame =
                DicefireApi.getFame(
                        server,
                        player.getUUID()
                );


        // Freigeschaltete Mods des Spielers holen
        var unlockedMods =
                EconomySavedData
                        .get(server)
                        .getPlayer(player.getUUID())
                        .getUnlockedMods();


        // Daten an genau diesen Spieler schicken
        PacketDistributor.sendToPlayer(
                player,
                new ProgressionDataPayload(
                        fame,
                        List.copyOf(unlockedMods)
                )
        );
    }
}