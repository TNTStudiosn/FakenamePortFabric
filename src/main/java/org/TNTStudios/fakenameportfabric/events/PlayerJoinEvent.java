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
            String fakeName = FakeName.getFakeName(player);

            if (!fakeName.isEmpty()) {
                FakeNamePacket.sendFakeName(player, fakeName);
            } else {
                FakeNamePacket.sendFakeName(player, player.getEntityName());
            }

            // Actualizar el tablist y nametag después de que el jugador se conecte
            server.execute(() -> {
                FakeNamePacket.updateNametag(player, fakeName);

                // Enviar la actualización del nombre falso a todos los jugadores
                for (ServerPlayerEntity otherPlayer : server.getPlayerManager().getPlayerList()) {
                    FakeNamePacket.updateNametag(otherPlayer, FakeName.getFakeName(otherPlayer));
                }
            });

        });
    }


}
