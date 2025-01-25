package org.TNTStudios.fakenameportfabric;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakeName {
    private static final Logger LOGGER = LoggerFactory.getLogger("FakeName");
    public static final TrackedData<String> FAKE_NAME = DataTracker.registerData(ServerPlayerEntity.class, TrackedDataHandlerRegistry.STRING);

    public static void register(ServerPlayerEntity player) {
        try {
            player.getDataTracker().startTracking(FAKE_NAME, "");
            LOGGER.info("[FakeName] Registrado FakeName para jugador: {}", player.getEntityName());
        } catch (IllegalStateException e) {
            LOGGER.warn("[FakeName] FakeName ya registrado para jugador: {}", player.getEntityName());
        }
    }

    public static void setFakeName(ServerPlayerEntity player, String fakeName) {
        if (isTrackingFakeName(player)) {
            try {
                player.getDataTracker().set(FAKE_NAME, fakeName);
                LOGGER.info("[FakeName] Se ha cambiado el nombre de {} a {}", player.getEntityName(), fakeName);
            } catch (Exception e) {
                LOGGER.error("[FakeName] No se pudo establecer el FakeName para {}: {}", player.getEntityName(), e.getMessage());
            }
        } else {
            LOGGER.error("[FakeName] No se pudo establecer el FakeName porque no está registrado en DataTracker: {}", player.getEntityName());
        }
    }

    public static String getFakeName(ServerPlayerEntity player) {
        if (isTrackingFakeName(player)) {
            try {
                return player.getDataTracker().get(FAKE_NAME);
            } catch (Exception e) {
                LOGGER.error("[FakeName] Error al obtener FakeName para {}: {}", player.getEntityName(), e.getMessage());
            }
        }
        return player.getEntityName(); // Devuelve el nombre real si no hay FakeName registrado
    }

    private static boolean isTrackingFakeName(ServerPlayerEntity player) {
        try {
            player.getDataTracker().get(FAKE_NAME); // Si no está registrado, lanzará una excepción
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
