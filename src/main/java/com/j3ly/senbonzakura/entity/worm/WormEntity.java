package com.j3ly.senbonzakura.entity.worm;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * Shared head entity for every worm variant. One AI-driven head + N PartEntity segments that
 * sample a ring buffer of past head positions. Ground variants rear up and strike; the sky and
 * ocean variants use phantom-style circling/swooping and never enter the rear-up phase. The head
 * circles continuously; every segment simply follows the head's trail with no gravity.
 *
 * See AGENT.md for design notes.
 */
public abstract class WormEntity extends Monster {
    /** Push the first body segment further down the pose curve so it doesn't clip into the head. */
    private static final float POSE_HEAD_GAP = 0.25F;
    private static final int TRAIL_TICKS = 80;
    /** Hard cap on head-to-segment and segment-to-segment distance, as a multiple of spacing. */
    private static final float MAX_SEGMENT_SPACING_FACTOR = 1.35F;

    // Attack timeline (ticks) - shared by the ground variants
    public static final int REAR_TICKS = 12;      // 0..12   pose blend 0 -> 1
    public static final int STRIKE_TICKS = 18;    // 12..18  lunge + damage sweep
    public static final int RECOVER_TICKS = 32;   // 18..32  blend 1 -> 0
    public static final int ATTACK_COOLDOWN = 40;

    private static final EntityDataAccessor<Byte> DATA_PHASE =
            SynchedEntityData.defineId(WormEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> DATA_ATTACK_TICK =
            SynchedEntityData.defineId(WormEntity.class, EntityDataSerializers.INT);
    // The pose is recomputed on the client, so the tail anchor must be synced too.
    private static final EntityDataAccessor<Float> DATA_TAIL_X =
            SynchedEntityData.defineId(WormEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_TAIL_Y =
            SynchedEntityData.defineId(WormEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_TAIL_Z =
            SynchedEntityData.defineId(WormEntity.class, EntityDataSerializers.FLOAT);

    private final WormPart[] parts;
    private final Deque<Vec3> trail = new ArrayDeque<>();
    private final ServerBossEvent bossEvent;
    private final Set<ServerPlayer> trackedPlayers = new HashSet<>();

    // Server-only lunge target; the tail anchor lives in synched data (see above).
    private Vec3 strikeTarget = Vec3.ZERO;
    /** Position when the attack started, used to cap total forward travel. */
    private Vec3 strikeOrigin = Vec3.ZERO;
    /** Horizontal direction captured at attack start; keeps the lunge forward even if the target moves. */
    private Vec3 strikeDirection = Vec3.ZERO;
    private boolean strikeHit = false;
    private int attackCooldown = 0;

    /** Flight steering target for the sky variant (written by its goal, read by its move control). */
    Vec3 moveTargetPoint = Vec3.ZERO;

    public WormEntity(EntityType<? extends WormEntity> type, Level level) {
        super(type, level);
        WormVariant variant = this.variant();
        this.parts = new WormPart[variant.segments()];
        for (int i = 0; i < variant.segments(); i++) {
            this.parts[i] = new WormPart(this, "segment" + i, variant.segmentSize(), variant.segmentSize());
        }
        // Like the Ender Dragon: keep part ids next to the parent's id so both sides
        // can resolve parts via Level#getEntity(id).
        this.setId(ENTITY_COUNTER.getAndAdd(variant.segments() + 1) + 1);
        this.setNoGravity(true); // PoC: the worm flies/swims; burrowing comes later
        this.moveTargetPoint = this.position();
        this.moveControl = isFlyingVariant(variant)
                ? new SkyMoveControl(this)
                : new WormMoveControl(this);
        if (variant.isBoss()) {
            this.bossEvent = new ServerBossEvent(
                    Component.literal(variant.bossName()),
                    variant.bossBarColor(),
                    BossEvent.BossBarOverlay.NOTCHED_20);
            this.bossEvent.setVisible(true);
        } else {
            this.bossEvent = null;
        }
    }

    private ServerBossEvent bossEvent() { return this.bossEvent; }

    /** Constant per subclass so it is available even while the super constructor runs. */
    public abstract WormVariant variant();

    private static boolean isFlyingVariant(WormVariant variant) {
        return variant == WormVariant.SKY || variant == WormVariant.OCEAN;
    }

    @Override
    public void setId(int id) {
        super.setId(id);
        if (this.parts != null) {
            for (int i = 0; i < this.parts.length; i++) {
                this.parts[i].setId(id + i + 1);
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes(WormVariant variant) {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, variant.health())
                .add(Attributes.ATTACK_DAMAGE, variant.damage())
                .add(Attributes.MOVEMENT_SPEED, variant.movementSpeed())
                .add(Attributes.FOLLOW_RANGE, variant.followRange())
                .add(Attributes.ATTACK_KNOCKBACK, 1.0);
    }

    @Override
    protected void registerGoals() {
        // Called from the Mob constructor, so use the virtual variant() accessor.
        if (isFlyingVariant(this.variant())) {
            // Mirrors vanilla Phantom: strategy only picks the phase, circle maintains the
            // flight path (even with no target), and swoop handles the dive/bite.
            SkyFlightState flight = new SkyFlightState(this);
            this.goalSelector.addGoal(1, new SkyAttackStrategyGoal(this, flight));
            this.goalSelector.addGoal(2, new SkySwoopAttackGoal(this, flight));
            this.goalSelector.addGoal(3, new SkyCircleAroundAnchorGoal(this, flight));
        } else {
            this.goalSelector.addGoal(1, new WormAttackGoal(this));
            this.goalSelector.addGoal(2, new FloatGoal(this));
        }
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    /** Boss worms never despawn naturally. */
    @Override
    public void checkDespawn() {
        if (!this.variant().isBoss()) super.checkDespawn();
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (this.bossEvent != null) {
            this.bossEvent.addPlayer(player);
            this.trackedPlayers.add(player);
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        if (this.bossEvent != null) {
            this.bossEvent.removePlayer(player);
            this.trackedPlayers.remove(player);
        }
    }

    @Override
    public void die(DamageSource source) {
        if (this.bossEvent != null) this.bossEvent.removeAllPlayers();
        super.die(source);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.variant().ambientSound();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return this.variant().hurtSound();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.variant().deathSound();
    }

    /**
     * Phantom parity: {@code FlyingMob} disables fall damage entirely. The flying serpents dive at
     * ground-level targets, so without this the dive ends in fatal fall damage instead of a bite.
     */
    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        if (!isFlyingVariant(this.variant())) {
            super.checkFallDamage(y, onGround, state, pos);
        }
    }

    @Override
    public boolean onClimbable() {
        return !isFlyingVariant(this.variant()) && super.onClimbable();
    }

    public static <T extends WormEntity> boolean checkSpawnRules(EntityType<T> type,
                                                                 ServerLevelAccessor level,
                                                                 MobSpawnType spawnType,
                                                                 BlockPos pos,
                                                                 net.minecraft.util.RandomSource random) {
        return Monster.checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_PHASE, (byte) 0);
        builder.define(DATA_ATTACK_TICK, 0);
        builder.define(DATA_TAIL_X, 0.0F);
        builder.define(DATA_TAIL_Y, 0.0F);
        builder.define(DATA_TAIL_Z, 0.0F);
    }

    @Override
    public PartEntity<?>[] getParts() {
        return this.parts;
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    public int getPhase() { return this.entityData.get(DATA_PHASE); }
    public int getAttackTick() { return this.entityData.get(DATA_ATTACK_TICK); }

    private Vec3 getTailAnchor() {
        return new Vec3(this.entityData.get(DATA_TAIL_X),
                this.entityData.get(DATA_TAIL_Y),
                this.entityData.get(DATA_TAIL_Z));
    }

    /** 0..1 blend factor for the rear-up pose; flying serpents never rear up. */
    public float getPoseBlend() {
        if (isFlyingVariant(this.variant()) || getPhase() == 0) return 0.0F;
        int t = getAttackTick();
        if (t <= REAR_TICKS) return smooth(t / (float) REAR_TICKS);
        if (t <= STRIKE_TICKS) return 1.0F;
        return 1.0F - smooth((t - STRIKE_TICKS) / (float) (RECOVER_TICKS - STRIKE_TICKS));
    }

    private static float smooth(float t) {
        t = Mth.clamp(t, 0.0F, 1.0F);
        return t * t * (3.0F - 2.0F * t);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.bossEvent != null && this.level() instanceof ServerLevel serverLevel) {
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
            for (ServerPlayer player : serverLevel.getPlayers(p -> true)) {
                if (!this.trackedPlayers.contains(player)
                        && player.distanceToSqr(this) < 64.0 * 64.0) {
                    this.bossEvent.addPlayer(player);
                    this.trackedPlayers.add(player);
                } else if (this.trackedPlayers.contains(player)
                        && player.distanceToSqr(this) > 80.0 * 80.0) {
                    this.bossEvent.removePlayer(player);
                    this.trackedPlayers.remove(player);
                }
            }
        }
        if (!this.level().isClientSide() && !isFlyingVariant(this.variant())) {
            if (this.attackCooldown > 0) this.attackCooldown--;
            tickAttack();
        }

        this.trail.addFirst(this.position());
        if (this.trail.size() > TRAIL_TICKS) this.trail.removeLast();

        updateParts();
    }

    /** Called by the attack goal (server side only). */
    public void startAttack(LivingEntity target) {
        if (getPhase() != 0 || this.attackCooldown > 0) return;
        WormVariant variant = this.variant();
        Vec3 tailAnchor = pointAtDistance(variant.segments() * variant.segmentSpacing()).add(0, -0.35, 0);
        this.entityData.set(DATA_TAIL_X, (float) tailAnchor.x);
        this.entityData.set(DATA_TAIL_Y, (float) tailAnchor.y);
        this.entityData.set(DATA_TAIL_Z, (float) tailAnchor.z);
        this.strikeOrigin = this.position();
        Vec3 targetCenter = target.getBoundingBox().getCenter();
        Vec3 toTarget = targetCenter.subtract(this.position());
        Vec3 flat = new Vec3(toTarget.x, 0.0, toTarget.z);
        if (flat.lengthSqr() > 1.0E-4) {
            this.strikeDirection = flat.normalize();
        } else {
            Vec3 look = Vec3.directionFromRotation(0.0F, this.getYRot());
            this.strikeDirection = new Vec3(look.x, 0.0, look.z).normalize();
        }
        this.strikeTarget = targetCenter;
        this.strikeHit = false;
        this.playSound(variant.telegraphSound(), 0.6F, 0.6F + this.random.nextFloat() * 0.1F);
        this.lookAt(target, 180.0F, 180.0F);
        this.entityData.set(DATA_PHASE, (byte) 1);
        this.entityData.set(DATA_ATTACK_TICK, 0);
    }

    /** Horizontal distance travelled since the attack began. */
    private double strikeTravel() {
        return Math.hypot(this.getX() - this.strikeOrigin.x, this.getZ() - this.strikeOrigin.z);
    }

    /** Server-only attack timeline; attack tick/phase reach the client through synched data. */
    private void tickAttack() {
        if (getPhase() == 0) return;
        WormVariant variant = this.variant();
        int t = getAttackTick() + 1;
        double jumpRemaining = variant.maxLungeDistance() - this.strikeTravel();

        // Keep pushing forward while rearing up, but spend the same limited jump budget.
        if (t <= REAR_TICKS) {
            float creep = 0.06F + 0.14F * getPoseBlend();
            if (jumpRemaining > 0.0) {
                this.setDeltaMovement(this.strikeDirection.scale(Math.min(creep, jumpRemaining)));
            }
        }
        if (t == REAR_TICKS + 1) {
            // Full forward jump at the strike, aimed at the point captured when the attack started.
            Vec3 dir = this.strikeTarget.subtract(position());
            Vec3 flat = new Vec3(dir.x, 0.0, dir.z);
            Vec3 lungeDir = flat.lengthSqr() > 1.0E-4 ? flat.normalize() : this.strikeDirection;
            if (jumpRemaining > 0.0) {
                this.setDeltaMovement(lungeDir.scale(Math.min(variant.lungeSpeed(), jumpRemaining)));
            }
            this.playSound(variant.attackSound(), 0.9F, 0.7F + this.random.nextFloat() * 0.15F);
        }
        if (t > REAR_TICKS + 1 && t <= STRIKE_TICKS) {
            // Friction would stall the head after one tick; keep the jump going until the budget runs out.
            if (jumpRemaining > 0.0) {
                this.setDeltaMovement(this.strikeDirection.scale(Math.min(variant.lungeSpeed() * 0.85F, jumpRemaining)));
            } else {
                this.setDeltaMovement(Vec3.ZERO);
            }
        }
        if (t > REAR_TICKS + 1 && t <= STRIKE_TICKS && !this.strikeHit) {
            // One damage sweep per attack, not one per tick.
            // Match the swept box to the visually raised head, not the grounded entity origin.
            AABB box = this.getBoundingBox().inflate(0.7)
                    .move(0.0, variant.headLift() * getPoseBlend(), 0.0);
            for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class, box, e -> e != this && !e.is(this))) {
                doHurtTarget(e);
            }
            this.strikeHit = true;
        }
        if (t >= RECOVER_TICKS) {
            this.entityData.set(DATA_PHASE, (byte) 0);
            this.entityData.set(DATA_ATTACK_TICK, 0);
            this.attackCooldown = ATTACK_COOLDOWN;
            this.setDeltaMovement(Vec3.ZERO);
            return;
        }
        this.entityData.set(DATA_ATTACK_TICK, t);
    }

    private void updateParts() {
        float blend = getPoseBlend();
        Vec3 headPos = this.position();
        WormVariant variant = this.variant();
        float partYOffset = 0.35F * (variant.segmentSize() / 0.6F);
        double maxSpacing = Math.max(variant.segmentSpacing() * MAX_SEGMENT_SPACING_FACTOR,
                variant.segmentSize() * 1.1F);
        for (int i = 0; i < this.parts.length; i++) {
            WormPart part = this.parts[i];

            // Parts are never ticked by the level, so snapshot their previous transform
            // ourselves before moving them (the Ender Dragon does the same for its parts).
            // Doing this after moving would collapse client interpolation to a no-op.
            part.setOldPosAndRot();

            Vec3 front = i == 0 ? headPos : this.parts[i - 1].position();
            Vec3 trailPos = pointAtDistance((i + 1) * variant.segmentSpacing());
            Vec3 pos = trailPos;
            if (blend > 0.001F) {
                Vec3 posed = posedPosition(i, blend, headPos);
                pos = trailPos.add(posed.subtract(trailPos).scale(blend));
            }
            pos = new Vec3(pos.x, pos.y + partYOffset, pos.z);

            // Never let the chain stretch: clamp against the head (first segment) or the
            // already-final position of the segment in front. Trail sampling already keeps
            // arc-length spacing; this also catches teleports.
            Vec3 delta = pos.subtract(front);
            double distance = delta.length();
            if (distance > maxSpacing) {
                pos = front.add(delta.scale(maxSpacing / distance));
            }

            // Face along the chain (toward the segment/head in front).
            // Minecraft yaw 0 faces +Z, so atan2(-dx, dz) points at (dx, dz).
            float yaw = (float) Math.toDegrees(Math.atan2(-(front.x - pos.x), front.z - pos.z));
            part.setPos(pos.x, pos.y, pos.z);
            part.setYRot(yaw);
        }
    }

    /** Point at arc-distance {@code dist} along the trail polyline (0 = head position). */
    private Vec3 pointAtDistance(double dist) {
        Vec3 prev = this.position();
        double acc = 0.0;
        for (Vec3 p : this.trail) {
            double d = prev.distanceTo(p);
            if (d < 1.0E-6) continue;
            if (acc + d >= dist) {
                double t = (dist - acc) / d;
                return prev.add(p.subtract(prev).scale(t));
            }
            acc += d;
            prev = p;
        }
        return prev;
    }

    /** Rear-up pose: quadratic Bezier from the raised head to the synced tail anchor. */
    private Vec3 posedPosition(int i, float blend, Vec3 headPos) {
        // Remap along the curve so the first segment starts further from the head,
        // while the last segment still lands on the tail anchor.
        double raw = (i + 1.0) / (double) this.parts.length;
        double s = POSE_HEAD_GAP + (1.0 - POSE_HEAD_GAP) * raw;
        double lift = this.variant().headLift() * blend;
        Vec3 raisedHead = headPos.add(0, lift, 0);
        Vec3 facing = Vec3.directionFromRotation(0.0F, this.getYRot());
        Vec3 ctrl = raisedHead.add(facing.scale(1.2)).add(0, lift * 0.5, 0);
        Vec3 a = raisedHead.add(ctrl.subtract(raisedHead).scale(s));
        Vec3 b = ctrl.add(getTailAnchor().subtract(ctrl).scale(s));
        return a.add(b.subtract(a).scale(s));
    }
}
