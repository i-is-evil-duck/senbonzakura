package com.senbonzakura.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.BlockTags;

public class SawBladeAxeItem extends SwordItem {

    public SawBladeAxeItem(Properties properties) {
        super(Tiers.IRON, 3, -2.4f, properties);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        // Mine wood as fast as a diamond axe (efficiency 8.0f)
        if (state.is(BlockTags.PLANKS) || state.is(BlockTags.LOGS) ||
            state.getBlock().toString().contains("wood") ||
            state.getBlock().toString().contains("log")) {
            return 8.0f; // Diamond axe speed
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState state) {
        // Can harvest wood blocks like an axe
        if (state.is(BlockTags.PLANKS) || state.is(BlockTags.LOGS)) {
            return true;
        }
        return super.isCorrectToolForDrops(state);
    }
}
