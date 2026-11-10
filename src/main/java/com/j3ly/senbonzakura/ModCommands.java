package com.j3ly.senbonzakura;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("nodrop")
                .requires(src -> src.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(ctx -> setDrop(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), 0))));

        dispatcher.register(Commands.literal("drop")
                .requires(src -> src.hasPermission(2))
                .then(Commands.argument("percent", IntegerArgumentType.integer(0, 100))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> setDrop(ctx.getSource(),
                                        EntityArgument.getPlayer(ctx, "player"),
                                        IntegerArgumentType.getInteger(ctx, "percent"))))));
    }

    private static int setDrop(CommandSourceStack source, ServerPlayer target, int percent) throws CommandSyntaxException {
        ServerLevel level = source.getLevel();
        ModDropData.get(level).setPercent(target.getUUID(), percent);
        if (percent <= 0) {
            source.sendSuccess(() -> Component.literal("Disabled Senbonzakura drops for " + target.getGameProfile().getName()), true);
        } else if (percent >= 100) {
            source.sendSuccess(() -> Component.literal("Set Senbonzakura drops to 100% (normal) for " + target.getGameProfile().getName()), true);
        } else {
            source.sendSuccess(() -> Component.literal("Set Senbonzakura drops to " + percent + "% for " + target.getGameProfile().getName()), true);
        }
        return 1;
    }
}
