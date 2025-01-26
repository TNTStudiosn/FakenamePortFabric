package org.TNTStudios.fakenameportfabric;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.UUID;

public class FakeName {
    private static final Logger LOGGER = LoggerFactory.getLogger("FakeName");
    // Registro correcto del TrackedData usando DataTracker
    public static final TrackedData<String> FAKE_NAME = DataTracker.registerData(ServerPlayerEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final Map<UUID, String> FAKE_NAMES = new ConcurrentHashMap<>();

    /**
     * Inicia el seguimiento del DataTracker para FAKE_NAME.
     *
     * @param player El jugador para el cual iniciar el seguimiento.
     */
    public static void register(ServerPlayerEntity player) {
        player.getDataTracker().startTracking(FAKE_NAME, "");
        LOGGER.info("[FakeName] Registrado FakeName para jugador: {}", player.getEntityName());
    }

    /**
     * Establece el nombre falso para un jugador.
     *
     * @param player    El jugador cuyo nombre falso se está estableciendo.
     * @param fakeName  El nuevo nombre falso.
     */
    public static void setFakeName(ServerPlayerEntity player, String fakeName) {
        FAKE_NAMES.put(player.getUuid(), fakeName);
        player.getDataTracker().set(FAKE_NAME, fakeName);
        LOGGER.info("[FakeName] Se ha cambiado el nombre de {} a {}", player.getEntityName(), fakeName);
    }

    /**
     * Obtiene el nombre falso de un jugador.
     *
     * @param player El jugador cuyo nombre falso se está obteniendo.
     * @return El nombre falso o el nombre real si no hay uno establecido.
     */
    public static String getFakeName(ServerPlayerEntity player) {
        return FAKE_NAMES.getOrDefault(player.getUuid(), player.getEntityName());
    }
}
