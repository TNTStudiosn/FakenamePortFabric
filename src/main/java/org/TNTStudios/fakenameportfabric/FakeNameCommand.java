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
import org.TNTStudios.fakenameportfabric.storage.FakeNameStorage;

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
                                        .executes(cmd -> handleSetname(cmd.getSource(), EntityArgumentType.getPlayers(cmd, "target"), StringArgumentType.getString(cmd, "fakename"))))))
                // Nuevo Subcomando HideName para el ejecutante actual
                .then(CommandManager.literal("HideName")
                        .executes(cmd -> handleHideName(cmd.getSource(), Collections.singleton(cmd.getSource().getPlayerOrThrow()))))
                // Nuevo Subcomando HideName para múltiples objetivos con permisos
                .then(CommandManager.literal("HideName")
                        .then(CommandManager.argument("target", EntityArgumentType.players())
                                .requires(source -> source.hasPermissionLevel(4)) // Solo OPs nivel 4 pueden ocultar nombres de otros
                                .executes(cmd -> handleHideName(cmd.getSource(), EntityArgumentType.getPlayers(cmd, "target")))))
        );
    }

    private static int handleSetname(ServerCommandSource source, Collection<ServerPlayerEntity> players, String fakeName) {
        fakeName = fakeName.replace("&", "§") + "§r";

        // Verificar si el nombre ya está en uso
        for (ServerPlayerEntity player : source.getServer().getPlayerManager().getPlayerList()) {
            if (FakeNameStorage.getFakeName(player).equals(fakeName)) {
                source.sendError(Text.literal("Ese nombre ya está en uso!"));
                return 0;
            }
        }

        for (ServerPlayerEntity player : players) {
            FakeNameStorage.setFakeName(player, fakeName);
            FakeNamePacket.sendFakeName(player, fakeName);
            source.sendMessage(Text.literal("El nombre de " + player.getName().getString() + " ahora es " + fakeName));
        }

        return 1;
    }

    private static int handleClear(ServerCommandSource source, Collection<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            // Utiliza el método del almacenamiento para borrar el fake name de forma persistente
            FakeNameStorage.clearFakeName(player);
            // Envía el paquete para actualizar el nombre en los clientes
            FakeNamePacket.sendFakeName(player, player.getEntityName());

            source.sendMessage(Text.literal("El nombre falso de " + player.getName().getString() + " ha sido eliminado."));
        }
        return 1;
    }



    private static int handleRealname(ServerCommandSource source, String string) {
        string = string.replace("&", "§") + "§r";
        String stripped = net.minecraft.text.Text.of(string).getString();
        boolean found = false;

        for (ServerPlayerEntity player : source.getServer().getPlayerManager().getPlayerList()) {
            if (FakeNameStorage.getFakeName(player).equals(stripped)) {
                source.sendMessage(Text.literal("El nombre real de " + string + " es " + player.getEntityName()));
                found = true;
            }
        }

        if (!found) {
            source.sendError(Text.literal("No se encontró a ningún jugador con ese nombre."));
        }
        return found ? 1 : 0;
    }

    // Nuevo Método para Handle HideName
    private static int handleHideName(ServerCommandSource source, Collection<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            // Enviar un nombre falso "." para ocultar el nombre
            FakeNameStorage.setFakeName(player, ".");
            FakeNamePacket.sendFakeName(player, ".");
            source.sendMessage(Text.literal("El nombre de " + player.getName().getString() + " ahora está oculto."));
        }
        return 1;
    }
}
