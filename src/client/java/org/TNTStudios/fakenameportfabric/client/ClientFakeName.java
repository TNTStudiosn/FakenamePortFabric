package org.TNTStudios.fakenameportfabric.client;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientFakeName {
    private static final Map<String, String> CLIENT_FAKE_NAMES = new ConcurrentHashMap<>();

    public static void setFakeName(String playerName, String fakeName) {
        CLIENT_FAKE_NAMES.put(playerName, fakeName);
    }

    public static String getFakeName(String playerName) {
        return CLIENT_FAKE_NAMES.getOrDefault(playerName, playerName);
    }

}
