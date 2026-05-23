package com.senbonzakura;

import com.senbonzakura.item.*;
import com.senbonzakura.network.DashPacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Random;

@Mod(SenbonzakuraMod.MOD_ID)
public class SenbonzakuraMod {
    public static final String MOD_ID = "senbonzakura";
    public static final Random random = new Random();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    // Weapons
    public static final RegistryObject<Item> SENBONZAKURA = ITEMS.register("senbonzakura",
            () -> new SenbonzakuraItem(new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> CRESCENT_BLADE = ITEMS.register("crescent_blade",
            () -> new CrescentBladeItem(new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> CRYPT_BLADE = ITEMS.register("crypt_blade",
            () -> new CryptBladeItem(new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> SAW_BLADE_AXE = ITEMS.register("saw_blade_axe",
            () -> new SawBladeAxeItem(new Item.Properties()));
    public static final RegistryObject<Item> RITE_OF_COMMANDMENTS = ITEMS.register("rite_of_commandments",
            () -> new RiteOfCommandmentsItem(new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> PYRERIZER = ITEMS.register("pyrerizer",
            () -> new PyrerizerItem(new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> YUKI_NO_AME = ITEMS.register("yuki_no_ame",
            () -> new YukiNoAmeItem(new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> THE_FINALE = ITEMS.register("the_finale",
            () -> new TheFinaleItem(new Item.Properties().fireResistant()));

    // Crafting Items
    public static final RegistryObject<Item> ARCHAIC_CROWN = ITEMS.register("archaic_crown", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> INKY_PEARL = ITEMS.register("inky_pearl", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EXALTED_BLOSSOM = ITEMS.register("exalted_blossom", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ANCIENT_AMBER = ITEMS.register("ancient_amber", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DEEPWOKEN_RELIC = ITEMS.register("deepwoken_relic", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EXCELLENT_PRINCESS_DIAMOND = ITEMS.register("excellent_princess_diamond", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SACRED_THREADS = ITEMS.register("sacred_threads", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CRUDE_JEWEL_STEEL = ITEMS.register("crude_jewel_steel", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TAMAHAGANE = ITEMS.register("tamahagane", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SUPREME_ALLOY = ITEMS.register("supreme_alloy", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CINDERING_CORE = ITEMS.register("cindering_core", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ULTRA_CONDUCTIVE_MATERIAL = ITEMS.register("ultra_conductive_material", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SKYSTONE = ITEMS.register("skystone", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DARKENED_RUBBLE = ITEMS.register("darkened_rubble", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SINNERS_ASH = ITEMS.register("sinners_ash", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FROZEN_CORE = ITEMS.register("frozen_core", () -> new Item(new Item.Properties()));

    public SenbonzakuraMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide()) return;

        Player player = event.player;
        ItemStack mainHand = player.getMainHandItem();

        if (mainHand.getItem() instanceof SenbonzakuraItem || mainHand.getItem() instanceof YukiNoAmeItem) {
            spawnSakuraParticles((ServerLevel) player.level(), player);
        } else if (mainHand.getItem() instanceof CrescentBladeItem) {
            spawnGreenParticles((ServerLevel) player.level(), player);
        } else if (mainHand.getItem() instanceof CryptBladeItem) {
            spawnBlackParticles((ServerLevel) player.level(), player);
        } else if (mainHand.getItem() instanceof RiteOfCommandmentsItem) {
            spawnWhiteParticles((ServerLevel) player.level(), player);
        } else if (mainHand.getItem() instanceof PyrerizerItem) {
            spawnGreyParticles((ServerLevel) player.level(), player);
        } else if (mainHand.getItem() instanceof TheFinaleItem) {
            spawnRedParticles((ServerLevel) player.level(), player);
        }
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        net.minecraft.world.damagesource.DamageSource source = event.getSource();
        if (source == null) return;

        String sourceName = source.getMsgId();
        if (sourceName != null && sourceName.contains("lightning") && random.nextInt(100) == 0) {
            net.minecraft.world.level.Level level = event.getEntity().level();
            level.addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(
                    level, event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(),
                    new ItemStack(SKYSTONE.get()))
            );
        }
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        net.minecraft.world.entity.Entity entity = event.getEntity();
        net.minecraft.world.level.Level level = entity.level();

        // Archaic Crowns - 1/5 from Ender Dragon, Wither
        if (entity instanceof net.minecraft.world.entity.boss.enderdragon.EnderDragon ||
            entity instanceof net.minecraft.world.entity.boss.wither.WitherBoss) {
            if (random.nextInt(5) == 0) {
                event.getDrops().add(new net.minecraft.world.entity.item.ItemEntity(
                        level, entity.getX(), entity.getY(), entity.getZ(),
                        new ItemStack(ARCHAIC_CROWN.get()))
                );
            }
        }

        // Inky Pearls - 1/30 Elder Guardian, 1/300 Guardian/Drowned
        if (entity instanceof net.minecraft.world.entity.monster.Guardian) {
            String entityName = net.minecraft.world.entity.EntityType.getKey(entity.getType()).toString();
            boolean isElder = entityName.contains("elder");
            if (isElder) {
                if (random.nextInt(30) == 0) {
                    event.getDrops().add(new net.minecraft.world.entity.item.ItemEntity(
                            level, entity.getX(), entity.getY(), entity.getZ(),
                            new ItemStack(INKY_PEARL.get()))
                    );
                }
            } else {
                if (random.nextInt(300) == 0) {
                    event.getDrops().add(new net.minecraft.world.entity.item.ItemEntity(
                            level, entity.getX(), entity.getY(), entity.getZ(),
                            new ItemStack(INKY_PEARL.get()))
                    );
                }
            }
        } else if (entity instanceof net.minecraft.world.entity.monster.Drowned) {
            if (random.nextInt(300) == 0) {
                event.getDrops().add(new net.minecraft.world.entity.item.ItemEntity(
                        level, entity.getX(), entity.getY(), entity.getZ(),
                        new ItemStack(INKY_PEARL.get()))
                );
            }
        }

        // Deepwoken Relic - 1/2500 from Zombies, Skeletons, Creepers, Spiders, Endermen
        if (entity instanceof net.minecraft.world.entity.monster.Zombie ||
            entity instanceof net.minecraft.world.entity.monster.Skeleton ||
            entity instanceof net.minecraft.world.entity.monster.Creeper ||
            entity instanceof net.minecraft.world.entity.monster.Spider ||
            entity instanceof net.minecraft.world.entity.monster.EnderMan) {
            if (random.nextInt(2500) == 0) {
                event.getDrops().add(new net.minecraft.world.entity.item.ItemEntity(
                        level, entity.getX(), entity.getY(), entity.getZ(),
                        new ItemStack(DEEPWOKEN_RELIC.get()))
                );
            }
        }

        // Cindering Core - 1/1500 from any Nether mob
        net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dim = level.dimension();
        if (dim != net.minecraft.world.level.Level.NETHER) return;
        if (entity instanceof net.minecraft.world.entity.monster.Blaze ||
            entity instanceof net.minecraft.world.entity.monster.Ghast ||
            entity instanceof net.minecraft.world.entity.monster.MagmaCube ||
            entity instanceof net.minecraft.world.entity.monster.WitherSkeleton ||
            entity instanceof net.minecraft.world.entity.boss.wither.WitherBoss) {
            if (random.nextInt(1500) == 0) {
                event.getDrops().add(new net.minecraft.world.entity.item.ItemEntity(
                        level, entity.getX(), entity.getY(), entity.getZ(),
                        new ItemStack(CINDERING_CORE.get()))
                );
            }
        }

        // Darkened Rubble - from ZombifiedPiglin only (1/150)
        if (entity instanceof net.minecraft.world.entity.monster.ZombifiedPiglin) {
            if (random.nextInt(150) == 0) {
                event.getDrops().add(new net.minecraft.world.entity.item.ItemEntity(
                        level, entity.getX(), entity.getY(), entity.getZ(),
                        new ItemStack(DARKENED_RUBBLE.get()))
                );
            }
        }

        // Weapon drops - 1/1000
        if (random.nextInt(1000) != 0) return;

        if (entity instanceof net.minecraft.world.entity.monster.EnderMan) {
            event.getDrops().add(new net.minecraft.world.entity.item.ItemEntity(
                    level, entity.getX(), entity.getY(), entity.getZ(),
                    new ItemStack(SENBONZAKURA.get()))
            );
        } else if (entity instanceof net.minecraft.world.entity.monster.Zombie) {
            event.getDrops().add(new net.minecraft.world.entity.item.ItemEntity(
                    level, entity.getX(), entity.getY(), entity.getZ(),
                    new ItemStack(CRYPT_BLADE.get()))
            );
        } else if (entity instanceof net.minecraft.world.entity.monster.Skeleton) {
            event.getDrops().add(new net.minecraft.world.entity.item.ItemEntity(
                    level, entity.getX(), entity.getY(), entity.getZ(),
                    new ItemStack(CRESCENT_BLADE.get()))
            );
        }
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void onBlockBreak(net.minecraftforge.event.level.BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) return;
        net.minecraft.world.level.Level level = (net.minecraft.world.level.Level) event.getLevel();
        net.minecraft.world.level.block.state.BlockState state = event.getState();
        net.minecraft.world.level.block.Block block = state.getBlock();

        // Ancient Amber - 1/1000 when stripping logs
        if (block.toString().contains("stripped") || block.toString().contains("log")) {
            if (event.getPlayer() != null && event.getPlayer().getMainHandItem().getItem() instanceof net.minecraft.world.item.AxeItem) {
                if (random.nextInt(1000) == 0) {
                    level.addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(
                            level, event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(),
                            new ItemStack(ANCIENT_AMBER.get()))
                    );
                }
            }
        }

        // Exalted Blossom - 1/5000 from cherry blossom leaves
        if (block.toString().contains("cherry_leaves")) {
            if (random.nextInt(5000) == 0) {
                level.addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(
                        level, event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(),
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

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            DashPacket.register();
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(SENBONZAKURA);
            event.accept(CRESCENT_BLADE);
            event.accept(CRYPT_BLADE);
            event.accept(SAW_BLADE_AXE);
            event.accept(ARCHAIC_CROWN);
            event.accept(INKY_PEARL);
            event.accept(EXALTED_BLOSSOM);
            event.accept(ANCIENT_AMBER);
            event.accept(DEEPWOKEN_RELIC);
            event.accept(EXCELLENT_PRINCESS_DIAMOND);
            event.accept(SACRED_THREADS);
            event.accept(CRUDE_JEWEL_STEEL);
            event.accept(TAMAHAGANE);
            event.accept(SUPREME_ALLOY);
            event.accept(CINDERING_CORE);
            event.accept(ULTRA_CONDUCTIVE_MATERIAL);
            event.accept(SKYSTONE);
            event.accept(DARKENED_RUBBLE);
            event.accept(SINNERS_ASH);
            event.accept(FROZEN_CORE);
            event.accept(RITE_OF_COMMANDMENTS);
            event.accept(PYRERIZER);
            event.accept(YUKI_NO_AME);
            event.accept(THE_FINALE);
        }
    }
}
