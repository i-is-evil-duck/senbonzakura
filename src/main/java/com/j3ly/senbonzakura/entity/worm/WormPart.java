package com.j3ly.senbonzakura.entity.worm;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.neoforged.neoforge.entity.PartEntity;

/**
 * A body segment. Not a real mob: no AI, no persistence, damage routes to the parent.
 */
public class WormPart extends PartEntity<WormEntity> {
    private final String name;
    private final EntityDimensions size;

    public WormPart(WormEntity parent, String name, float width, float height) {
        super(parent);
        this.name = name;
        this.size = EntityDimensions.scalable(width, height);
        this.refreshDimensions();
        this.noPhysics = true; // never collides with blocks or pushes the head
    }

    public String name() {
        return this.name;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.size;
    }

    @Override
    public boolean isPickable() {
        return true; // allow melee/projectile hits to reach the shared health pool
    }

    @Override
    public boolean is(Entity entity) {
        return entity == this || entity == this.getParent();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) return false;
        // Whole worm shares one health pool; later: per-segment armor multipliers
        return this.getParent().hurt(source, amount);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean shouldBeSaved() {
        return false; // parts live and die with their parent
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
        throw new UnsupportedOperationException("Worm parts spawn with their parent");
    }
}
