package com.j3ly.senbonzakura.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.level.Level;

public class GodRockItem extends Item {
    public GodRockItem(Properties properties) {
        super(properties
                .stacksTo(1)
                .component(DataComponents.UNBREAKABLE, new Unbreakable(true))
                .attributes(ItemAttributeModifiers.builder()
                        .add(Attributes.ATTACK_DAMAGE,
                                new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 18.5, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND)
                        .add(Attributes.ATTACK_SPEED,
                                new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.4, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND)
                        .build()));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide) return;
        if (!(entity instanceof Player player)) return;
        if (player.getMainHandItem() != stack && player.getOffhandItem() != stack) return;

        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 1, false, false, true));

        if (level instanceof ServerLevel serverLevel && player.getRandom().nextInt(4) == 0) {
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                    player.getX() + (player.getRandom().nextDouble() - 0.5) * 0.8,
                    player.getY() + 0.5 + player.getRandom().nextDouble() * 1.0,
                    player.getZ() + (player.getRandom().nextDouble() - 0.5) * 0.8,
                    1, 0.0, 0.02, 0.0, 0.01);
        }
    }
}
