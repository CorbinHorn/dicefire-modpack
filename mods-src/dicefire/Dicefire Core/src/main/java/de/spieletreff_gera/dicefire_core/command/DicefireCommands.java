package de.spieletreff_gera.dicefire_core.command;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import de.spieletreff_gera.dicefire_core.api.DicefireApi;
import de.spieletreff_gera.dicefire_core.config.ProgressionConfig;
import de.spieletreff_gera.dicefire_core.progression.ModCategory;
import de.spieletreff_gera.dicefire_core.progression.ModUnlock;
import de.spieletreff_gera.dicefire_core.progression.ProgressionManager;
import de.spieletreff_gera.dicefire_core.progression.UnlockResult;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class DicefireCommands {

    public static void register(RegisterCommandsEvent event) {

        var dispatcher = event.getDispatcher();

        // /balance
        dispatcher.register(
                Commands.literal("balance")
                        .executes(context -> {
                            ServerPlayer player =
                                    context.getSource().getPlayerOrException();

                            long shards = DicefireApi.getShards(
                                    context.getSource().getServer(),
                                    player.getUUID()
                            );

                            long fame = DicefireApi.getFame(
                                    context.getSource().getServer(),
                                    player.getUUID()
                            );

                            player.sendSystemMessage(
                                    Component.literal(
                                            "Splitter: " + shards
                                                    + " | Ruhm: " + fame
                                    )
                            );

                            return 1;
                        })
        );

        // /bal
        dispatcher.register(
                Commands.literal("bal")
                        .redirect(
                                dispatcher.getRoot()
                                        .getChild("balance")
                        )
        );

        // /economy ...
        dispatcher.register(
                Commands.literal("economy")

                        .requires(source ->
                                source.hasPermission(2)
                        )

                        // -----------------
                        // SHARDS
                        // -----------------

                        .then(
                                Commands.literal("shards")

                                        .then(
                                                Commands.literal("add")
                                                        .then(
                                                                Commands.argument(
                                                                                "player",
                                                                                EntityArgument.player()
                                                                        )
                                                                        .then(
                                                                                Commands.argument(
                                                                                                "amount",
                                                                                                LongArgumentType.longArg(1)
                                                                                        )
                                                                                        .executes(context -> {
                                                                                            ServerPlayer target =
                                                                                                    EntityArgument.getPlayer(
                                                                                                            context,
                                                                                                            "player"
                                                                                                    );

                                                                                            long amount =
                                                                                                    LongArgumentType.getLong(
                                                                                                            context,
                                                                                                            "amount"
                                                                                                    );

                                                                                            DicefireApi.addShards(
                                                                                                    context.getSource().getServer(),
                                                                                                    target.getUUID(),
                                                                                                    amount
                                                                                            );

                                                                                            context.getSource()
                                                                                                    .sendSuccess(
                                                                                                            () -> Component.literal(
                                                                                                                    target.getName().getString()
                                                                                                                            + " erhält "
                                                                                                                            + amount
                                                                                                                            + " Splitter."
                                                                                                            ),
                                                                                                            true
                                                                                                    );

                                                                                            return 1;
                                                                                        })
                                                                        )
                                                        )
                                        )

                                        .then(
                                                Commands.literal("set")
                                                        .then(
                                                                Commands.argument(
                                                                                "player",
                                                                                EntityArgument.player()
                                                                        )
                                                                        .then(
                                                                                Commands.argument(
                                                                                                "amount",
                                                                                                LongArgumentType.longArg(0)
                                                                                        )
                                                                                        .executes(context -> {
                                                                                            ServerPlayer target =
                                                                                                    EntityArgument.getPlayer(
                                                                                                            context,
                                                                                                            "player"
                                                                                                    );

                                                                                            long amount =
                                                                                                    LongArgumentType.getLong(
                                                                                                            context,
                                                                                                            "amount"
                                                                                                    );

                                                                                            DicefireApi.setShards(
                                                                                                    context.getSource().getServer(),
                                                                                                    target.getUUID(),
                                                                                                    amount
                                                                                            );

                                                                                            context.getSource()
                                                                                                    .sendSuccess(
                                                                                                            () -> Component.literal(
                                                                                                                    "Splitter von "
                                                                                                                            + target.getName().getString()
                                                                                                                            + " auf "
                                                                                                                            + amount
                                                                                                                            + " gesetzt."
                                                                                                            ),
                                                                                                            true
                                                                                                    );

                                                                                            return 1;
                                                                                        })
                                                                        )
                                                        )
                                        )

                                        .then(
                                                Commands.literal("remove")
                                                        .then(
                                                                Commands.argument(
                                                                                "player",
                                                                                EntityArgument.player()
                                                                        )
                                                                        .then(
                                                                                Commands.argument(
                                                                                                "amount",
                                                                                                LongArgumentType.longArg(1)
                                                                                        )
                                                                                        .executes(context -> {
                                                                                            ServerPlayer target =
                                                                                                    EntityArgument.getPlayer(
                                                                                                            context,
                                                                                                            "player"
                                                                                                    );

                                                                                            long amount =
                                                                                                    LongArgumentType.getLong(
                                                                                                            context,
                                                                                                            "amount"
                                                                                                    );

                                                                                            boolean success =
                                                                                                    DicefireApi.withdrawShards(
                                                                                                            context.getSource().getServer(),
                                                                                                            target.getUUID(),
                                                                                                            amount
                                                                                                    );

                                                                                            if (!success) {
                                                                                                context.getSource()
                                                                                                        .sendFailure(
                                                                                                                Component.literal(
                                                                                                                        "Spieler hat nicht genug Splitter."
                                                                                                                )
                                                                                                        );

                                                                                                return 0;
                                                                                            }

                                                                                            context.getSource()
                                                                                                    .sendSuccess(
                                                                                                            () -> Component.literal(
                                                                                                                    amount
                                                                                                                            + " Splitter von "
                                                                                                                            + target.getName().getString()
                                                                                                                            + " entfernt."
                                                                                                            ),
                                                                                                            true
                                                                                                    );

                                                                                            return 1;
                                                                                        })
                                                                        )
                                                        )
                                        )
                        )

                        // -----------------
                        // FAME
                        // -----------------

                        .then(
                                Commands.literal("fame")

                                        .then(
                                                Commands.literal("add")
                                                        .then(
                                                                Commands.argument(
                                                                                "player",
                                                                                EntityArgument.player()
                                                                        )
                                                                        .then(
                                                                                Commands.argument(
                                                                                                "amount",
                                                                                                LongArgumentType.longArg(1)
                                                                                        )
                                                                                        .executes(context -> {
                                                                                            ServerPlayer target =
                                                                                                    EntityArgument.getPlayer(
                                                                                                            context,
                                                                                                            "player"
                                                                                                    );

                                                                                            long amount =
                                                                                                    LongArgumentType.getLong(
                                                                                                            context,
                                                                                                            "amount"
                                                                                                    );

                                                                                            DicefireApi.addFame(
                                                                                                    context.getSource().getServer(),
                                                                                                    target.getUUID(),
                                                                                                    amount
                                                                                            );

                                                                                            context.getSource()
                                                                                                    .sendSuccess(
                                                                                                            () -> Component.literal(
                                                                                                                    target.getName().getString()
                                                                                                                            + " erhält "
                                                                                                                            + amount
                                                                                                                            + " Ruhm."
                                                                                                            ),
                                                                                                            true
                                                                                                    );

                                                                                            return 1;
                                                                                        })
                                                                        )
                                                        )
                                        )

                                        .then(
                                                Commands.literal("set")
                                                        .then(
                                                                Commands.argument(
                                                                                "player",
                                                                                EntityArgument.player()
                                                                        )
                                                                        .then(
                                                                                Commands.argument(
                                                                                                "amount",
                                                                                                LongArgumentType.longArg(0)
                                                                                        )
                                                                                        .executes(context -> {
                                                                                            ServerPlayer target =
                                                                                                    EntityArgument.getPlayer(
                                                                                                            context,
                                                                                                            "player"
                                                                                                    );

                                                                                            long amount =
                                                                                                    LongArgumentType.getLong(
                                                                                                            context,
                                                                                                            "amount"
                                                                                                    );

                                                                                            DicefireApi.setFame(
                                                                                                    context.getSource().getServer(),
                                                                                                    target.getUUID(),
                                                                                                    amount
                                                                                            );

                                                                                            context.getSource()
                                                                                                    .sendSuccess(
                                                                                                            () -> Component.literal(
                                                                                                                    "Ruhm von "
                                                                                                                            + target.getName().getString()
                                                                                                                            + " auf "
                                                                                                                            + amount
                                                                                                                            + " gesetzt."
                                                                                                            ),
                                                                                                            true
                                                                                                    );

                                                                                            return 1;
                                                                                        })
                                                                        )
                                                        )
                                        )

                                        .then(
                                                Commands.literal("remove")
                                                        .then(
                                                                Commands.argument(
                                                                                "player",
                                                                                EntityArgument.player()
                                                                        )
                                                                        .then(
                                                                                Commands.argument(
                                                                                                "amount",
                                                                                                LongArgumentType.longArg(1)
                                                                                        )
                                                                                        .executes(context -> {
                                                                                            ServerPlayer target =
                                                                                                    EntityArgument.getPlayer(
                                                                                                            context,
                                                                                                            "player"
                                                                                                    );

                                                                                            long amount =
                                                                                                    LongArgumentType.getLong(
                                                                                                            context,
                                                                                                            "amount"
                                                                                                    );

                                                                                            boolean success =
                                                                                                    DicefireApi.withdrawFame(
                                                                                                            context.getSource().getServer(),
                                                                                                            target.getUUID(),
                                                                                                            amount
                                                                                                    );

                                                                                            if (!success) {
                                                                                                context.getSource()
                                                                                                        .sendFailure(
                                                                                                                Component.literal(
                                                                                                                        "Spieler hat nicht genug Ruhm."
                                                                                                                )
                                                                                                        );

                                                                                                return 0;
                                                                                            }

                                                                                            context.getSource()
                                                                                                    .sendSuccess(
                                                                                                            () -> Component.literal(
                                                                                                                    amount
                                                                                                                            + " Ruhm von "
                                                                                                                            + target.getName().getString()
                                                                                                                            + " entfernt."
                                                                                                            ),
                                                                                                            true
                                                                                                    );

                                                                                            return 1;
                                                                                        })
                                                                        )
                                                        )
                                        )
                        )
        );

        dispatcher.register(
                Commands.literal("progression")

                        .then(
                                Commands.literal("unlock")

                                        .then(
                                                Commands.argument(
                                                                "mod",
                                                                StringArgumentType.word()
                                                        )
                                                        .suggests((context, builder) -> {

                                                            for (ModCategory category : ProgressionConfig.getCategories()) {
                                                                for (ModUnlock unlock : category.unlocks()) {

                                                                    builder.suggest(unlock.id());
                                                                }
                                                            }

                                                            return builder.buildFuture();
                                                        })

                                                        .executes(context -> {

                                                            ServerPlayer player =
                                                                    context.getSource()
                                                                            .getPlayerOrException();

                                                            String mod =
                                                                    StringArgumentType.getString(
                                                                            context,
                                                                            "mod"
                                                                    );

                                                            UnlockResult result =
                                                                    ProgressionManager.purchaseUnlock(
                                                                            context.getSource()
                                                                                    .getServer(),
                                                                            player.getUUID(),
                                                                            mod
                                                                    );

                                                            switch (result) {

                                                                case SUCCESS ->
                                                                        player.sendSystemMessage(
                                                                                Component.literal(
                                                                                        "Mod erfolgreich freigeschaltet!"
                                                                                )
                                                                        );

                                                                case UNKNOWN_MOD ->
                                                                        player.sendSystemMessage(
                                                                                Component.literal(
                                                                                        "Diese Mod kann nicht freigeschaltet werden."
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
                                                            }

                                                            return 1;
                                                        })
                                        )
                        )

                        .then(
                                Commands.literal("lock")

                                        .requires(source ->
                                                source.hasPermission(2)
                                        )

                                        .then(
                                                Commands.argument(
                                                                "mod",
                                                                StringArgumentType.word()
                                                        )

                                                        .suggests((context, builder) -> {

                                                            for (ModCategory category : ProgressionConfig.getCategories()) {
                                                                for (ModUnlock unlock : category.unlocks()) {
                                                                    builder.suggest(unlock.id());
                                                                }
                                                            }

                                                            return builder.buildFuture();
                                                        })

                                                        .executes(context -> {

                                                            ServerPlayer player =
                                                                    context.getSource().getPlayerOrException();

                                                            String mod =
                                                                    StringArgumentType.getString(
                                                                            context,
                                                                            "mod"
                                                                    );

                                                            DicefireApi.lockMod(
                                                                    context.getSource().getServer(),
                                                                    player.getUUID(),
                                                                    mod
                                                            );

                                                            player.sendSystemMessage(
                                                                    Component.literal(
                                                                            mod + " wurde wieder gesperrt."
                                                                    )
                                                            );

                                                            return 1;
                                                        })
                                        )
                        )
        );
    }
}