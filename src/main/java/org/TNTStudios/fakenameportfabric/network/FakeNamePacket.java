package org.TNTStudios.fakenameportfabric.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.TNTStudios.fakenameportfabric.Fakenameportfabric;

public class FakeNamePacket {
    public static final Identifier FAKE_NAME_PACKET_ID = new Identifier(Fakenameportfabric.MOD_ID, "fake_name_packet");

    public static void sendFakeName(ServerPlayerEntity player, String fakename) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(fakename);
        ServerPlayNetworking.send(player, FAKE_NAME_PACKET_ID, buf);
    }
}
