package org.TNTStudios.fakenameportfabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.TNTStudios.fakenameportfabric.FakeNameCommand;
import org.TNTStudios.fakenameportfabric.events.PlayerJoinEvent;
import org.TNTStudios.fakenameportfabric.storage.FakeNameStorage;

public class Fakenameportfabric implements ModInitializer {
    public static final String MOD_ID = "fakenameportfabric";

    @Override
    public void onInitialize() {
        FakeNameCommand.register();
        PlayerJoinEvent.register();

        // Registrar eventos de ciclo de vida del servidor
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            FakeNameStorage.initialize(server);
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            FakeNameStorage.save();
        });
    }
}
