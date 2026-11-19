package com.j3ly.senbonzakura;

import com.j3ly.senbonzakura.entity.ModEntities;
import com.j3ly.senbonzakura.entity.boss.ChaoticBossEntity;
import com.j3ly.senbonzakura.entity.boss.DralliusChaosUnleashedEntity;
import com.j3ly.senbonzakura.entity.boss.DralliusVisionsOfDoomEntity;
import com.j3ly.senbonzakura.entity.boss.FelioOfFlameEntity;
import com.j3ly.senbonzakura.entity.boss.FrozenSerpentEntity;
import com.j3ly.senbonzakura.entity.boss.HellSerpentEntity;
import com.j3ly.senbonzakura.entity.boss.IeirithOfIceEntity;
import com.j3ly.senbonzakura.entity.boss.IronWormEntity;
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
import com.j3ly.senbonzakura.entity.boss.SkySerpentEntity;
import com.j3ly.senbonzakura.entity.boss.TherimOfThunderEntity;
import com.j3ly.senbonzakura.entity.boss.WahikoOfWindEntity;
import com.j3ly.senbonzakura.entity.boss.WendigoWarlordEntity;
import com.j3ly.senbonzakura.entity.boss.WerewolfTribeChiefEntity;
import com.j3ly.senbonzakura.entity.boss.ZombieGeneralEntity;
import com.j3ly.senbonzakura.effect.CinderingEffect;
import com.j3ly.senbonzakura.item.*;
import com.j3ly.senbonzakura.item.boss.*;
import com.j3ly.senbonzakura.item.music.*;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
//? if neoforge {
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
//?}

import java.util.Random;
import java.util.Set;

//? if neoforge {
@Mod(SenbonzakuraMod.MOD_ID)
//?}
public class SenbonzakuraMod {
    public static final String MOD_ID = "senbonzakura";
    public static final Random random = new Random();

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, MOD_ID);
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, MOD_ID);

    // Particles
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CINDERING_PARTICLES =
            PARTICLE_TYPES.register("cindering_particles", () -> new ModSimpleParticleType(false));

    // Effects
    public static final DeferredHolder<MobEffect, CinderingEffect> CINDERING =
            MOB_EFFECTS.register("cindering", CinderingEffect::new);

    // Weapons
    public static final DeferredHolder<Item, SenbonzakuraItem> SENBONZAKURA = ITEMS.register("senbonzakura",
            () -> new SenbonzakuraItem(new Item.Properties().fireResistant()));
    public static final DeferredHolder<Item, CrescentBladeItem> CRESCENT_BLADE = ITEMS.register("crescent_blade",
            () -> new CrescentBladeItem(new Item.Properties().fireResistant()));
    public static final DeferredHolder<Item, CryptBladeItem> CRYPT_BLADE = ITEMS.register("crypt_blade",
            () -> new CryptBladeItem(new Item.Properties().fireResistant()));
    public static final DeferredHolder<Item, SawBladeAxeItem> SAW_BLADE_AXE = ITEMS.register("saw_blade_axe",
            () -> new SawBladeAxeItem(new Item.Properties()));
    public static final DeferredHolder<Item, RiteOfCommandmentsItem> RITE_OF_COMMANDMENTS = ITEMS.register("rite_of_commandments",
            () -> new RiteOfCommandmentsItem(new Item.Properties().fireResistant()));
    public static final DeferredHolder<Item, PyrerizerItem> PYRERIZER = ITEMS.register("pyrerizer",
            () -> new PyrerizerItem(new Item.Properties().fireResistant()));
    public static final DeferredHolder<Item, YukiNoAmeItem> YUKI_NO_AME = ITEMS.register("yuki_no_ame",
            () -> new YukiNoAmeItem(new Item.Properties().fireResistant()));
    public static final DeferredHolder<Item, RazorSharkItem> RAZOR_SHARK = ITEMS.register("razor_shark",
            () -> new RazorSharkItem(new Item.Properties().fireResistant()));
    public static final DeferredHolder<Item, SentenceItem> SENTENCE = ITEMS.register("sentence",
            () -> new SentenceItem(new Item.Properties().fireResistant()));
    public static final DeferredHolder<Item, WendigoItem> WENDIGO = ITEMS.register("wendigo",
            () -> new WendigoItem(new Item.Properties().fireResistant()));
    public static final DeferredHolder<Item, EmpireStaffItem> EMPIRE_STAFF = ITEMS.register("empire_staff",
            () -> new EmpireStaffItem(new Item.Properties().fireResistant()));
    public static final DeferredHolder<Item, GodRockItem> GOD_ROCK = ITEMS.register("god_rock",
            () -> new GodRockItem(new Item.Properties()));
    public static final DeferredHolder<Item, MatthiasCursebladeItem> MATTHIAS_CURSEBLADE = ITEMS.register("matthias_curseblade",
            () -> new MatthiasCursebladeItem(new Item.Properties().fireResistant()));
    public static final DeferredHolder<Item, DuckysWandItem> DUCKYS_WAND = ITEMS.register("duckys_wand",
            () -> new DuckysWandItem(new Item.Properties().fireResistant().stacksTo(1)));

    // Crafting Items
    public static final DeferredHolder<Item, Item> INKY_PEARL = ITEMS.register("inky_pearl", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> EXALTED_BLOSSOM = ITEMS.register("exalted_blossom", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> ANCIENT_AMBER = ITEMS.register("ancient_amber", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> DEEPWOKEN_RELIC = ITEMS.register("deepwoken_relic", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> EXCELLENT_PRINCESS_DIAMOND = ITEMS.register("excellent_princess_diamond", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> PERFECT_RADIANT_CUT_DIAMOND = ITEMS.register("perfect_radiant_cut_diamond", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> SACRED_THREADS = ITEMS.register("sacred_threads", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> CRUDE_JEWEL_STEEL = ITEMS.register("crude_jewel_steel", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> TAMAHAGANE = ITEMS.register("tamahagane", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> SUPREME_ALLOY = ITEMS.register("supreme_alloy", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> CINDERING_CORE = ITEMS.register("cindering_core", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> ULTRA_CONDUCTIVE_MATERIAL = ITEMS.register("ultra_conductive_material", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> SKYSTONE = ITEMS.register("skystone", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> DARKENED_RUBBLE = ITEMS.register("darkened_rubble", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> SINNERS_ASH = ITEMS.register("sinners_ash", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> FROZEN_CORE = ITEMS.register("frozen_core", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> DUCK_EGG = ITEMS.register("duck_egg", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> GOLD_DUCK_EGG = ITEMS.register("gold_duck_egg", () -> new Item(new Item.Properties()));

    // Internal projectile item (never shown in creative tabs)
    public static final DeferredHolder<Item, Item> DUCK_PROJECTILE = ITEMS.register("duck_projectile", () -> new Item(new Item.Properties()));

    // Coins
    public static final DeferredHolder<Item, Item> COPPER_COIN = ITEMS.register("copper_coin", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> GOLD_COIN = ITEMS.register("gold_coin", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> SHINY_COIN = ITEMS.register("shiny_coin", () -> new Item(new Item.Properties()));

    // Boss summon items
    public static final DeferredHolder<Item, Item> ROYAL_JELLY = ITEMS.register("royal_jelly", () -> new RoyalJellyItem(new Item.Properties()));
    public static final DeferredHolder<Item, Item> PUTRID_BAIT = ITEMS.register("putrid_bait", () -> new PutridBaitItem(new Item.Properties()));
    public static final DeferredHolder<Item, Item> CALCIUM = ITEMS.register("calcium", () -> new CalciumItem(new Item.Properties()));
    public static final DeferredHolder<Item, Item> SUSPICIOUS_BUG = ITEMS.register("suspicious_bug", () -> new SuspiciousBugItem(new Item.Properties()));
    public static final DeferredHolder<Item, Item> CORRODING_LURE = ITEMS.register("corroding_lure", () -> new BossSummonItem(
            (EntityType<? extends LivingEntity>) ModEntities.IRON_WORM.get(), true, Set.of(Level.OVERWORLD), new Item.Properties()) {});
    public static final DeferredHolder<Item, Item> LUNAR_TOTEM = ITEMS.register("lunar_totem", () -> new BossSummonItem(
            (EntityType<? extends LivingEntity>) ModEntities.WEREWOLF_TRIBE_CHIEF.get(), true, Set.of(Level.OVERWORLD), new Item.Properties()) {});
    public static final DeferredHolder<Item, Item> STORM_TOTEM = ITEMS.register("storm_totem", () -> new BossSummonItem(
            (EntityType<? extends LivingEntity>) ModEntities.SKY_SERPENT.get(), true, Set.of(Level.OVERWORLD), new Item.Properties()) {});
    public static final DeferredHolder<Item, Item> INFERNAL_TOTEM = ITEMS.register("infernal_totem", () -> new BossSummonItem(
            (EntityType<? extends LivingEntity>) ModEntities.HELL_SERPENT.get(), true, Set.of(Level.NETHER), new Item.Properties()) {});
    public static final DeferredHolder<Item, Item> FROST_TOTEM = ITEMS.register("frost_totem", () -> new BossSummonItem(
            (EntityType<? extends LivingEntity>) ModEntities.FROZEN_SERPENT.get(), true, Set.of(Level.OVERWORLD), new Item.Properties()) {});
    public static final DeferredHolder<Item, Item> DEEP_TOTEM = ITEMS.register("deep_totem", () -> new BossSummonItem(
            (EntityType<? extends LivingEntity>) ModEntities.KRAKEN_GUARDIAN.get(), true, Set.of(Level.OVERWORLD), new Item.Properties()) {});
    public static final DeferredHolder<Item, Item> END_TOTEM = ITEMS.register("end_totem", () -> new BossSummonItem(
            (EntityType<? extends LivingEntity>) ModEntities.WENDIGO_WARLORD.get(), true, Set.of(Level.END), new Item.Properties()) {});
    public static final DeferredHolder<Item, Item> SINFUL_TOTEM = ITEMS.register("sinful_totem", () -> new BossSummonItem(
            (EntityType<? extends LivingEntity>) ModEntities.SINFUL_SQUIRE.get(), true, Set.of(Level.OVERWORLD), new Item.Properties()) {});
    public static final DeferredHolder<Item, Item> BISHOP_TOTEM = ITEMS.register("bishop_totem", () -> new BossSummonItem(
            (EntityType<? extends LivingEntity>) ModEntities.SINFUL_BISHOP.get(), true, Set.of(Level.OVERWORLD), new Item.Properties()) {});
    public static final DeferredHolder<Item, Item> KNIGHT_TOTEM = ITEMS.register("knight_totem", () -> new BossSummonItem(
            (EntityType<? extends LivingEntity>) ModEntities.SINISTER_KNIGHT.get(), true, Set.of(Level.OVERWORLD), new Item.Properties()) {});
    public static final DeferredHolder<Item, Item> FLAME_TOTEM = ITEMS.register("flame_totem", () -> new BossSummonItem(
            (EntityType<? extends LivingEntity>) ModEntities.FELIO_OF_FLAME.get(), true, Set.of(Level.OVERWORLD), new Item.Properties()) {});
    public static final DeferredHolder<Item, Item> ICE_TOTEM = ITEMS.register("ice_totem", () -> new BossSummonItem(
            (EntityType<? extends LivingEntity>) ModEntities.IEIRITH_OF_ICE.get(), true, Set.of(Level.OVERWORLD), new Item.Properties()) {});
    public static final DeferredHolder<Item, Item> THUNDER_TOTEM = ITEMS.register("thunder_totem", () -> new BossSummonItem(
            (EntityType<? extends LivingEntity>) ModEntities.THERIM_OF_THUNDER.get(), true, Set.of(Level.OVERWORLD), new Item.Properties()) {});
    public static final DeferredHolder<Item, Item> WIND_TOTEM = ITEMS.register("wind_totem", () -> new BossSummonItem(
            (EntityType<? extends LivingEntity>) ModEntities.WAHIKO_OF_WIND.get(), true, Set.of(Level.OVERWORLD), new Item.Properties()) {});
    public static final DeferredHolder<Item, Item> CHERRY_TOTEM = ITEMS.register("cherry_totem", () -> new BossSummonItem(
            (EntityType<? extends LivingEntity>) ModEntities.SAKURAI_WAHIKO.get(), true, Set.of(Level.OVERWORLD), new Item.Properties()) {});
    public static final DeferredHolder<Item, Item> SHADOW_TOTEM = ITEMS.register("shadow_totem", () -> new BossSummonItem(
            (EntityType<? extends LivingEntity>) ModEntities.SERRIK_OF_SHADOW.get(), true, Set.of(Level.OVERWORLD), new Item.Properties()) {});
    public static final DeferredHolder<Item, Item> RUIN_TOTEM = ITEMS.register("ruin_totem", () -> new BossSummonItem(
            (EntityType<? extends LivingEntity>) ModEntities.LUCIUS_RETAINER_OF_RUIN.get(), true, Set.of(Level.OVERWORLD), new Item.Properties()) {});
    public static final DeferredHolder<Item, Item> VISIONS_TOTEM = ITEMS.register("visions_totem", () -> new BossSummonItem(
            (EntityType<? extends LivingEntity>) ModEntities.DRALLIUS_VISIONS_OF_DOOM.get(), true, Set.of(Level.OVERWORLD), new Item.Properties()) {});
    public static final DeferredHolder<Item, Item> CHAOS_TOTEM = ITEMS.register("chaos_totem", () -> new BossSummonItem(
            (EntityType<? extends LivingEntity>) ModEntities.DRALLIUS_CHAOS_UNLEASHED.get(), true, Set.of(Level.OVERWORLD), new Item.Properties()) {});

    // King Slime drops
    public static final DeferredHolder<Item, Item> CONDENSED_SLIME = ITEMS.register("condensed_slime", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> SLIME_BALL_SLINGSHOT = ITEMS.register("slime_ball_slingshot", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> SLIME_SHIELD = ITEMS.register("slime_shield", () -> new Item(new Item.Properties()));

    // Zombie / Skeleton General drops
    public static final DeferredHolder<Item, Item> FORGOTTEN_INSIGNIA = ITEMS.register("forgotten_insignia", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> UNDEAD_SWORD = ITEMS.register("undead_sword", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> UNDEAD_BOW = ITEMS.register("undead_bow", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> TOMB_CHESTPLATE = ITEMS.register("tomb_chestplate", () -> new Item(new Item.Properties()));

    // Queen Spider drops
    public static final DeferredHolder<Item, Item> QUEEN_SILK = ITEMS.register("queen_silk", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> SPIDER_NECKLACE = ITEMS.register("spider_necklace", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> WEBBER = ITEMS.register("webber", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> NOXIOUS_DAGGER = ITEMS.register("noxious_dagger", () -> new Item(new Item.Properties()));

    // Iron Worm drops
    public static final DeferredHolder<Item, Item> METEOR_IRON_SHARD = ITEMS.register("meteor_iron_shard", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> DRILL_SHAPED_TEETH = ITEMS.register("drill_shaped_teeth", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> IRON_SCALE = ITEMS.register("iron_scale", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> IRON_BONE_FRAGMENT = ITEMS.register("iron_bone_fragment", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> IRON_MANDIBLE_BLADE = ITEMS.register("iron_mandible_blade", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> IRON_SPINE_SPEAR = ITEMS.register("iron_spine_spear", () -> new Item(new Item.Properties()));

    // Werewolf Tribe Chief drops
    public static final DeferredHolder<Item, Item> WEREWOLF_FANG = ITEMS.register("werewolf_fang", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> MOON_CLAW = ITEMS.register("moon_claw", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> LUNAR_PELT = ITEMS.register("lunar_pelt", () -> new Item(new Item.Properties()));

    // Sky Serpent drops
    public static final DeferredHolder<Item, Item> SERPENT_FANG = ITEMS.register("serpent_fang", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> SKY_SCALE = ITEMS.register("sky_scale", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> CLOUD_ESS = ITEMS.register("cloud_essence", () -> new Item(new Item.Properties()));

    // Hell Serpent drops
    public static final DeferredHolder<Item, Item> HELL_SCALE = ITEMS.register("hell_scale", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> INFERNAL_CORE = ITEMS.register("infernal_core", () -> new Item(new Item.Properties()));

    // Frozen Serpent drops
    public static final DeferredHolder<Item, Item> FROST_FANG = ITEMS.register("frost_fang", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> EVERFROST_SHARD = ITEMS.register("everfrost_shard", () -> new Item(new Item.Properties()));

    // Kraken Guardian drops
    public static final DeferredHolder<Item, Item> KRAKEN_TENTACLE = ITEMS.register("kraken_tentacle", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> DEEP_PRISM = ITEMS.register("deep_prism", () -> new Item(new Item.Properties()));

    // Wendigo Warlord drops
    public static final DeferredHolder<Item, Item> WENDIGO_CLAW = ITEMS.register("wendigo_claw", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> SHADOW_HEART = ITEMS.register("shadow_heart", () -> new Item(new Item.Properties()));

    // Sinful Squire drops
    public static final DeferredHolder<Item, Item> SINFUL_BLADE = ITEMS.register("sinful_blade", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> PENITENT_SHIELD = ITEMS.register("penitent_shield", () -> new Item(new Item.Properties()));

    // Sinful Bishop drops
    public static final DeferredHolder<Item, Item> BISHOP_STAFF = ITEMS.register("bishop_staff", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> HOLY_SEAL = ITEMS.register("holy_seal", () -> new Item(new Item.Properties()));

    // Sinister Knight drops
    public static final DeferredHolder<Item, Item> KNIGHT_LANCE = ITEMS.register("knight_lance", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> DARK_PLATING = ITEMS.register("dark_plating", () -> new Item(new Item.Properties()));

    // Felio, of Flame drops
    public static final DeferredHolder<Item, Item> CURSED_FLAMES = ITEMS.register("cursed_flames", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> SCORCHED_EARTH = ITEMS.register("scorched_earth", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> INSIGNIA_OF_FIRE = ITEMS.register("insignia_of_fire", () -> new Item(new Item.Properties()));

    // Ieirith, of Ice drops
    public static final DeferredHolder<Item, Item> EVERFROSTED_GLASS = ITEMS.register("everfrosted_glass", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> GRAN_IEIRITH = ITEMS.register("gran_ieirith", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> INSIGNIA_OF_ICE = ITEMS.register("insignia_of_ice", () -> new Item(new Item.Properties()));

    // Therim, of Thunder drops
    public static final DeferredHolder<Item, Item> ROARING_SPARK = ITEMS.register("roaring_spark", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> THUNDERBIRD = ITEMS.register("thunderbird", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> INSIGNIA_OF_THUNDER = ITEMS.register("insignia_of_thunder", () -> new Item(new Item.Properties()));

    // Wahiko, of Wind drops
    public static final DeferredHolder<Item, Item> WIND_ESSENCE = ITEMS.register("wind_essence", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> CHERRY_PETAL = ITEMS.register("cherry_petal", () -> new Item(new Item.Properties()));

    // Sakurai Wahiko drops
    public static final DeferredHolder<Item, Item> SAKURAI_BLADE = ITEMS.register("sakurai_blade", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> BLOSSOM_CORE = ITEMS.register("blossom_core", () -> new Item(new Item.Properties()));

    // Serrik, of Shadow drops
    public static final DeferredHolder<Item, Item> UMBRAL_SPORES = ITEMS.register("umbral_spores", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> DEPTH_ASCENDER = ITEMS.register("depth_ascender", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> INSIGNIA_OF_SHADOW = ITEMS.register("insignia_of_shadow", () -> new Item(new Item.Properties()));

    // Lucius, Retainer of Ruin drops
    public static final DeferredHolder<Item, Item> RUIN_SHARD = ITEMS.register("ruin_shard", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> VOID_ESSENCE = ITEMS.register("void_essence", () -> new Item(new Item.Properties()));

    // Drallius drops
    public static final DeferredHolder<Item, Item> VISIONS_OF_CHAOS = ITEMS.register("visions_of_chaos", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> FINALITY = ITEMS.register("finality", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> CHAOTIC_CIRCLET = ITEMS.register("chaotic_circlet", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> CHAOTIC_MANTLE = ITEMS.register("chaotic_mantle", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> CHAOTIC_PLATING = ITEMS.register("chaotic_plating", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> CHAOTIC_KICKS = ITEMS.register("chaotic_kicks", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> THE_CREDITS = ITEMS.register("the_credits", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> FROZONE = ITEMS.register("frozone", () -> new Item(new Item.Properties()));

    // Music discs
    public static final DeferredHolder<Item, Item> MUSIC_DISC_SERVANTS = ITEMS.register("music_disc_servants", () -> new MusicDiscItem(new Item.Properties()));
    public static final DeferredHolder<Item, Item> MUSIC_DISC_UNIVERSE = ITEMS.register("music_disc_universe", () -> new MusicDiscItem(new Item.Properties()));

    public SenbonzakuraMod(IEventBus modEventBus) {
        ModGameRules.init();
        ITEMS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        PARTICLE_TYPES.register(modEventBus);
        MOB_EFFECTS.register(modEventBus);
        //? if neoforge {
        ModSounds.SOUND_EVENTS.register(modEventBus);
        //?}
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::onEntityAttributes);
        //? if neoforge {
        NeoForge.EVENT_BUS.addListener(this::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(this::onLevelTick);
        NeoForge.EVENT_BUS.addListener(this::onLivingDamage);
        NeoForge.EVENT_BUS.addListener(this::onLivingDeath);
        NeoForge.EVENT_BUS.addListener(this::onLivingDrops);
        NeoForge.EVENT_BUS.addListener(this::onBlockBreak);
        NeoForge.EVENT_BUS.addListener(this::onAttackEntity);
        NeoForge.EVENT_BUS.addListener(this::onServerTick);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
        //?}
    }

    public void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

    public void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        // Ensure tagged Wendigo arrows are cleaned up even if player is offline/dimension-changed
        WendigoItem.cleanupExpiredTagged(level, level.getGameTime());
    }

    public void onLivingDamage(LivingDamageEvent.Pre event) {
        // Cindering: fire damage is tripled while the effect is active.
        if (event.getEntity().hasEffect(CINDERING) && event.getSource().is(DamageTypeTags.IS_FIRE)) {
            event.setNewDamage(event.getNewDamage() * 3.0f);
        }

        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (!CrescentBladeItem.isDualWielding(player)) return;
        if (!(player.getMainHandItem().getItem() instanceof CrescentBladeItem)) return;
        if (CrescentBladeItem.isInAbilityDamageContext(player.getUUID())) return;
        // Double main Crescent Blade damage while dual-wielding two Crescent Blades
        event.setNewDamage(event.getNewDamage() * 2.0f);
    }

    public void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        ItemStack mainHand = player.getMainHandItem();
        ServerLevel level = (ServerLevel) player.level();

        WendigoItem.tickCleanup(level, player.getUUID(), level.getGameTime());
        WendigoItem.removeAbilityArrowsFromInventory(player);

        if (mainHand.getItem() instanceof SenbonzakuraItem) {
            spawnSakuraParticles(level, player);
        } else if (mainHand.getItem() instanceof YukiNoAmeItem) {
            spawnSnowflakeParticles(level, player);
        } else if (mainHand.getItem() instanceof CrescentBladeItem) {
            spawnGreenParticles(level, player);
        } else if (mainHand.getItem() instanceof CryptBladeItem) {
            spawnBlackParticles(level, player);
        } else if (mainHand.getItem() instanceof RiteOfCommandmentsItem) {
            spawnWhiteParticles(level, player);
        } else if (mainHand.getItem() instanceof PyrerizerItem) {
            spawnGreyParticles(level, player);
        } else if (mainHand.getItem() instanceof RazorSharkItem || mainHand.getItem() instanceof WendigoItem) {
            spawnBubbleParticles(level, player);
        } else if (mainHand.getItem() instanceof SentenceItem) {
            spawnRedSwordParticles(level, player);
        }

        if (mainHand.getItem() instanceof DuckysWandItem) {
            DuckysWandItem.tickSwing(player, level);
        }
    }

    public void onAttackEntity(AttackEntityEvent event) {
        // Ducky's Wand replaces melee swings with duck projectiles.
        if (event.getEntity().getMainHandItem().getItem() instanceof DuckysWandItem) {
            event.setCanceled(true);
        }
    }

    public void onServerTick(ServerTickEvent.Post event) {
        DuckyDomain.tick(event.getServer());
    }

    public void onLivingDeath(LivingDeathEvent event) {
        // Matthias' Curseblade: defeated mobs burst into a mob-only, block-safe blast.
        LivingEntity victim = event.getEntity();
        if (!(victim instanceof Mob)) return;
        if (!(victim.level() instanceof ServerLevel level)) return;

        Player killer = null;
        if (event.getSource().getEntity() instanceof Player p) {
            killer = p;
        } else if (victim.getKillCredit() instanceof Player p) {
            killer = p;
        }
        if (killer == null) return;
        if (!(killer.getMainHandItem().getItem() instanceof MatthiasCursebladeItem)) return;

        MatthiasCursebladeItem.onVictimDefeated(level, victim, killer);
    }

    public void onLivingDrops(LivingDropsEvent event) {
        Entity entity = event.getEntity();
        // Ducks spawned by Ducky's Wand die on impact without leaving loot.
        if (entity.getPersistentData().getBoolean(DuckCompat.NO_DROPS_KEY)) {
            event.getDrops().clear();
            return;
        }
        if (!(entity.level() instanceof ServerLevel level)) return;

        java.util.UUID killerId = null;
        if (event.getSource().getEntity() instanceof Player p) {
            killerId = p.getUUID();
        } else if (entity instanceof net.minecraft.world.entity.LivingEntity living && living.getKillCredit() instanceof Player p) {
            killerId = p.getUUID();
        }

        // Duck Egg - 1/100 when a chicken drowns
        if (entity instanceof net.minecraft.world.entity.animal.Chicken
                && event.getSource().is(net.minecraft.world.damagesource.DamageTypes.DROWN)) {
            if (ModDropData.rollDrop(level, killerId, 100)) {
                event.getDrops().add(new ItemEntity(
                        level, entity.getX(), entity.getY(), entity.getZ(),
                        new ItemStack(DUCK_EGG.get())));
            }
        }

        // Inky Pearls - 1/90 Elder Guardian, 1/900 Guardian/Drowned (craftable alternative)
        if (entity instanceof net.minecraft.world.entity.monster.ElderGuardian) {
            if (ModDropData.rollDrop(level, killerId, 90)) {
                event.getDrops().add(new ItemEntity(
                        level, entity.getX(), entity.getY(), entity.getZ(),
                        new ItemStack(INKY_PEARL.get()))
                );
            }
        } else if (entity instanceof net.minecraft.world.entity.monster.Guardian) {
            if (ModDropData.rollDrop(level, killerId, 900)) {
                event.getDrops().add(new ItemEntity(
                        level, entity.getX(), entity.getY(), entity.getZ(),
                        new ItemStack(INKY_PEARL.get()))
                );
            }
        } else if (entity instanceof net.minecraft.world.entity.monster.Drowned) {
            if (ModDropData.rollDrop(level, killerId, 900)) {
                event.getDrops().add(new ItemEntity(
                        level, entity.getX(), entity.getY(), entity.getZ(),
                        new ItemStack(INKY_PEARL.get()))
                );
            }
        }

        // Sigil of Doom (deepwoken_relic) - 1/1000 from Silverfish, 1/6000 from other hostile mobs
        if (entity instanceof net.minecraft.world.entity.monster.Silverfish) {
            if (ModDropData.rollDrop(level, killerId, 1000)) {
                event.getDrops().add(new ItemEntity(
                        level, entity.getX(), entity.getY(), entity.getZ(),
                        new ItemStack(DEEPWOKEN_RELIC.get()))
                );
            }
        } else if (entity instanceof net.minecraft.world.entity.monster.Monster) {
            if (ModDropData.rollDrop(level, killerId, 6000)) {
                event.getDrops().add(new ItemEntity(
                        level, entity.getX(), entity.getY(), entity.getZ(),
                        new ItemStack(DEEPWOKEN_RELIC.get()))
                );
            }
        }

        // Cindering Core - 1/300 from Ghast, Blaze
        if (entity instanceof net.minecraft.world.entity.monster.Blaze ||
            entity instanceof net.minecraft.world.entity.monster.Ghast) {
            if (ModDropData.rollDrop(level, killerId, 300)) {
                event.getDrops().add(new ItemEntity(
                        level, entity.getX(), entity.getY(), entity.getZ(),
                        new ItemStack(CINDERING_CORE.get()))
                );
            }
        }

        // Skystone - 1/300 from Phantoms, Breeze, Stray
        if (entity instanceof net.minecraft.world.entity.monster.Phantom ||
            entity instanceof net.minecraft.world.entity.monster.breeze.Breeze ||
            entity instanceof net.minecraft.world.entity.monster.Stray) {
            if (ModDropData.rollDrop(level, killerId, 300)) {
                event.getDrops().add(new ItemEntity(
                        level, entity.getX(), entity.getY(), entity.getZ(),
                        new ItemStack(SKYSTONE.get()))
                );
            }
        }

        // Sinners Ash - 1/30 from Ender Dragon, Wither, Warden
        if (entity instanceof net.minecraft.world.entity.boss.enderdragon.EnderDragon ||
            entity instanceof net.minecraft.world.entity.boss.wither.WitherBoss ||
            entity instanceof net.minecraft.world.entity.monster.warden.Warden) {
            if (ModDropData.rollDrop(level, killerId, 30)) {
                event.getDrops().add(new ItemEntity(
                        level, entity.getX(), entity.getY(), entity.getZ(),
                        new ItemStack(SINNERS_ASH.get()))
                );
            }
        }

        // Nether-only drops
        var dim = level.dimension();
        if (dim == net.minecraft.world.level.Level.NETHER) {
            // Darkened Rubble - 1/1500 from Wither Skeleton, Magma Cube, Blaze,
            // Piglin, Zombified Piglin and Ghast (craftable alternative)
            if (entity instanceof net.minecraft.world.entity.monster.WitherSkeleton ||
                entity instanceof net.minecraft.world.entity.monster.MagmaCube ||
                entity instanceof net.minecraft.world.entity.monster.Blaze ||
                entity instanceof net.minecraft.world.entity.monster.piglin.Piglin ||
                entity instanceof net.minecraft.world.entity.monster.ZombifiedPiglin ||
                entity instanceof net.minecraft.world.entity.monster.Ghast) {
                if (ModDropData.rollDrop(level, killerId, 1500)) {
                    event.getDrops().add(new ItemEntity(
                            level, entity.getX(), entity.getY(), entity.getZ(),
                            new ItemStack(DARKENED_RUBBLE.get()))
                    );
                }
            }
        }

        // Direct weapon drops (specific weapons)
        if (entity instanceof net.minecraft.world.entity.monster.ElderGuardian) {
            if (ModDropData.rollDrop(level, killerId, 35)) {
                event.getDrops().add(new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(RAZOR_SHARK.get())));
            }
        } else if (entity instanceof net.minecraft.world.entity.monster.Guardian) {
            if (ModDropData.rollDrop(level, killerId, 350)) {
                event.getDrops().add(new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(RAZOR_SHARK.get())));
            }
        }
        if (entity instanceof net.minecraft.world.entity.monster.Drowned) {
            if (ModDropData.rollDrop(level, killerId, 1250)) {
                event.getDrops().add(new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(RAZOR_SHARK.get())));
            }
        }

        if (entity instanceof net.minecraft.world.entity.monster.warden.Warden ||
            entity instanceof net.minecraft.world.entity.boss.wither.WitherBoss ||
            entity instanceof net.minecraft.world.entity.boss.enderdragon.EnderDragon) {
            if (ModDropData.rollDrop(level, killerId, 44)) {
                event.getDrops().add(new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(SENTENCE.get())));
            }
        }

        boolean normalSkeleton = entity instanceof net.minecraft.world.entity.monster.Skeleton
                && !(entity instanceof net.minecraft.world.entity.monster.Stray)
                && !(entity instanceof net.minecraft.world.entity.monster.WitherSkeleton);
        if (normalSkeleton || entity instanceof net.minecraft.world.entity.monster.Stray || entity instanceof net.minecraft.world.entity.monster.WitherSkeleton) {
            if (ModDropData.rollDrop(level, killerId, 1250)) {
                event.getDrops().add(new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(WENDIGO.get())));
            }
        }

        // Wendigo - 1/1500 from Silverfish
        if (entity instanceof net.minecraft.world.entity.monster.Silverfish) {
            if (ModDropData.rollDrop(level, killerId, 1500)) {
                event.getDrops().add(new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(WENDIGO.get())));
            }
        }
    }

    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        var state = event.getState();
        var block = state.getBlock();
        var player = event.getPlayer();
        java.util.UUID playerId = player != null ? player.getUUID() : null;

        // Ancient Amber - 1/900 when breaking/stripping logs with an axe
        if (block.toString().contains("stripped") || block.toString().contains("log")) {
            if (player != null && player.getMainHandItem().getItem() instanceof net.minecraft.world.item.AxeItem) {
                if (ModDropData.rollDrop(level, playerId, 900)) {
                    level.addFreshEntity(new ItemEntity(
                            level, event.getPos().getX() + 0.5, event.getPos().getY() + 0.5, event.getPos().getZ() + 0.5,
                            new ItemStack(ANCIENT_AMBER.get()))
                    );
                }
            }
        }

        // Exalted Blossom - 1/7500 from cherry blossom leaves
        if (block.toString().contains("cherry_leaves")) {
            if (ModDropData.rollDrop(level, playerId, 7500)) {
                level.addFreshEntity(new ItemEntity(
                        level, event.getPos().getX() + 0.5, event.getPos().getY() + 0.5, event.getPos().getZ() + 0.5,
                        new ItemStack(EXALTED_BLOSSOM.get()))
                );
            }
        }
    }

    private void spawnSakuraParticles(ServerLevel level, Player player) {
        if (random.nextInt(3) != 0) return;
        Vec3 pos = player.position();
        double px = pos.x + (random.nextDouble() - 0.5) * 1.5;
        double py = pos.y + 0.5 + random.nextDouble() * 1.5;
        double pz = pos.z + (random.nextDouble() - 0.5) * 1.5;
        level.sendParticles(ParticleTypes.CHERRY_LEAVES, px, py, pz, 1, 0.02, 0.05, 0.02, 0.01);
    }

    private void spawnSnowflakeParticles(ServerLevel level, Player player) {
        if (random.nextInt(3) != 0) return;
        Vec3 pos = player.position();
        double px = pos.x + (random.nextDouble() - 0.5) * 1.5;
        double py = pos.y + 0.5 + random.nextDouble() * 1.5;
        double pz = pos.z + (random.nextDouble() - 0.5) * 1.5;
        level.sendParticles(ParticleTypes.SNOWFLAKE, px, py, pz, 1, 0.02, 0.05, 0.02, 0.01);
    }

    private void spawnGreenParticles(ServerLevel level, Player player) {
        if (random.nextInt(2) != 0) return;
        Vec3 pos = player.position();
        double px = pos.x + (random.nextDouble() - 0.5) * 1.2;
        double py = pos.y + 0.3 + random.nextDouble() * 1.2;
        double pz = pos.z + (random.nextDouble() - 0.5) * 1.2;
        level.sendParticles(ParticleTypes.ENCHANT, px, py, pz, 1, 0.01, 0.05, 0.01, 0.02);
    }

    private void spawnBlackParticles(ServerLevel level, Player player) {
        Vec3 pos = player.position();

        if (random.nextInt(2) == 0) {
            double angle = random.nextDouble() * Math.PI * 2;
            double radius = 0.8 + random.nextDouble() * 0.5;
            double px = pos.x + Math.cos(angle) * radius;
            double py = pos.y + 0.5 + random.nextDouble() * 1.0;
            double pz = pos.z + Math.sin(angle) * radius;
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, px, py, pz, 1, 0.02, 0.03, 0.02, 0.01);
        }

        if (random.nextInt(4) == 0) {
            double px = pos.x + (random.nextDouble() - 0.5) * 1.0;
            double py = pos.y + random.nextDouble() * 0.5;
            double pz = pos.z + (random.nextDouble() - 0.5) * 1.0;
            level.sendParticles(ParticleTypes.SMOKE, px, py, pz, 1, 0.03, 0.05, 0.03, 0.02);
        }
    }

    private void spawnWhiteParticles(ServerLevel level, Player player) {
        if (random.nextInt(2) != 0) return;
        Vec3 pos = player.position();
        double px = pos.x + (random.nextDouble() - 0.5) * 1.0;
        double py = pos.y + 0.5 + random.nextDouble() * 1.0;
        double pz = pos.z + (random.nextDouble() - 0.5) * 1.0;
        level.sendParticles(ParticleTypes.WHITE_ASH, px, py, pz, 1, 0.01, 0.05, 0.01, 0.02);
    }

    private void spawnGreyParticles(ServerLevel level, Player player) {
        Vec3 pos = player.position();
        if (random.nextInt(2) == 0) {
            double angle = random.nextDouble() * Math.PI * 2;
            double radius = 0.5 + random.nextDouble() * 0.5;
            double px = pos.x + Math.cos(angle) * radius;
            double py = pos.y + 0.3 + random.nextDouble() * 0.8;
            double pz = pos.z + Math.sin(angle) * radius;
            level.sendParticles(ParticleTypes.FLAME, px, py, pz, 1, 0.02, 0.04, 0.02, 0.01);
        }
    }

    private void spawnRedParticles(ServerLevel level, Player player) {
        if (random.nextInt(2) != 0) return;
        Vec3 pos = player.position();
        double px = pos.x + (random.nextDouble() - 0.5) * 1.5;
        double py = pos.y + 0.5 + random.nextDouble() * 1.5;
        double pz = pos.z + (random.nextDouble() - 0.5) * 1.5;
        level.sendParticles(ParticleTypes.EXPLOSION, px, py, pz, 1, 0.02, 0.05, 0.02, 0.01);
    }

    private void spawnBubbleParticles(ServerLevel level, Player player) {
        if (random.nextInt(2) != 0) return;
        Vec3 pos = player.position();
        double px = pos.x + (random.nextDouble() - 0.5) * 1.2;
        double py = pos.y + 0.4 + random.nextDouble() * 1.2;
        double pz = pos.z + (random.nextDouble() - 0.5) * 1.2;
        level.sendParticles(ParticleTypes.BUBBLE, px, py, pz, 1, 0.02, 0.05, 0.02, 0.01);
    }

    private void spawnRedSwordParticles(ServerLevel level, Player player) {
        if (random.nextInt(2) != 0) return;
        Vec3 pos = player.position();
        double px = pos.x + (random.nextDouble() - 0.5) * 1.2;
        double py = pos.y + 0.4 + random.nextDouble() * 1.2;
        double pz = pos.z + (random.nextDouble() - 0.5) * 1.2;
        level.sendParticles(ParticleTypes.DAMAGE_INDICATOR, px, py, pz, 1, 0.02, 0.05, 0.02, 0.01);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(SENBONZAKURA.get());
            event.accept(CRESCENT_BLADE.get());
            event.accept(CRYPT_BLADE.get());
            event.accept(SAW_BLADE_AXE.get());
            event.accept(RITE_OF_COMMANDMENTS.get());
            event.accept(PYRERIZER.get());
            event.accept(YUKI_NO_AME.get());
            event.accept(RAZOR_SHARK.get());
            event.accept(SENTENCE.get());
            event.accept(WENDIGO.get());
            event.accept(EMPIRE_STAFF.get());
            event.accept(GOD_ROCK.get());
            event.accept(MATTHIAS_CURSEBLADE.get());
            event.accept(DUCKYS_WAND.get());
        }
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(INKY_PEARL.get());
            event.accept(EXALTED_BLOSSOM.get());
            event.accept(ANCIENT_AMBER.get());
            event.accept(DEEPWOKEN_RELIC.get());
            event.accept(EXCELLENT_PRINCESS_DIAMOND.get());
            event.accept(PERFECT_RADIANT_CUT_DIAMOND.get());
            event.accept(SACRED_THREADS.get());
            event.accept(CRUDE_JEWEL_STEEL.get());
            event.accept(TAMAHAGANE.get());
            event.accept(SUPREME_ALLOY.get());
            event.accept(CINDERING_CORE.get());
            event.accept(ULTRA_CONDUCTIVE_MATERIAL.get());
            event.accept(SKYSTONE.get());
            event.accept(DARKENED_RUBBLE.get());
            event.accept(SINNERS_ASH.get());
            event.accept(FROZEN_CORE.get());
            event.accept(DUCK_EGG.get());
            event.accept(GOLD_DUCK_EGG.get());
            event.accept(COPPER_COIN.get());
            event.accept(GOLD_COIN.get());
            event.accept(SHINY_COIN.get());
            event.accept(ROYAL_JELLY.get());
            event.accept(PUTRID_BAIT.get());
            event.accept(CALCIUM.get());
            event.accept(SUSPICIOUS_BUG.get());
            event.accept(CONDENSED_SLIME.get());
            event.accept(SLIME_BALL_SLINGSHOT.get());
            event.accept(SLIME_SHIELD.get());
            event.accept(FORGOTTEN_INSIGNIA.get());
            event.accept(UNDEAD_SWORD.get());
            event.accept(UNDEAD_BOW.get());
            event.accept(TOMB_CHESTPLATE.get());
            event.accept(QUEEN_SILK.get());
            event.accept(SPIDER_NECKLACE.get());
            event.accept(WEBBER.get());
            event.accept(NOXIOUS_DAGGER.get());
            event.accept(CORRODING_LURE.get());
            event.accept(LUNAR_TOTEM.get());
            event.accept(STORM_TOTEM.get());
            event.accept(INFERNAL_TOTEM.get());
            event.accept(FROST_TOTEM.get());
            event.accept(DEEP_TOTEM.get());
            event.accept(END_TOTEM.get());
            event.accept(SINFUL_TOTEM.get());
            event.accept(BISHOP_TOTEM.get());
            event.accept(KNIGHT_TOTEM.get());
            event.accept(FLAME_TOTEM.get());
            event.accept(ICE_TOTEM.get());
            event.accept(THUNDER_TOTEM.get());
            event.accept(WIND_TOTEM.get());
            event.accept(CHERRY_TOTEM.get());
            event.accept(SHADOW_TOTEM.get());
            event.accept(RUIN_TOTEM.get());
            event.accept(VISIONS_TOTEM.get());
            event.accept(CHAOS_TOTEM.get());
            event.accept(METEOR_IRON_SHARD.get());
            event.accept(DRILL_SHAPED_TEETH.get());
            event.accept(IRON_SCALE.get());
            event.accept(IRON_BONE_FRAGMENT.get());
            event.accept(IRON_MANDIBLE_BLADE.get());
            event.accept(IRON_SPINE_SPEAR.get());
            event.accept(WEREWOLF_FANG.get());
            event.accept(MOON_CLAW.get());
            event.accept(LUNAR_PELT.get());
            event.accept(SERPENT_FANG.get());
            event.accept(SKY_SCALE.get());
            event.accept(CLOUD_ESS.get());
            event.accept(HELL_SCALE.get());
            event.accept(INFERNAL_CORE.get());
            event.accept(FROST_FANG.get());
            event.accept(EVERFROST_SHARD.get());
            event.accept(KRAKEN_TENTACLE.get());
            event.accept(DEEP_PRISM.get());
            event.accept(WENDIGO_CLAW.get());
            event.accept(SHADOW_HEART.get());
            event.accept(SINFUL_BLADE.get());
            event.accept(PENITENT_SHIELD.get());
            event.accept(BISHOP_STAFF.get());
            event.accept(HOLY_SEAL.get());
            event.accept(KNIGHT_LANCE.get());
            event.accept(DARK_PLATING.get());
            event.accept(CURSED_FLAMES.get());
            event.accept(SCORCHED_EARTH.get());
            event.accept(INSIGNIA_OF_FIRE.get());
            event.accept(EVERFROSTED_GLASS.get());
            event.accept(GRAN_IEIRITH.get());
            event.accept(INSIGNIA_OF_ICE.get());
            event.accept(ROARING_SPARK.get());
            event.accept(THUNDERBIRD.get());
            event.accept(INSIGNIA_OF_THUNDER.get());
            event.accept(WIND_ESSENCE.get());
            event.accept(CHERRY_PETAL.get());
            event.accept(SAKURAI_BLADE.get());
            event.accept(BLOSSOM_CORE.get());
            event.accept(UMBRAL_SPORES.get());
            event.accept(DEPTH_ASCENDER.get());
            event.accept(INSIGNIA_OF_SHADOW.get());
            event.accept(RUIN_SHARD.get());
            event.accept(VOID_ESSENCE.get());
            event.accept(VISIONS_OF_CHAOS.get());
            event.accept(FINALITY.get());
            event.accept(CHAOTIC_CIRCLET.get());
            event.accept(CHAOTIC_MANTLE.get());
            event.accept(CHAOTIC_PLATING.get());
            event.accept(CHAOTIC_KICKS.get());
            event.accept(THE_CREDITS.get());
            event.accept(FROZONE.get());
            event.accept(MUSIC_DISC_SERVANTS.get());
            event.accept(MUSIC_DISC_UNIVERSE.get());
            event.accept(WEREWOLF_FANG.get());
            event.accept(MOON_CLAW.get());
            event.accept(LUNAR_PELT.get());
            event.accept(SERPENT_FANG.get());
            event.accept(SKY_SCALE.get());
            event.accept(CLOUD_ESS.get());
            event.accept(MUSIC_DISC_SERVANTS.get());
            event.accept(MUSIC_DISC_UNIVERSE.get());
            event.accept(ROYAL_JELLY.get());
            event.accept(PUTRID_BAIT.get());
            event.accept(CALCIUM.get());
            event.accept(SUSPICIOUS_BUG.get());
            event.accept(CONDENSED_SLIME.get());
            event.accept(SLIME_BALL_SLINGSHOT.get());
            event.accept(SLIME_SHIELD.get());
        }
    }

    private void onEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.KING_SLIME.get(), KingSlimeEntity.createAttributes().build());
        event.put(ModEntities.ZOMBIE_GENERAL.get(), ZombieGeneralEntity.createAttributes().build());
        event.put(ModEntities.SKELETON_GENERAL.get(), SkeletonGeneralEntity.createAttributes().build());
        event.put(ModEntities.QUEEN_SPIDER.get(), QueenSpiderEntity.createAttributes().build());
        event.put(ModEntities.IRON_WORM.get(), IronWormEntity.createAttributes().build());
        event.put(ModEntities.WEREWOLF_TRIBE_CHIEF.get(), WerewolfTribeChiefEntity.createAttributes().build());
        event.put(ModEntities.SKY_SERPENT.get(), SkySerpentEntity.createAttributes().build());
        event.put(ModEntities.HELL_SERPENT.get(), HellSerpentEntity.createAttributes().build());
        event.put(ModEntities.FROZEN_SERPENT.get(), FrozenSerpentEntity.createAttributes().build());
        event.put(ModEntities.KRAKEN_GUARDIAN.get(), KrakenGuardianEntity.createAttributes().build());
        event.put(ModEntities.WENDIGO_WARLORD.get(), WendigoWarlordEntity.createAttributes().build());
        event.put(ModEntities.SINFUL_SQUIRE.get(), SinfulSquireEntity.createAttributes().build());
        event.put(ModEntities.SINFUL_BISHOP.get(), SinfulBishopEntity.createAttributes().build());
        event.put(ModEntities.SINISTER_KNIGHT.get(), SinisterKnightEntity.createAttributes().build());
        event.put(ModEntities.FELIO_OF_FLAME.get(), FelioOfFlameEntity.createAttributes().build());
        event.put(ModEntities.IEIRITH_OF_ICE.get(), IeirithOfIceEntity.createAttributes().build());
        event.put(ModEntities.THERIM_OF_THUNDER.get(), TherimOfThunderEntity.createAttributes().build());
        event.put(ModEntities.WAHIKO_OF_WIND.get(), WahikoOfWindEntity.createAttributes().build());
        event.put(ModEntities.SAKURAI_WAHIKO.get(), SakuraiWahikoEntity.createAttributes().build());
        event.put(ModEntities.SERRIK_OF_SHADOW.get(), SerrikOfShadowEntity.createAttributes().build());
        event.put(ModEntities.LUCIUS_RETAINER_OF_RUIN.get(), LuciusRetainerOfRuinEntity.createAttributes().build());
        event.put(ModEntities.DRALLIUS_VISIONS_OF_DOOM.get(), DralliusVisionsOfDoomEntity.createAttributes().build());
        event.put(ModEntities.DRALLIUS_CHAOS_UNLEASHED.get(), DralliusChaosUnleashedEntity.createAttributes().build());
    }

    /** SimpleParticleType's constructor is protected, so expose a public subclass for registration. */
    public static class ModSimpleParticleType extends SimpleParticleType {
        public ModSimpleParticleType(boolean overrideLimiter) {
            super(overrideLimiter);
        }
    }
}
