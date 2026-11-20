package com.j3ly.senbonzakura.entity;

import com.j3ly.senbonzakura.SenbonzakuraMod;
import com.j3ly.senbonzakura.entity.boss.ChaoticBossEntity;
import com.j3ly.senbonzakura.entity.boss.DralliusChaosUnleashedEntity;
import com.j3ly.senbonzakura.entity.boss.DralliusVisionsOfDoomEntity;
import com.j3ly.senbonzakura.entity.boss.FelioOfFlameEntity;
import com.j3ly.senbonzakura.entity.boss.FrozenSerpentEntity;
import com.j3ly.senbonzakura.entity.boss.IeirithOfIceEntity;
import com.j3ly.senbonzakura.entity.boss.KingSlimeEntity;
import com.j3ly.senbonzakura.entity.boss.KrakenGuardianEntity;
import com.j3ly.senbonzakura.entity.boss.LuciusRetainerOfRuinEntity;
import com.j3ly.senbonzakura.entity.boss.QueenSpiderEntity;
import com.j3ly.senbonzakura.entity.boss.SakuraiWahikoEntity;
import com.j3ly.senbonzakura.entity.boss.SerrikOfShadowEntity;
import com.j3ly.senbonzakura.entity.boss.SinfulBishopEntity;
import com.j3ly.senbonzakura.entity.boss.SinfulSquireEntity;
import com.j3ly.senbonzakura.entity.boss.SinisterKnightEntity;
import com.j3ly.senbonzakura.entity.boss.SkeletonGeneralEntity;
import com.j3ly.senbonzakura.entity.boss.TherimOfThunderEntity;
import com.j3ly.senbonzakura.entity.boss.WahikoOfWindEntity;
import com.j3ly.senbonzakura.entity.boss.WendigoWarlordEntity;
import com.j3ly.senbonzakura.entity.boss.WerewolfTribeChiefEntity;
import com.j3ly.senbonzakura.entity.boss.ZombieGeneralEntity;
import com.j3ly.senbonzakura.entity.worm.EarthWormEntity;
import com.j3ly.senbonzakura.entity.worm.HellSerpentEntity;
import com.j3ly.senbonzakura.entity.worm.IronWormEntity;
import com.j3ly.senbonzakura.entity.worm.OceanSerpentEntity;
import com.j3ly.senbonzakura.entity.worm.SkySerpentEntity;
import com.j3ly.senbonzakura.entity.worm.WormVariant;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.IronGolem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Registers all custom boss entities. */
public final class ModEntities {
    private ModEntities() {
    }

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, SenbonzakuraMod.MOD_ID);

    /** King Slime — pre-iron tier boss, 44 HP (22 hearts), vanilla slime reskin. */
    public static final DeferredHolder<EntityType<?>, EntityType<KingSlimeEntity>> KING_SLIME =
            ENTITY_TYPES.register("king_slime", () -> EntityType.Builder
                    .<KingSlimeEntity>of(KingSlimeEntity::new, MobCategory.MONSTER)
                    .sized(2.3F, 2.3F)
                    .clientTrackingRange(10)
                    .build("king_slime"));

    /** Zombie General — pre-iron tier boss, 80 HP (40 hearts), vanilla zombie reskin. */
    public static final DeferredHolder<EntityType<?>, EntityType<ZombieGeneralEntity>> ZOMBIE_GENERAL =
            ENTITY_TYPES.register("zombie_general", () -> EntityType.Builder
                    .<ZombieGeneralEntity>of(ZombieGeneralEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10)
                    .build("zombie_general"));

    /** Skeleton General — pre-iron tier boss, 80 HP (40 hearts), vanilla skeleton reskin. */
    public static final DeferredHolder<EntityType<?>, EntityType<SkeletonGeneralEntity>> SKELETON_GENERAL =
            ENTITY_TYPES.register("skeleton_general", () -> EntityType.Builder
                    .<SkeletonGeneralEntity>of(SkeletonGeneralEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(10)
                    .build("skeleton_general"));

    /** Queen Spider — pre-iron tier boss, 90 HP (45 hearts), vanilla spider reskin, 2x size. */
    public static final DeferredHolder<EntityType<?>, EntityType<QueenSpiderEntity>> QUEEN_SPIDER =
            ENTITY_TYPES.register("queen_spider", () -> EntityType.Builder
                    .<QueenSpiderEntity>of(QueenSpiderEntity::new, MobCategory.MONSTER)
                    .sized(2.8F, 1.8F)
                    .clientTrackingRange(10)
                    .build("queen_spider"));

    /** Iron Worm — pre-iron tier gate, 480 HP. Real segmented worm from the Iron Worm PoC. */
    public static final DeferredHolder<EntityType<?>, EntityType<IronWormEntity>> IRON_WORM =
            ENTITY_TYPES.register("iron_worm", () -> EntityType.Builder
                    .<IronWormEntity>of(IronWormEntity::new, MobCategory.MONSTER)
                    .sized(WormVariant.IRON.hitboxWidth(), WormVariant.IRON.hitboxHeight())
                    .clientTrackingRange(12)
                    .build("iron_worm"));

    /** Earthworm — small, weak overworld worm. Spawn egg only. */
    public static final DeferredHolder<EntityType<?>, EntityType<EarthWormEntity>> EARTH_WORM =
            ENTITY_TYPES.register("earth_worm", () -> EntityType.Builder
                    .<EarthWormEntity>of(EarthWormEntity::new, MobCategory.MONSTER)
                    .sized(WormVariant.EARTH.hitboxWidth(), WormVariant.EARTH.hitboxHeight())
                    .clientTrackingRange(10)
                    .build("earth_worm"));

    /** Werewolf Tribe Chief — post-Ender Dragon / Wither boss, 400 HP (200 hearts). Iron Golem placeholder. */
    public static final DeferredHolder<EntityType<?>, EntityType<WerewolfTribeChiefEntity>> WEREWOLF_TRIBE_CHIEF =
            ENTITY_TYPES.register("werewolf_tribe_chief", () -> EntityType.Builder
                    .<WerewolfTribeChiefEntity>of(WerewolfTribeChiefEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 1.2F)
                    .clientTrackingRange(10)
                    .build("werewolf_tribe_chief"));

    /** Sky Serpent — post-Ender Dragon / Wither boss, 420 HP. Flying segmented worm. */
    public static final DeferredHolder<EntityType<?>, EntityType<SkySerpentEntity>> SKY_SERPENT =
            ENTITY_TYPES.register("sky_serpent", () -> EntityType.Builder
                    .<SkySerpentEntity>of(SkySerpentEntity::new, MobCategory.MONSTER)
                    .sized(WormVariant.SKY.hitboxWidth(), WormVariant.SKY.hitboxHeight())
                    .clientTrackingRange(12)
                    .build("sky_serpent"));

    /** Hell Serpent — post-Ender Dragon / Wither boss, 480 HP. Nether-restricted segmented worm. */
    public static final DeferredHolder<EntityType<?>, EntityType<HellSerpentEntity>> HELL_SERPENT =
            ENTITY_TYPES.register("hell_serpent", () -> EntityType.Builder
                    .<HellSerpentEntity>of(HellSerpentEntity::new, MobCategory.MONSTER)
                    .sized(WormVariant.HELL.hitboxWidth(), WormVariant.HELL.hitboxHeight())
                    .clientTrackingRange(12)
                    .build("hell_serpent"));

    /** Ocean Serpent — water-dwelling phantom-style serpent. Natural spawn in oceans. */
    public static final DeferredHolder<EntityType<?>, EntityType<OceanSerpentEntity>> OCEAN_SERPENT =
            ENTITY_TYPES.register("ocean_serpent", () -> EntityType.Builder
                    .<OceanSerpentEntity>of(OceanSerpentEntity::new, MobCategory.WATER_CREATURE)
                    .sized(WormVariant.OCEAN.hitboxWidth(), WormVariant.OCEAN.hitboxHeight())
                    .clientTrackingRange(10)
                    .build("ocean_serpent"));

    /** Frozen Serpent — post-Warden boss, 500 HP (250 hearts). Iron Golem placeholder. */
    public static final DeferredHolder<EntityType<?>, EntityType<FrozenSerpentEntity>> FROZEN_SERPENT =
            ENTITY_TYPES.register("frozen_serpent", () -> EntityType.Builder
                    .<FrozenSerpentEntity>of(FrozenSerpentEntity::new, MobCategory.MONSTER)
                    .sized(1.4F, 2.9F)
                    .clientTrackingRange(12)
                    .build("frozen_serpent"));

    /** Kraken Guardian — post-Warden boss, 520 HP (260 hearts). Iron Golem placeholder. */
    public static final DeferredHolder<EntityType<?>, EntityType<KrakenGuardianEntity>> KRAKEN_GUARDIAN =
            ENTITY_TYPES.register("kraken_guardian", () -> EntityType.Builder
                    .<KrakenGuardianEntity>of(KrakenGuardianEntity::new, MobCategory.MONSTER)
                    .sized(1.4F, 2.9F)
                    .clientTrackingRange(12)
                    .build("kraken_guardian"));

    /** Wendigo Warlord — post-Warden boss, 600 HP (300 hearts). Iron Golem placeholder. */
    public static final DeferredHolder<EntityType<?>, EntityType<WendigoWarlordEntity>> WENDIGO_WARLORD =
            ENTITY_TYPES.register("wendigo_warlord", () -> EntityType.Builder
                    .<WendigoWarlordEntity>of(WendigoWarlordEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 1.2F)
                    .clientTrackingRange(10)
                    .build("wendigo_warlord"));

    /** Sinful Squire — post-Wendigo Warlord boss, 700 HP (350 hearts). Iron Golem placeholder. */
    public static final DeferredHolder<EntityType<?>, EntityType<SinfulSquireEntity>> SINFUL_SQUIRE =
            ENTITY_TYPES.register("sinful_squire", () -> EntityType.Builder
                    .<SinfulSquireEntity>of(SinfulSquireEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 1.2F)
                    .clientTrackingRange(10)
                    .build("sinful_squire"));

    /** Sinful Bishop — post-Wendigo Warlord boss, 700 HP (350 hearts). Iron Golem placeholder. */
    public static final DeferredHolder<EntityType<?>, EntityType<SinfulBishopEntity>> SINFUL_BISHOP =
            ENTITY_TYPES.register("sinful_bishop", () -> EntityType.Builder
                    .<SinfulBishopEntity>of(SinfulBishopEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 1.2F)
                    .clientTrackingRange(10)
                    .build("sinful_bishop"));

    /** Sinister Knight — post-Wendigo Warlord boss, 800 HP (400 hearts). Iron Golem placeholder. */
    public static final DeferredHolder<EntityType<?>, EntityType<SinisterKnightEntity>> SINISTER_KNIGHT =
            ENTITY_TYPES.register("sinister_knight", () -> EntityType.Builder
                    .<SinisterKnightEntity>of(SinisterKnightEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 1.2F)
                    .clientTrackingRange(10)
                    .build("sinister_knight"));

    /** Felio, of Flame — post-Sinister Knight boss, 1000 HP (500 hearts). Iron Golem placeholder. */
    public static final DeferredHolder<EntityType<?>, EntityType<FelioOfFlameEntity>> FELIO_OF_FLAME =
            ENTITY_TYPES.register("felio_of_flame", () -> EntityType.Builder
                    .<FelioOfFlameEntity>of(FelioOfFlameEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 1.2F)
                    .clientTrackingRange(10)
                    .build("felio_of_flame"));

    /** Ieirith, of Ice — post-Sinister Knight boss, 1000 HP (500 hearts). Iron Golem placeholder. */
    public static final DeferredHolder<EntityType<?>, EntityType<IeirithOfIceEntity>> IEIRITH_OF_ICE =
            ENTITY_TYPES.register("ieirith_of_ice", () -> EntityType.Builder
                    .<IeirithOfIceEntity>of(IeirithOfIceEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 1.2F)
                    .clientTrackingRange(10)
                    .build("ieirith_of_ice"));

    /** Therim, of Thunder — post-Sinister Knight boss, 1000 HP (500 hearts). Iron Golem placeholder. */
    public static final DeferredHolder<EntityType<?>, EntityType<TherimOfThunderEntity>> THERIM_OF_THUNDER =
            ENTITY_TYPES.register("therim_of_thunder", () -> EntityType.Builder
                    .<TherimOfThunderEntity>of(TherimOfThunderEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 1.2F)
                    .clientTrackingRange(10)
                    .build("therim_of_thunder"));

    /** Wahiko, of Wind — post-Sinister Knight boss, 1000 HP (500 hearts). Iron Golem placeholder. */
    public static final DeferredHolder<EntityType<?>, EntityType<WahikoOfWindEntity>> WAHIKO_OF_WIND =
            ENTITY_TYPES.register("wahiko_of_wind", () -> EntityType.Builder
                    .<WahikoOfWindEntity>of(WahikoOfWindEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 1.2F)
                    .clientTrackingRange(10)
                    .build("wahiko_of_wind"));

    /** Sakurai Wahiko, Senbonzakura — Cherry Grove variant, 2500 HP (1250 hearts). Iron Golem placeholder. */
    public static final DeferredHolder<EntityType<?>, EntityType<SakuraiWahikoEntity>> SAKURAI_WAHIKO =
            ENTITY_TYPES.register("sakurai_wahiko", () -> EntityType.Builder
                    .<SakuraiWahikoEntity>of(SakuraiWahikoEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 1.2F)
                    .clientTrackingRange(10)
                    .build("sakurai_wahiko"));

    /** Serrik, of Shadow — post-Sinister Knight boss, 1000 HP (500 hearts). Iron Golem placeholder. */
    public static final DeferredHolder<EntityType<?>, EntityType<SerrikOfShadowEntity>> SERRIK_OF_SHADOW =
            ENTITY_TYPES.register("serrik_of_shadow", () -> EntityType.Builder
                    .<SerrikOfShadowEntity>of(SerrikOfShadowEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 1.2F)
                    .clientTrackingRange(10)
                    .build("serrik_of_shadow"));

    /** Lucius, Retainer of Ruin — post-Elemental Heroes boss, 2000 HP (1000 hearts). Iron Golem placeholder. */
    public static final DeferredHolder<EntityType<?>, EntityType<LuciusRetainerOfRuinEntity>> LUCIUS_RETAINER_OF_RUIN =
            ENTITY_TYPES.register("lucius_retainer_of_ruin", () -> EntityType.Builder
                    .<LuciusRetainerOfRuinEntity>of(LuciusRetainerOfRuinEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 1.2F)
                    .clientTrackingRange(12)
                    .build("lucius_retainer_of_ruin"));

    /** Drallius, Visions of Doom — post-Lucius boss, 5000 HP (2500 hearts). Iron Golem placeholder. */
    public static final DeferredHolder<EntityType<?>, EntityType<DralliusVisionsOfDoomEntity>> DRALLIUS_VISIONS_OF_DOOM =
            ENTITY_TYPES.register("drallius_visions_of_doom", () -> EntityType.Builder
                    .<DralliusVisionsOfDoomEntity>of(DralliusVisionsOfDoomEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 1.2F)
                    .clientTrackingRange(12)
                    .build("drallius_visions_of_doom"));

    /** Drallius, Chaos: UNLEASHED — final boss, 5000 HP (2500 hearts). Iron Golem placeholder. */
    public static final DeferredHolder<EntityType<?>, EntityType<DralliusChaosUnleashedEntity>> DRALLIUS_CHAOS_UNLEASHED =
            ENTITY_TYPES.register("drallius_chaos_unleashed", () -> EntityType.Builder
                    .<DralliusChaosUnleashedEntity>of(DralliusChaosUnleashedEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 1.2F)
                    .clientTrackingRange(12)
                    .build("drallius_chaos_unleashed"));
}
