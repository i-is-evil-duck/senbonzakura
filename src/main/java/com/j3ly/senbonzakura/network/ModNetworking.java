package com.j3ly.senbonzakura.network;

import com.j3ly.senbonzakura.SenbonzakuraMod;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = SenbonzakuraMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class ModNetworking {

    private static final Map<UUID, SpinState> ACTIVE_SPINS = new ConcurrentHashMap<>();

    public record SpinState(long startTick, float degrees, int durationTicks, boolean pingPong) {}

    private ModNetworking() {}

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(
                SpinAttackPayload.TYPE,
                SpinAttackPayload.STREAM_CODEC,
                ModNetworking::handleSpin
        );
    }

    /** Call on the server when a sweep attack fires. */
    public static void sendSpin(Player player, float degrees, int durationTicks, boolean pingPong) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player, new SpinAttackPayload(player.getUUID(), degrees, durationTicks, pingPong));
    }

    private static void handleSpin(SpinAttackPayload payload, IPayloadContext context) {
        // context.player() is the local client player here; using Player/Level
        // keeps this class free of client-only references for dedicated servers.
        Player player = context.player();
        long tick = player != null && player.level() != null ? player.level().getGameTime() : 0L;
        ACTIVE_SPINS.put(payload.playerUuid(),
                new SpinState(tick, payload.degrees(), payload.durationTicks(), payload.pingPong()));
    }

    public static SpinState getSpin(UUID uuid) {
        return ACTIVE_SPINS.get(uuid);
    }

    public static void clearSpin(UUID uuid) {
        ACTIVE_SPINS.remove(uuid);
    }
}
