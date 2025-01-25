package org.TNTStudios.fakenameportfabric;

import net.fabricmc.api.ModInitializer;
import org.TNTStudios.fakenameportfabric.events.PlayerJoinEvent;

public class Fakenameportfabric implements ModInitializer {
    public static final String MOD_ID = "fakenameportfabric";

    @Override
    public void onInitialize() {
        FakeNameCommand.register();
        PlayerJoinEvent.register(); // Se asegura que los nombres falsos sean registrados correctamente al entrar
    }
}
