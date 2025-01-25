package org.TNTStudios.fakenameportfabric;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.server.network.ServerPlayerEntity;

public class FakeName {
    public static final TrackedData<String> FAKE_NAME = DataTracker.registerData(ServerPlayerEntity.class, TrackedDataHandlerRegistry.STRING);

    public static void register(ServerPlayerEntity player) {
        player.getDataTracker().startTracking(FAKE_NAME, "");
    }

    public static void setFakeName(ServerPlayerEntity player, String fakeName) {
        player.getDataTracker().set(FAKE_NAME, fakeName);
    }

    public static String getFakeName(ServerPlayerEntity player) {
        return player.getDataTracker().get(FAKE_NAME);
    }
}
