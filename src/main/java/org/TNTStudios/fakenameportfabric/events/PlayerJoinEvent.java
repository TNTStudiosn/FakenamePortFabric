package org.TNTStudios.fakenameportfabric.events;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.TNTStudios.fakenameportfabric.FakeName;
import org.TNTStudios.fakenameportfabric.network.FakeNamePacket;

public class PlayerJoinEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger("PlayerJoinEvent");

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            FakeName.register(player);

            // Obtener el FakeName o el nombre real si no tiene
            String fakeName = FakeName.getFakeName(player);
            if (fakeName == null || fakeName.isEmpty()) {
                fakeName = player.getEntityName();
            }

            // Enviar el nombre falso actualizado al jugador
            FakeNamePacket.sendFakeName(player, fakeName);

            // Actualizar solo el jugador que entró al servidor
            FakeNamePacket.updateNametag(player, fakeName);

            // Notificar a todos los jugadores sobre el FakeName actualizado
            for (ServerPlayerEntity otherPlayer : server.getPlayerManager().getPlayerList()) {
                if (!otherPlayer.equals(player)) {
                    FakeNamePacket.updateNametag(otherPlayer, FakeName.getFakeName(otherPlayer));
                }
            }

            LOGGER.info("[PlayerJoinEvent] {} ha ingresado con el nombre falso: {}", player.getEntityName(), fakeName);
        });
    }
}
