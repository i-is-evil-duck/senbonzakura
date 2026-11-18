package com.j3ly.senbonzakura;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Same-world white-room arena for the Ducky Domain ability. Building the box in
 * a force-loaded far-away chunk avoids cross-dimension loading screens and the
 * chunk-generation cost of a real second dimension.
 */
public final class DuckyDomain {
    /** Radius, in blocks, that the ability pulls entities from. */
    public static final int RADIUS = 15;
    /** 30 seconds. */
    public static final int DURATION_TICKS = 600;
    private static final int HALF_SIDE = 10;

    private static Session active;

    private DuckyDomain() {
    }

    public static boolean isActive() {
        return active != null;
    }

    public static String status() {
        if (active == null) return "No Ducky Domain is active.";
        long remaining = Math.max(0, active.expiresAt - active.worldTime);
        return "Ducky Domain active: " + active.participants.size() + " captured for "
                + (remaining / 20) + " more second(s).";
    }

    public static boolean open(ServerLevel level, Player caster) {
        if (active != null) return false;

        BlockPos center = arenaCenter(level);
        AABB area = new AABB(
                caster.getX() - RADIUS, caster.getY() - RADIUS, caster.getZ() - RADIUS,
                caster.getX() + RADIUS, caster.getY() + RADIUS, caster.getZ() + RADIUS);
        List<LivingEntity> targets = new ArrayList<>();
        for (Entity entity : level.getEntities(caster, area, e -> e instanceof LivingEntity)) {
            targets.add((LivingEntity) entity);
        }
        if (!targets.contains(caster)) targets.add(caster);

        List<ChunkPos> forcedChunks = forceChunks(level, center);
        buildArena(level, center);

        Session session = new Session(level.dimension(), center, forcedChunks,
                level.getGameTime() + DURATION_TICKS);
        double spread = HALF_SIDE - 2;
        for (LivingEntity living : targets) {
            Vec3 original = living.position();
            session.participants.add(new Captured(living.getUUID(), living instanceof Player,
                    living, original, living.getYRot(), living.getXRot()));
            double x = center.getX() + 0.5 + (level.getRandom().nextDouble() - 0.5) * spread;
            double z = center.getZ() + 0.5 + (level.getRandom().nextDouble() - 0.5) * spread;
            living.teleportTo(x, center.getY() + 1, z);
            living.setDeltaMovement(Vec3.ZERO);
        }
        active = session;

        Vec3 casterOriginal = session.participants.get(session.participants.size() - 1).original;
        level.playSound(null, casterOriginal.x, casterOriginal.y, casterOriginal.z,
                ModSounds.DUCKYS_WAND_DOMAIN.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
        return true;
    }

    public static void tick(MinecraftServer server) {
        Session session = active;
        if (session == null) return;
        ServerLevel level = server.getLevel(session.dimension);
        if (level == null) return;
        session.worldTime = level.getGameTime();

        BlockPos center = session.center;
        for (Captured captured : session.participants) {
            Entity entity = resolve(server, captured);
            if (entity == null || !entity.isAlive()) continue;
            Vec3 pos = entity.position();
            boolean outside = pos.x < center.getX() - HALF_SIDE || pos.x > center.getX() + HALF_SIDE
                    || pos.z < center.getZ() - HALF_SIDE || pos.z > center.getZ() + HALF_SIDE
                    || pos.y < center.getY() + 1 || pos.y > center.getY() + 5;
            if (outside) {
                entity.teleportTo(center.getX() + 0.5, center.getY() + 1, center.getZ() + 0.5);
                entity.setDeltaMovement(Vec3.ZERO);
            }
        }

        if (session.worldTime >= session.expiresAt) close(server);
    }

    /** Ends the current domain early (also used by the admin command). */
    public static void close(MinecraftServer server) {
        Session session = active;
        if (session == null) return;
        ServerLevel level = server.getLevel(session.dimension);
        if (level != null) {
            for (ChunkPos chunk : session.forcedChunks) {
                level.setChunkForced(chunk.x, chunk.z, false);
            }
            for (Captured captured : session.participants) {
                Entity entity = resolve(server, captured);
                if (entity == null || !entity.isAlive()) continue;
                entity.teleportTo(captured.original.x, captured.original.y, captured.original.z);
                entity.setYRot(captured.yRot);
                entity.setXRot(captured.xRot);
                entity.setDeltaMovement(Vec3.ZERO);
            }
        }
        active = null;
    }

    public static boolean isInsideArena(ResourceKey<Level> dimension, BlockPos pos) {
        Session session = active;
        if (session == null || session.dimension != dimension) return false;
        BlockPos center = session.center;
        return Math.abs(pos.getX() - center.getX()) <= HALF_SIDE
                && Math.abs(pos.getZ() - center.getZ()) <= HALF_SIDE
                && pos.getY() >= center.getY() && pos.getY() <= center.getY() + 5;
    }

    private static Entity resolve(MinecraftServer server, Captured captured) {
        if (captured.isPlayer) return server.getPlayerList().getPlayer(captured.uuid);
        return captured.entity != null && captured.entity.isAlive() ? captured.entity : null;
    }

    private static BlockPos arenaCenter(ServerLevel level) {
        int y = level.dimension() == Level.NETHER ? 80 : 120;
        return new BlockPos(100000, y, 100000);
    }

    private static List<ChunkPos> forceChunks(ServerLevel level, BlockPos center) {
        List<ChunkPos> forced = new ArrayList<>();
        int minX = (center.getX() - HALF_SIDE - 2) >> 4;
        int maxX = (center.getX() + HALF_SIDE + 2) >> 4;
        int minZ = (center.getZ() - HALF_SIDE - 2) >> 4;
        int maxZ = (center.getZ() + HALF_SIDE + 2) >> 4;
        for (int cx = minX; cx <= maxX; cx++) {
            for (int cz = minZ; cz <= maxZ; cz++) {
                if (level.setChunkForced(cx, cz, true)) forced.add(new ChunkPos(cx, cz));
            }
        }
        return forced;
    }

    private static void buildArena(ServerLevel level, BlockPos center) {
        if (level.getBlockState(center).is(Blocks.WHITE_CONCRETE)) return;
        int minX = center.getX() - HALF_SIDE - 1;
        int maxX = center.getX() + HALF_SIDE + 1;
        int minZ = center.getZ() - HALF_SIDE - 1;
        int maxZ = center.getZ() + HALF_SIDE + 1;
        int minY = center.getY();
        int maxY = center.getY() + 6;
        for (int x = minX - 1; x <= maxX + 1; x++) {
            for (int z = minZ - 1; z <= maxZ + 1; z++) {
                for (int y = minY - 1; y <= maxY + 1; y++) {
                    // The inner shell is a visible white room; an invisible barrier
                    // layer just outside it keeps anyone from mining or phasing out.
                    boolean whiteShell = x >= minX && x <= maxX && z >= minZ && z <= maxZ
                            && y >= minY && y <= maxY
                            && (x == minX || x == maxX || z == minZ || z == maxZ
                                || y == minY || y == maxY);
                    boolean barrierShell = x == minX - 1 || x == maxX + 1
                            || z == minZ - 1 || z == maxZ + 1
                            || y == minY - 1 || y == maxY + 1;
                    // Flag 2 sends the update to clients without triggering neighbor updates.
                    if (whiteShell) {
                        level.setBlock(new BlockPos(x, y, z), Blocks.WHITE_CONCRETE.defaultBlockState(), 2);
                    } else if (barrierShell) {
                        level.setBlock(new BlockPos(x, y, z), Blocks.BARRIER.defaultBlockState(), 2);
                    }
                }
            }
        }
    }

    private static final class Session {
        final ResourceKey<Level> dimension;
        final BlockPos center;
        final List<ChunkPos> forcedChunks;
        final List<Captured> participants = new ArrayList<>();
        final long expiresAt;
        long worldTime;

        Session(ResourceKey<Level> dimension, BlockPos center, List<ChunkPos> forcedChunks, long expiresAt) {
            this.dimension = dimension;
            this.center = center;
            this.forcedChunks = forcedChunks;
            this.expiresAt = expiresAt;
        }
    }

    private static final class Captured {
        final UUID uuid;
        final boolean isPlayer;
        final Entity entity;
        final Vec3 original;
        final float yRot;
        final float xRot;

        Captured(UUID uuid, boolean isPlayer, Entity entity, Vec3 original, float yRot, float xRot) {
            this.uuid = uuid;
            this.isPlayer = isPlayer;
            this.entity = entity;
            this.original = original;
            this.yRot = yRot;
            this.xRot = xRot;
        }
    }
}
