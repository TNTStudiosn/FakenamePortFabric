package org.TNTStudios.fakenameportfabric;

import net.fabricmc.api.ModInitializer;

public class Fakenameportfabric implements ModInitializer {
    public static final String MOD_ID = "fakenameportfabric";

    @Override
    public void onInitialize() {
        FakeNameCommand.register();
    }
}
