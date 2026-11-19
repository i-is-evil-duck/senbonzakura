package com.j3ly.senbonzakura;

import com.j3ly.senbonzakura.entity.ModEntities;
import com.j3ly.senbonzakura.entity.boss.KingSlimeEntity;
import com.j3ly.senbonzakura.effect.CinderingEffect;
import com.j3ly.senbonzakura.item.*;
import com.j3ly.senbonzakura.item.boss.RoyalJellyItem;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

    // King Slime drops
    public static final DeferredHolder<Item, Item> CONDENSED_SLIME = ITEMS.register("condensed_slime", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> SLIME_BALL_SLINGSHOT = ITEMS.register("slime_ball_slingshot", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> SLIME_SHIELD = ITEMS.register("slime_shield", () -> new Item(new Item.Properties()));

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
            event.accept(CONDENSED_SLIME.get());
            event.accept(SLIME_BALL_SLINGSHOT.get());
            event.accept(SLIME_SHIELD.get());
        }
    }

    private void onEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.KING_SLIME.get(), KingSlimeEntity.createAttributes().build());
    }

    /** SimpleParticleType's constructor is protected, so expose a public subclass for registration. */
    public static class ModSimpleParticleType extends SimpleParticleType {
        public ModSimpleParticleType(boolean overrideLimiter) {
            super(overrideLimiter);
        }
    }
}
