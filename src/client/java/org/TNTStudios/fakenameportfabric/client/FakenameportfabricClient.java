package org.TNTStudios.fakenameportfabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakenameportfabricClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("FakenameportfabricClient");

    @Override
    public void onInitializeClient() {
        LOGGER.info("[FakeName] Inicializando mod en el CLIENTE...");

        ClientPlayNetworking.registerGlobalReceiver(
                new Identifier("fakenameportfabric", "fake_name_packet"),
                (client, handler, buf, responseSender) -> {
                    String playerName = buf.readString();
                    String fakeName = buf.readString();

                    client.execute(() -> {
                        ClientFakeName.setFakeName(playerName, fakeName);

                        // Asegurar que el Tablist y el Nametag se actualicen en el cliente
                        PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(playerName);
                        if (entry != null) {
                            entry.setDisplayName(Text.literal(fakeName));
                        }
                    });
                }
        );

    }
}
