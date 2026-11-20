package com.j3ly.senbonzakura.entity.worm;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;

/**
 * Per-species tuning for the segmented worms. All variants share the same trail/pose
 * machinery in {@link WormEntity}; only numbers, textures and sounds change.
 *
 * IRON / HELL / SKY are Chaotic Mode bosses and keep the main mod's boss HP values.
 * EARTH / OCEAN are regular mobs with the PoC's smaller stats.
 */
public enum WormVariant {
    IRON(
            "iron_worm",
            6, 0.60F, 0.60F, 0.80F, 0.80F,
            480.0, 12.0, 0.32, 48.0, 4.0,
            2.2F, 1.4F, 3.0F, 1.25F,
            "textures/entity/iron_worm.png",
            SoundEvents.SILVERFISH_AMBIENT, SoundEvents.SILVERFISH_AMBIENT,
            SoundEvents.IRON_GOLEM_ATTACK, SoundEvents.SILVERFISH_HURT, SoundEvents.SILVERFISH_DEATH,
            0x8A8F96, 0x3C3F45,
            "Iron Worm", BossEvent.BossBarColor.GREEN
    ),
    EARTH(
            "earth_worm",
            4, 0.50F, 0.45F, 0.55F, 0.55F,
            14.0, 2.0, 0.30, 32.0, 3.0,
            1.4F, 0.9F, 2.0F, 0.90F,
            "textures/entity/earth_worm.png",
            SoundEvents.SILVERFISH_AMBIENT, SoundEvents.SILVERFISH_AMBIENT,
            SoundEvents.SILVERFISH_STEP, SoundEvents.SILVERFISH_HURT, SoundEvents.SILVERFISH_DEATH,
            0x8B5A2B, 0x4A2E15,
            null, BossEvent.BossBarColor.GREEN
    ),
    HELL(
            "hell_serpent",
            10, 0.70F, 0.85F, 1.15F, 1.15F,
            480.0, 14.0, 0.36, 64.0, 5.0,
            3.2F, 1.7F, 4.5F, 1.75F,
            "textures/entity/hell_serpent.png",
            SoundEvents.BLAZE_AMBIENT, SoundEvents.ENDER_DRAGON_GROWL,
            SoundEvents.IRON_GOLEM_ATTACK, SoundEvents.BLAZE_HURT, SoundEvents.BLAZE_DEATH,
            0xC0392B, 0x4A0F0A,
            "Hell Serpent", BossEvent.BossBarColor.RED
    ),
    SKY(
            "sky_serpent",
            7, 0.65F, 0.60F, 0.85F, 0.85F,
            420.0, 12.0, 0.34, 64.0, 0.0,
            0.0F, 0.0F, 0.0F, 1.35F,
            "textures/entity/sky_serpent.png",
            SoundEvents.PHANTOM_AMBIENT, SoundEvents.PHANTOM_SWOOP,
            SoundEvents.PHANTOM_BITE, SoundEvents.PHANTOM_HURT, SoundEvents.PHANTOM_DEATH,
            0xBBD7EC, 0x4E6E8E,
            "Sky Serpent", BossEvent.BossBarColor.BLUE
    ),
    OCEAN(
            "ocean_serpent",
            7, 0.65F, 0.60F, 0.85F, 0.85F,
            30.0, 6.0, 0.34, 64.0, 0.0,
            0.0F, 0.0F, 0.0F, 1.35F,
            "textures/entity/ocean_serpent.png",
            SoundEvents.PHANTOM_AMBIENT, SoundEvents.PHANTOM_SWOOP,
            SoundEvents.PHANTOM_BITE, SoundEvents.PHANTOM_HURT, SoundEvents.PHANTOM_DEATH,
            0x2E8B8B, 0x123F52,
            null, BossEvent.BossBarColor.BLUE
    );

    private final String id;
    private final int segments;
    private final float segmentSpacing;
    private final float segmentSize;
    private final float hitboxWidth;
    private final float hitboxHeight;
    private final double health;
    private final double damage;
    private final double movementSpeed;
    private final double followRange;
    private final double attackRange;
    private final float headLift;
    private final float lungeSpeed;
    private final float maxLungeDistance;
    private final float modelScale;
    private final String texture;
    private final SoundEvent ambientSound;
    private final SoundEvent telegraphSound;
    private final SoundEvent attackSound;
    private final SoundEvent hurtSound;
    private final SoundEvent deathSound;
    private final int eggPrimary;
    private final int eggSecondary;
    private final String bossName;
    private final BossEvent.BossBarColor bossBarColor;

    WormVariant(String id, int segments, float segmentSpacing, float segmentSize,
                float hitboxWidth, float hitboxHeight,
                double health, double damage, double movementSpeed, double followRange, double attackRange,
                float headLift, float lungeSpeed, float maxLungeDistance, float modelScale,
                String texture, SoundEvent ambientSound, SoundEvent telegraphSound,
                SoundEvent attackSound, SoundEvent hurtSound, SoundEvent deathSound,
                int eggPrimary, int eggSecondary, String bossName, BossEvent.BossBarColor bossBarColor) {
        this.id = id;
        this.segments = segments;
        this.segmentSpacing = segmentSpacing;
        this.segmentSize = segmentSize;
        this.hitboxWidth = hitboxWidth;
        this.hitboxHeight = hitboxHeight;
        this.health = health;
        this.damage = damage;
        this.movementSpeed = movementSpeed;
        this.followRange = followRange;
        this.attackRange = attackRange;
        this.headLift = headLift;
        this.lungeSpeed = lungeSpeed;
        this.maxLungeDistance = maxLungeDistance;
        this.modelScale = modelScale;
        this.texture = texture;
        this.ambientSound = ambientSound;
        this.telegraphSound = telegraphSound;
        this.attackSound = attackSound;
        this.hurtSound = hurtSound;
        this.deathSound = deathSound;
        this.eggPrimary = eggPrimary;
        this.eggSecondary = eggSecondary;
        this.bossName = bossName;
        this.bossBarColor = bossBarColor;
    }

    public String id() { return this.id; }
    public int segments() { return this.segments; }
    public float segmentSpacing() { return this.segmentSpacing; }
    public float segmentSize() { return this.segmentSize; }
    public float hitboxWidth() { return this.hitboxWidth; }
    public float hitboxHeight() { return this.hitboxHeight; }
    public double health() { return this.health; }
    public double damage() { return this.damage; }
    public double movementSpeed() { return this.movementSpeed; }
    public double followRange() { return this.followRange; }
    public double attackRange() { return this.attackRange; }
    public float headLift() { return this.headLift; }
    public float lungeSpeed() { return this.lungeSpeed; }
    public float maxLungeDistance() { return this.maxLungeDistance; }
    public float modelScale() { return this.modelScale; }
    public String texture() { return this.texture; }
    public SoundEvent ambientSound() { return this.ambientSound; }
    public SoundEvent telegraphSound() { return this.telegraphSound; }
    public SoundEvent attackSound() { return this.attackSound; }
    public SoundEvent hurtSound() { return this.hurtSound; }
    public SoundEvent deathSound() { return this.deathSound; }
    public int eggPrimary() { return this.eggPrimary; }
    public int eggSecondary() { return this.eggSecondary; }
    /** Non-null only for the three Chaotic Mode boss worms. */
    public String bossName() { return this.bossName; }
    public BossEvent.BossBarColor bossBarColor() { return this.bossBarColor; }
    public boolean isBoss() { return this.bossName != null; }
}
