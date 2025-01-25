package org.TNTStudios.fakenameportfabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import org.TNTStudios.fakenameportfabric.Fakenameportfabric;

public class FakenameportfabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Registrar la recepción de paquetes del servidor con nombres falsos
        ClientPlayNetworking.registerGlobalReceiver(
                new Identifier(Fakenameportfabric.MOD_ID, "fake_name_packet"),
                (client, handler, buf, responseSender) -> {
                    String playerName = buf.readString();  // Nombre original del jugador
                    String fakeName = buf.readString();    // Nombre falso enviado desde el servidor

                    client.execute(() -> {
                        ClientFakeName.setFakeName(playerName, fakeName); // Guarda el nombre en el cliente
                    });
                }
        );
    }
}
