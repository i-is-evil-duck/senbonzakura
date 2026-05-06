package com.senbonzakura.network;

import com.senbonzakura.SenbonzakuraMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class DashPacket {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SenbonzakuraMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private final boolean dashing;

    public DashPacket(boolean dashing) {
        this.dashing = dashing;
    }

    public static void encode(DashPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.dashing);
    }

    public static DashPacket decode(FriendlyByteBuf buf) {
        return new DashPacket(buf.readBoolean());
    }

    public static void handle(DashPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Server-side handling if needed
        });
        ctx.get().setPacketHandled(true);
    }

    public static void register() {
        int id = 0;
        INSTANCE.registerMessage(id++, DashPacket.class, DashPacket::encode, DashPacket::decode, DashPacket::handle);
    }
}
