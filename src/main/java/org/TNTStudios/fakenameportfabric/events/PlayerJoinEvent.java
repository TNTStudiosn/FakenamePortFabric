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

            // 🔹 Registrar FakeName en el DataTracker antes de cualquier acceso
            FakeName.register(player);
            LOGGER.info("[FakeName] Registrado FakeName para jugador: {}", player.getEntityName());

            String fakeName = FakeName.getFakeName(player);
            LOGGER.info("[FakeName] FakeName actual de {}: {}", player.getEntityName(), fakeName);

            if (!fakeName.isEmpty()) {
                FakeNamePacket.sendFakeName(player, fakeName);
            }
        });
    }
}
