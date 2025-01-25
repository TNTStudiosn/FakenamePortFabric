package org.TNTStudios.fakenameportfabric.events;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.TNTStudios.fakenameportfabric.FakeName;
import org.TNTStudios.fakenameportfabric.network.FakeNamePacket;

public class PlayerJoinEvent {
    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            FakeName.register(player);

            String fakeName = FakeName.getFakeName(player);
            if (fakeName != null && !fakeName.isEmpty()) {
                FakeNamePacket.sendFakeName(player, fakeName);
            }
        });
    }
}
