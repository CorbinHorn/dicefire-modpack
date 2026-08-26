package de.spieletreff_gera.dicefire_core.network;

import de.spieletreff_gera.dicefire_core.Dicefire_core;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record ProgressionDataPayload(
        long fame,
        List<String> unlockedMods
) implements CustomPacketPayload {

    public static final Type<ProgressionDataPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            Dicefire_core.MODID,
                            "progression_data"
                    )
            );

    public static final StreamCodec<
            ByteBuf,
            ProgressionDataPayload
            > STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_LONG,
                    ProgressionDataPayload::fame,

                    ByteBufCodecs.STRING_UTF8
                            .apply(
                                    ByteBufCodecs.list()
                            ),
                    ProgressionDataPayload::unlockedMods,

                    ProgressionDataPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}