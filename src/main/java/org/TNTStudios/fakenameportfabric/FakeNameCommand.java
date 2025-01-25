package org.TNTStudios.fakenameportfabric;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.command.argument.EntityArgumentType;

import java.util.Collection;
import java.util.Collections;

public class FakeNameCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerCommands(dispatcher);
        });
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("fakename")
                .then(CommandManager.literal("real")
                        .then(CommandManager.argument("fakename", StringArgumentType.string())
                                .executes(cmd -> handleRealname(cmd.getSource(), StringArgumentType.getString(cmd, "fakename")))))
                .then(CommandManager.literal("clear")
                        .executes(cmd -> handleClear(cmd.getSource(), Collections.singleton(cmd.getSource().getPlayerOrThrow())))
                        .then(CommandManager.argument("target", EntityArgumentType.players())
                                .executes(cmd -> handleClear(cmd.getSource(), EntityArgumentType.getPlayers(cmd, "target")))))
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("fakename", StringArgumentType.string())
                                .executes(cmd -> handleSetname(cmd.getSource(), Collections.singleton(cmd.getSource().getPlayerOrThrow()), StringArgumentType.getString(cmd, "fakename"))))
                        .then(CommandManager.argument("target", EntityArgumentType.players())
                                .then(CommandManager.argument("fakename", StringArgumentType.string())
                                        .executes(cmd -> handleSetname(cmd.getSource(), EntityArgumentType.getPlayers(cmd, "target"), StringArgumentType.getString(cmd, "fakename")))))));
    }

    private static int handleSetname(ServerCommandSource source, Collection<ServerPlayerEntity> players, String string) {
        string = string.replace("&", "§") + "§r";

        for (ServerPlayerEntity player : players) {
            FakeName.setFakeName(player, string);
            source.sendMessage(Text.literal(player.getName().getString() + "'s name is now " + string));
        }

        return 1;
    }

    private static int handleClear(ServerCommandSource source, Collection<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            FakeName.setFakeName(player, "");
            source.sendMessage(Text.literal(player.getName().getString() + "'s fake name was cleared!"));
        }
        return 1;
    }

    private static int handleRealname(ServerCommandSource source, String string) {
        string = string.replace("&", "§") + "§r";
        String stripped = net.minecraft.text.Text.of(string).getString();
        boolean found = false;

        for (ServerPlayerEntity player : source.getServer().getPlayerManager().getPlayerList()) {
            if (FakeName.getFakeName(player).equals(stripped)) {
                source.sendMessage(Text.literal(string + "'s real name is " + player.getEntityName()));
                found = true;
            }
        }

        if (!found) {
            source.sendError(Text.literal("No player with that name was found!"));
        }
        return found ? 1 : 0;
    }
}
