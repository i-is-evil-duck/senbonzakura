package com.senbonzakura;

import com.senbonzakura.item.CrescentBladeItem;
import com.senbonzakura.item.CryptBladeItem;
import com.senbonzakura.item.SawBladeAxeItem;
import com.senbonzakura.item.SenbonzakuraItem;
import com.senbonzakura.network.DashPacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
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
    private static final Random random = new Random();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Item> SENBONZAKURA = ITEMS.register("senbonzakura",
            () -> new SenbonzakuraItem(new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> CRESCENT_BLADE = ITEMS.register("crescent_blade",
            () -> new CrescentBladeItem(new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> CRYPT_BLADE = ITEMS.register("crypt_blade",
            () -> new CryptBladeItem(new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> SAW_BLADE_AXE = ITEMS.register("saw_blade_axe",
            () -> new SawBladeAxeItem(new Item.Properties()));

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

        if (mainHand.getItem() instanceof SenbonzakuraItem) {
            spawnSakuraParticles((ServerLevel) player.level(), player);
        } else if (mainHand.getItem() instanceof CrescentBladeItem) {
            spawnGreenParticles((ServerLevel) player.level(), player);
        } else if (mainHand.getItem() instanceof CryptBladeItem) {
            spawnBlackParticles((ServerLevel) player.level(), player);
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
        }
    }
}
