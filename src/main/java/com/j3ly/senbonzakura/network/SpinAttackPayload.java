package com.j3ly.senbonzakura.network;

import com.j3ly.senbonzakura.SenbonzakuraMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/**
 * Clientbound packet telling clients to visually spin a player's model during a
 * 180/360 degree sweep attack. Purely cosmetic - the damage is handled server-side.
 */
public record SpinAttackPayload(UUID playerUuid, float degrees, int durationTicks, boolean pingPong)
        implements CustomPacketPayload {

    public static final Type<SpinAttackPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SenbonzakuraMod.MOD_ID, "spin_attack"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpinAttackPayload> STREAM_CODEC =
            CustomPacketPayload.codec(
                    (payload, buf) -> {
                        buf.writeUUID(payload.playerUuid());
                        buf.writeFloat(payload.degrees());
                        buf.writeVarInt(payload.durationTicks());
                        buf.writeBoolean(payload.pingPong());
                    },
                    buf -> new SpinAttackPayload(
                            buf.readUUID(), buf.readFloat(), buf.readVarInt(), buf.readBoolean())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
