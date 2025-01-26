package org.TNTStudios.fakenameportfabric.events;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.TNTStudios.fakenameportfabric.FakeName;
import org.TNTStudios.fakenameportfabric.network.FakeNamePacket;
import org.TNTStudios.fakenameportfabric.storage.FakeNameStorage;

public class PlayerJoinEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger("PlayerJoinEvent");

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity joiningPlayer = handler.getPlayer();

            // 1. Iniciar el seguimiento del DataTracker para FAKE_NAME
            FakeName.register(joiningPlayer);

            // 2. Establecer el nombre falso desde el almacenamiento
            String joiningFakeName = FakeNameStorage.getFakeName(joiningPlayer);
            FakeNameStorage.setFakeName(joiningPlayer, joiningFakeName);

            // 3. Enviar el nombre falso actualizado al jugador que se une
            FakeNamePacket.sendFakeName(joiningPlayer, joiningFakeName);

            // 4. Actualizar el nametag del jugador que se une
            FakeNamePacket.updateNametag(joiningPlayer, joiningFakeName);

            // 5. Notificar a todos los jugadores existentes sobre el FakeName actualizado del jugador que se une
            for (ServerPlayerEntity otherPlayer : server.getPlayerManager().getPlayerList()) {
                if (!otherPlayer.equals(joiningPlayer)) {
                    FakeNamePacket.updateNametag(otherPlayer, FakeNameStorage.getFakeName(otherPlayer));
                }
            }

            // 6. Enviar al jugador que se une los FakeNames de todos los jugadores existentes
            for (ServerPlayerEntity otherPlayer : server.getPlayerManager().getPlayerList()) {
                if (!otherPlayer.equals(joiningPlayer)) {
                    String otherFakeName = FakeNameStorage.getFakeName(otherPlayer);
                    FakeNamePacket.sendFakeNameToPlayer(joiningPlayer, otherPlayer, otherFakeName);
                }
            }

            LOGGER.info("[PlayerJoinEvent] {} ha ingresado con el nombre falso: {}", joiningPlayer.getEntityName(), joiningFakeName);
        });
    }
}
