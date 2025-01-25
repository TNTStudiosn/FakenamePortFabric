package org.TNTStudios.fakenameportfabric;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.server.network.ServerPlayerEntity;

public class FakeName {
    public static final TrackedData<String> FAKE_NAME = DataTracker.registerData(ServerPlayerEntity.class, TrackedDataHandlerRegistry.STRING);

    public static void register(ServerPlayerEntity player) {
        try {
            player.getDataTracker().startTracking(FAKE_NAME, ""); // Registrar el TrackedData
        } catch (IllegalStateException ignored) {
            // Si ya está registrado, no hacemos nada
        }
    }

    public static void setFakeName(ServerPlayerEntity player, String fakeName) {
        try {
            player.getDataTracker().set(FAKE_NAME, fakeName); // Evitar errores si aún no está registrado
        } catch (Exception ignored) {
            // Si el TrackedData no está inicializado, no crashea el servidor
        }
    }

    public static String getFakeName(ServerPlayerEntity player) {
        try {
            return player.getDataTracker().get(FAKE_NAME);
        } catch (Exception ignored) {
            return player.getEntityName(); // Si falla, devolver el nombre real
        }
    }
}
