package org.TNTStudios.fakenameportfabric;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.server.network.ServerPlayerEntity;

public class FakeName {
    public static final TrackedData<String> FAKE_NAME = DataTracker.registerData(ServerPlayerEntity.class, TrackedDataHandlerRegistry.STRING);

    public static void register(ServerPlayerEntity player) {
        try {
            player.getDataTracker().startTracking(FAKE_NAME, ""); // Registra la variable en el servidor
        } catch (IllegalStateException ignored) {
        }
    }

    public static void setFakeName(ServerPlayerEntity player, String fakeName) {
        try {
            player.getDataTracker().set(FAKE_NAME, fakeName); // Guarda el nombre falso en el servidor
        } catch (Exception ignored) {
        }
    }

    public static String getFakeName(ServerPlayerEntity player) {
        try {
            return player.getDataTracker().get(FAKE_NAME);
        } catch (Exception ignored) {
            return player.getEntityName(); // Si falla, devuelve el nombre real
        }
    }
}
