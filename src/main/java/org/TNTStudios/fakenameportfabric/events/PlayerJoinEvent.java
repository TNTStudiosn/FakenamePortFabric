package org.TNTStudios.fakenameportfabric.events;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.TNTStudios.fakenameportfabric.FakeName;
import org.TNTStudios.fakenameportfabric.network.FakeNamePacket;

import java.util.concurrent.atomic.AtomicInteger;

public class PlayerJoinEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger("PlayerJoinEvent");
    private static final AtomicInteger playerCounter = new AtomicInteger(1); // 🔹 Contador de jugadores

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();

            // 🔹 Generar un nombre "Player1", "Player2", "Player3"...
            String fakeName = "Player" + playerCounter.getAndIncrement();

            // 🔹 Registrar y asignar el FakeName
            FakeName.register(player);
            FakeName.setFakeName(player, fakeName);
            LOGGER.info("[FakeName] Asignado FakeName '{}' a {}", fakeName, player.getEntityName());

            // 🔹 Enviar el FakeName al cliente
            FakeNamePacket.sendFakeName(player, fakeName);
        });
    }
}
