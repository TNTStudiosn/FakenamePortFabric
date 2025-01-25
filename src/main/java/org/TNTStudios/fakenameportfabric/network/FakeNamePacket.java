package org.TNTStudios.fakenameportfabric.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.TNTStudios.fakenameportfabric.Fakenameportfabric;
import org.TNTStudios.fakenameportfabric.FakeName;

public class FakeNamePacket {
    public static final Identifier FAKE_NAME_PACKET_ID = new Identifier(Fakenameportfabric.MOD_ID, "fake_name_packet");

    public static void sendFakeName(ServerPlayerEntity player, String fakeName) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(fakeName);
        ServerPlayNetworking.send(player, FAKE_NAME_PACKET_ID, buf);

        // 🔹 Actualiza el tab list
        updateTabList(player, fakeName);

        // 🔹 Actualiza el nombre sobre la cabeza
        updateNametag(player, fakeName);
    }

    private static void updateTabList(ServerPlayerEntity player, String fakeName) {
        player.setCustomName(Text.literal(fakeName));
        player.setCustomNameVisible(true);

        for (ServerPlayerEntity otherPlayer : player.getServer().getPlayerManager().getPlayerList()) {
            otherPlayer.networkHandler.sendPacket(new PlayerListS2CPacket(
                    PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, player
            ));
        }
    }

    private static void updateNametag(ServerPlayerEntity player, String fakeName) {
        Scoreboard scoreboard = player.getServer().getScoreboard();
        Team team = scoreboard.getTeam(fakeName);

        if (team == null) {
            team = scoreboard.addTeam(fakeName);
        }

        team.setDisplayName(Text.literal(fakeName));
        team.setPrefix(Text.literal("§e[Fake]§r ")); // Opcional
        team.setNameTagVisibilityRule(Team.VisibilityRule.ALWAYS);
        scoreboard.addPlayerToTeam(player.getEntityName(), team);

        // 🔹 Envía el paquete de actualización del equipo a todos los jugadores
        for (ServerPlayerEntity otherPlayer : player.getServer().getPlayerManager().getPlayerList()) {
            otherPlayer.networkHandler.sendPacket(TeamS2CPacket.updateTeam(team, true));
        }
    }
}
