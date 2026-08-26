package de.spieletreff_gera.dicefire_core.network;

import de.spieletreff_gera.dicefire_core.Dicefire_core;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UnlockModPayload(String unlockId) implements CustomPacketPayload {

    public static final Type<UnlockModPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            Dicefire_core.MODID,
                            "unlock_mod"
                    )
            );

    public static final StreamCodec<ByteBuf, UnlockModPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    UnlockModPayload::unlockId,
                    UnlockModPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}