package org.TNTStudios.fakenameportfabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class FakenameportfabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {


        ClientPlayNetworking.registerGlobalReceiver(
                new Identifier("fakenameportfabric", "fake_name_packet"),
                (client, handler, buf, responseSender) -> {
                    UUID playerUUID = buf.readUuid();
                    String fakeName = buf.readString();

                    client.execute(() -> {
                        ClientFakeName.setFakeName(playerUUID, fakeName);

                        PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(playerUUID);
                        if (entry != null) {
                            entry.setDisplayName(Text.literal(fakeName));
                        }
                    });
                }
        );
    }
}
