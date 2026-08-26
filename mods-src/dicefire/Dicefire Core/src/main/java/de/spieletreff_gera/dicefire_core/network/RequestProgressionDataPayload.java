package de.spieletreff_gera.dicefire_core.network;

import de.spieletreff_gera.dicefire_core.Dicefire_core;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RequestProgressionDataPayload()
        implements CustomPacketPayload {

    public static final Type<RequestProgressionDataPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            Dicefire_core.MODID,
                            "request_progression_data"
                    )
            );

    public static final StreamCodec<
            ByteBuf,
            RequestProgressionDataPayload
            > STREAM_CODEC =
            StreamCodec.unit(
                    new RequestProgressionDataPayload()
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}