package de.spieletreff_gera.dicefire_core;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import de.spieletreff_gera.dicefire_core.command.DicefireCommands;
import net.neoforged.neoforge.common.NeoForge;
import de.spieletreff_gera.dicefire_core.config.ProgressionConfig;
import de.spieletreff_gera.dicefire_core.network.DicefireNetworking;
import de.spieletreff_gera.dicefire_core.enforcement.CraftingRestrictionHandler;

@Mod(Dicefire_core.MODID)
public class Dicefire_core {

    public static final String MODID = "dicefire_core";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Dicefire_core(IEventBus modEventBus) {

        LOGGER.info("Dicefire Core loaded.");

        ProgressionConfig.load();

        modEventBus.addListener(DicefireNetworking::register);

        NeoForge.EVENT_BUS.addListener(DicefireCommands::register);
        NeoForge.EVENT_BUS.register(CraftingRestrictionHandler.class);
    }
}