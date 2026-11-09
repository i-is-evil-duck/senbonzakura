package com.j3ly.senbonzakura;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;

public class ModGameRules {
    public static final GameRules.Key<GameRules.BooleanValue> ABILITY_XP_COST_ENABLED =
            GameRules.register("senbonzakuraAbilityXpCostEnabled", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));

    public static final GameRules.Key<GameRules.IntegerValue> ABILITY_XP_COST_LEVELS =
            GameRules.register("senbonzakuraAbilityXpCostLevels", GameRules.Category.PLAYER, GameRules.IntegerValue.create(1));

    /**
     * Checks gamerules and consumes XP levels if required.
     * @return true if activation should proceed, false if blocked due to insufficient XP.
     */
    public static boolean tryConsumeAbilityXp(Player player, ServerLevel level) {
        if (!level.getGameRules().getBoolean(ABILITY_XP_COST_ENABLED)) {
            return true;
        }
        int cost = level.getGameRules().getInt(ABILITY_XP_COST_LEVELS);
        if (cost <= 0) {
            return true;
        }
        if (player.getAbilities().instabuild) {
            return true;
        }
        if (player.experienceLevel < cost) {
            player.displayClientMessage(Component.translatable("message.senbonzakura.not_enough_xp", cost), true);
            return false;
        }
        player.giveExperienceLevels(-cost);
        return true;
    }

    public static void init() {
        // Ensures class is loaded and gamerules are registered.
    }
}
