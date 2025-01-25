package org.TNTStudios.fakenameportfabric.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.text.Text;
import org.TNTStudios.fakenameportfabric.Fakenameportfabric;
import org.TNTStudios.fakenameportfabric.FakeName;

public class FakeNamePacket {
    public static final Identifier FAKE_NAME_PACKET_ID = new Identifier(Fakenameportfabric.MOD_ID, "fake_name_packet");

    public static void sendFakeName(ServerPlayerEntity player, String fakename) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(fakename);
        ServerPlayNetworking.send(player, FAKE_NAME_PACKET_ID, buf);

        // 🔹 Asegurar que el tablist se actualiza para todos los jugadores
        updateTabList(player, fakename);
    }

    private static void updateTabList(ServerPlayerEntity player, String fakeName) {
        player.setCustomName(Text.literal(fakeName)); // Cambia el nombre en el juego
        player.setCustomNameVisible(true); // Asegura que el nombre se vea sobre la cabeza

        for (ServerPlayerEntity otherPlayer : player.getServer().getPlayerManager().getPlayerList()) {
            otherPlayer.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.play.PlayerListS2CPacket(
                    net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, player
            ));
        }
    }
}
