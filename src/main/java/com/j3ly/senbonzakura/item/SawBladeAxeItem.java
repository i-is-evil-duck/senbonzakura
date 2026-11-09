package com.j3ly.senbonzakura.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.state.BlockState;

public class SawBladeAxeItem extends AxeItem {

    public SawBladeAxeItem(Properties properties) {
        super(Tiers.IRON, properties.attributes(AxeItem.createAttributes(Tiers.IRON, 5, -3.0f)));
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(BlockTags.PLANKS) || state.is(BlockTags.LOGS) ||
            state.getBlock().toString().contains("wood") ||
            state.getBlock().toString().contains("log")) {
            return 8.0f;
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        if (state.is(BlockTags.PLANKS) || state.is(BlockTags.LOGS)) {
            return true;
        }
        return super.isCorrectToolForDrops(stack, state);
    }
}
