package org.TNTStudios.fakenameportfabric.client;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClientFakeName {
    private static final Map<UUID, String> CLIENT_FAKE_NAMES = new ConcurrentHashMap<>();

    public static void setFakeName(UUID playerUUID, String fakeName) {
        CLIENT_FAKE_NAMES.put(playerUUID, fakeName);
    }

    public static String getFakeName(UUID playerUUID) {
        return CLIENT_FAKE_NAMES.getOrDefault(playerUUID, null);
    }
}
