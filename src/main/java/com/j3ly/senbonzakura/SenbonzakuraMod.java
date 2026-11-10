package com.j3ly.senbonzakura;

import com.j3ly.senbonzakura.item.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Random;

@Mod(SenbonzakuraMod.MOD_ID)
public class SenbonzakuraMod {
    public static final String MOD_ID = "senbonzakura";
    public static final Random random = new Random();

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

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
    public static final DeferredHolder<Item, GodRockItem> GOD_ROCK = ITEMS.register("god_rock",
            () -> new GodRockItem(new Item.Properties()));

    // Crafting Items
    public static final DeferredHolder<Item, Item> INKY_PEARL = ITEMS.register("inky_pearl", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> EXALTED_BLOSSOM = ITEMS.register("exalted_blossom", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> ANCIENT_AMBER = ITEMS.register("ancient_amber", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> DEEPWOKEN_RELIC = ITEMS.register("deepwoken_relic", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> EXCELLENT_PRINCESS_DIAMOND = ITEMS.register("excellent_princess_diamond", () -> new Item(new Item.Properties()));
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

    // Coins
    public static final DeferredHolder<Item, Item> COPPER_COIN = ITEMS.register("copper_coin", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> GOLD_COIN = ITEMS.register("gold_coin", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> SHINY_COIN = ITEMS.register("shiny_coin", () -> new Item(new Item.Properties()));

    public SenbonzakuraMod(IEventBus modEventBus) {
        ModGameRules.init();
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::addCreative);
        NeoForge.EVENT_BUS.addListener(this::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(this::onLivingDeath);
        NeoForge.EVENT_BUS.addListener(this::onLivingDrops);
        NeoForge.EVENT_BUS.addListener(this::onBlockBreak);
    }

    public void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        ItemStack mainHand = player.getMainHandItem();
        ServerLevel level = (ServerLevel) player.level();

        if (mainHand.getItem() instanceof SenbonzakuraItem || mainHand.getItem() instanceof YukiNoAmeItem) {
            spawnSakuraParticles(level, player);
        } else if (mainHand.getItem() instanceof CrescentBladeItem) {
            spawnGreenParticles(level, player);
        } else if (mainHand.getItem() instanceof CryptBladeItem) {
            spawnBlackParticles(level, player);
        } else if (mainHand.getItem() instanceof RiteOfCommandmentsItem) {
            spawnWhiteParticles(level, player);
        } else if (mainHand.getItem() instanceof PyrerizerItem) {
            spawnGreyParticles(level, player);
        }
    }

    public void onLivingDeath(LivingDeathEvent event) {
        // Skystone drops from Phantoms/Breeze are handled in onLivingDrops.
    }

    public void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        Entity entity = event.getEntity();
        var level = entity.level();

        // Inky Pearls - 1/90 Elder Guardian, 1/900 Guardian/Drowned (craftable alternative)
        if (entity instanceof net.minecraft.world.entity.monster.ElderGuardian) {
            if (random.nextInt(90) == 0) {
                event.getDrops().add(new ItemEntity(
                        level, entity.getX(), entity.getY(), entity.getZ(),
                        new ItemStack(INKY_PEARL.get()))
                );
            }
        } else if (entity instanceof net.minecraft.world.entity.monster.Guardian) {
            if (random.nextInt(900) == 0) {
                event.getDrops().add(new ItemEntity(
                        level, entity.getX(), entity.getY(), entity.getZ(),
                        new ItemStack(INKY_PEARL.get()))
                );
            }
        } else if (entity instanceof net.minecraft.world.entity.monster.Drowned) {
            if (random.nextInt(900) == 0) {
                event.getDrops().add(new ItemEntity(
                        level, entity.getX(), entity.getY(), entity.getZ(),
                        new ItemStack(INKY_PEARL.get()))
                );
            }
        }

        // Deepwoken Relic - 1/6000 from Zombies, Skeletons, Creepers, Spiders, Endermen
        if (entity instanceof net.minecraft.world.entity.monster.Zombie ||
            entity instanceof net.minecraft.world.entity.monster.AbstractSkeleton ||
            entity instanceof net.minecraft.world.entity.monster.Creeper ||
            entity instanceof net.minecraft.world.entity.monster.Spider ||
            entity instanceof net.minecraft.world.entity.monster.EnderMan) {
            if (random.nextInt(6000) == 0) {
                event.getDrops().add(new ItemEntity(
                        level, entity.getX(), entity.getY(), entity.getZ(),
                        new ItemStack(DEEPWOKEN_RELIC.get()))
                );
            }
        }

        // Cindering Core - 1/300 from Ghast, Blaze
        if (entity instanceof net.minecraft.world.entity.monster.Blaze ||
            entity instanceof net.minecraft.world.entity.monster.Ghast) {
            if (random.nextInt(300) == 0) {
                event.getDrops().add(new ItemEntity(
                        level, entity.getX(), entity.getY(), entity.getZ(),
                        new ItemStack(CINDERING_CORE.get()))
                );
            }
        }

        // Skystone - 1/300 from Phantoms, Breeze
        if (entity instanceof net.minecraft.world.entity.monster.Phantom ||
            entity instanceof net.minecraft.world.entity.monster.breeze.Breeze) {
            if (random.nextInt(300) == 0) {
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
            if (random.nextInt(30) == 0) {
                event.getDrops().add(new ItemEntity(
                        level, entity.getX(), entity.getY(), entity.getZ(),
                        new ItemStack(SINNERS_ASH.get()))
                );
            }
        }

        // Nether-only drops
        var dim = level.dimension();
        if (dim == net.minecraft.world.level.Level.NETHER) {
            // Darkened Rubble - 1/450 from Zombified Piglin (craftable alternative)
            if (entity instanceof net.minecraft.world.entity.monster.ZombifiedPiglin) {
                if (random.nextInt(450) == 0) {
                    event.getDrops().add(new ItemEntity(
                            level, entity.getX(), entity.getY(), entity.getZ(),
                            new ItemStack(DARKENED_RUBBLE.get()))
                    );
                }
            }
        }
    }

    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getLevel() instanceof net.minecraft.world.level.Level level)) return;
        var state = event.getState();
        var block = state.getBlock();
        var player = event.getPlayer();

        // Ancient Amber - 1/900 when breaking/stripping logs with an axe
        if (block.toString().contains("stripped") || block.toString().contains("log")) {
            if (player != null && player.getMainHandItem().getItem() instanceof net.minecraft.world.item.AxeItem) {
                if (random.nextInt(900) == 0) {
                    level.addFreshEntity(new ItemEntity(
                            level, event.getPos().getX() + 0.5, event.getPos().getY() + 0.5, event.getPos().getZ() + 0.5,
                            new ItemStack(ANCIENT_AMBER.get()))
                    );
                }
            }
        }

        // Exalted Blossom - 1/7500 from cherry blossom leaves
        if (block.toString().contains("cherry_leaves")) {
            if (random.nextInt(7500) == 0) {
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

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(SENBONZAKURA.get());
            event.accept(CRESCENT_BLADE.get());
            event.accept(CRYPT_BLADE.get());
            event.accept(SAW_BLADE_AXE.get());
            event.accept(RITE_OF_COMMANDMENTS.get());
            event.accept(PYRERIZER.get());
            event.accept(YUKI_NO_AME.get());
            event.accept(GOD_ROCK.get());
        }
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(INKY_PEARL.get());
            event.accept(EXALTED_BLOSSOM.get());
            event.accept(ANCIENT_AMBER.get());
            event.accept(DEEPWOKEN_RELIC.get());
            event.accept(EXCELLENT_PRINCESS_DIAMOND.get());
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
            event.accept(COPPER_COIN.get());
            event.accept(GOLD_COIN.get());
            event.accept(SHINY_COIN.get());
        }
    }
}
