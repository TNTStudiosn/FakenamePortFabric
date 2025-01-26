package org.TNTStudios.fakenameportfabric;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.command.argument.EntityArgumentType;
import org.TNTStudios.fakenameportfabric.network.FakeNamePacket;

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
                                .requires(source -> source.hasPermissionLevel(4)) // Solo OPs nivel 4 pueden borrar nombres de otros
                                .executes(cmd -> handleClear(cmd.getSource(), EntityArgumentType.getPlayers(cmd, "target")))))
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("fakename", StringArgumentType.string())
                                .executes(cmd -> handleSetname(cmd.getSource(), Collections.singleton(cmd.getSource().getPlayerOrThrow()), StringArgumentType.getString(cmd, "fakename"))))
                        .then(CommandManager.argument("target", EntityArgumentType.players())
                                .requires(source -> source.hasPermissionLevel(4)) // Solo OPs nivel 4 pueden cambiar el nombre de otros
                                .then(CommandManager.argument("fakename", StringArgumentType.string())
                                        .executes(cmd -> handleSetname(cmd.getSource(), EntityArgumentType.getPlayers(cmd, "target"), StringArgumentType.getString(cmd, "fakename")))))));
    }


    private static int handleSetname(ServerCommandSource source, Collection<ServerPlayerEntity> players, String fakeName) {
        fakeName = fakeName.replace("&", "§") + "§r";

        // Verificar si el nombre ya está en uso
        for (ServerPlayerEntity player : source.getServer().getPlayerManager().getPlayerList()) {
            if (FakeName.getFakeName(player).equals(fakeName)) {
                source.sendError(Text.literal("Ese nombre ya está en uso!"));
                return 0;
            }
        }

        for (ServerPlayerEntity player : players) {
            FakeName.setFakeName(player, fakeName);
            FakeNamePacket.sendFakeName(player, fakeName);
            source.sendMessage(Text.literal("El nombre de " + player.getName().getString() + " ahora es " + fakeName));
        }

        return 1;
    }


    private static int handleClear(ServerCommandSource source, Collection<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            // Restaurar el nombre real en FakeName y el scoreboard
            FakeName.setFakeName(player, player.getEntityName());
            FakeNamePacket.sendFakeName(player, player.getEntityName());

            // Obtener el Scoreboard
            Scoreboard scoreboard = player.getServer().getScoreboard();
            String teamName = "FakeNameGlobal";
            Team team = scoreboard.getTeam(teamName);

            // Remover al jugador del equipo
            if (team != null) {
                scoreboard.removePlayerFromTeam(player.getEntityName(), team);
            }

            source.sendMessage(Text.literal("El nombre falso de " + player.getName().getString() + " ha sido eliminado."));
        }
        return 1;
    }




    private static int handleRealname(ServerCommandSource source, String string) {
        string = string.replace("&", "§") + "§r";
        String stripped = net.minecraft.text.Text.of(string).getString();
        boolean found = false;

        for (ServerPlayerEntity player : source.getServer().getPlayerManager().getPlayerList()) {
            if (FakeName.getFakeName(player).equals(stripped)) {
                source.sendMessage(Text.literal("El nombre real de " + string + " es " + player.getEntityName()));
                found = true;
            }
        }

        if (!found) {
            source.sendError(Text.literal("No se encontró a ningún jugador con ese nombre."));
        }
        return found ? 1 : 0;
    }
}