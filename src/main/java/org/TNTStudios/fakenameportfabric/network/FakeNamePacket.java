package org.TNTStudios.fakenameportfabric.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.TNTStudios.fakenameportfabric.Fakenameportfabric;
import org.TNTStudios.fakenameportfabric.FakeName;

public class FakeNamePacket {
    private static final Logger LOGGER = LoggerFactory.getLogger("FakeNamePacket");
    public static final Identifier FAKE_NAME_PACKET_ID = new Identifier(Fakenameportfabric.MOD_ID, "fake_name_packet");

    public static void sendFakeName(ServerPlayerEntity player, String fakeName) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(player.getEntityName());
        buf.writeString(fakeName);

        for (ServerPlayerEntity otherPlayer : player.getServer().getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(otherPlayer, FAKE_NAME_PACKET_ID, buf);
        }

        updateNametag(player, fakeName);
    }

    private static void updateNametag(ServerPlayerEntity player, String fakeName) {
        Scoreboard scoreboard = player.getServer().getScoreboard();
        String teamName = "FakeName_" + player.getUuidAsString().substring(0, 8); // Nombre único del equipo

        // Obtener o crear el equipo en el scoreboard
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.addTeam(teamName);
            team.setDisplayName(Text.literal(fakeName));
            team.setNameTagVisibilityRule(Team.VisibilityRule.ALWAYS);
            team.setPrefix(Text.literal(fakeName + " "));
        }

        // Eliminar al jugador de cualquier otro equipo
        Team currentTeam = scoreboard.getPlayerTeam(player.getEntityName());
        if (currentTeam != null) {
            scoreboard.removePlayerFromTeam(player.getEntityName(), currentTeam);
        }


        // Agregar el jugador al nuevo equipo
        scoreboard.addPlayerToTeam(player.getEntityName(), team);

        // Enviar la actualización del equipo a todos los jugadores
        for (ServerPlayerEntity otherPlayer : player.getServer().getPlayerManager().getPlayerList()) {
            otherPlayer.networkHandler.sendPacket(TeamS2CPacket.updateTeam(team, true));
        }
    }

}
