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

        // Enviar el paquete a todos los jugadores conectados
        for (ServerPlayerEntity otherPlayer : player.getServer().getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(otherPlayer, FAKE_NAME_PACKET_ID, buf);
        }

        // Asegurar que el tablist y el nametag se actualicen correctamente
        updateNametag(player, fakeName);
    }


    public static void updateNametag(ServerPlayerEntity player, String fakeName) {
        Scoreboard scoreboard = player.getServer().getScoreboard();
        String teamName = "FakeName_" + player.getUuidAsString().substring(0, 8);

        // Obtener o crear el equipo en el scoreboard
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.addTeam(teamName);
        }

        // Configurar el equipo correctamente
        team.setDisplayName(Text.literal(fakeName)); // Nombre del equipo
        team.setNameTagVisibilityRule(Team.VisibilityRule.ALWAYS);
        team.setPrefix(Text.literal("")); // Evita nombres duplicados en el Tablist
        team.setSuffix(Text.literal("")); // Evita nombres duplicados en el Tablist

        // Eliminar al jugador de cualquier otro equipo
        for (Team existingTeam : scoreboard.getTeams()) {
            if (existingTeam.getPlayerList().contains(player.getEntityName())) {
                scoreboard.removePlayerFromTeam(player.getEntityName(), existingTeam);
            }
        }

        // Agregar al jugador al equipo nuevo
        scoreboard.addPlayerToTeam(player.getEntityName(), team);

        // Enviar la actualización del equipo a todos los jugadores conectados
        for (ServerPlayerEntity otherPlayer : player.getServer().getPlayerManager().getPlayerList()) {
            otherPlayer.networkHandler.sendPacket(TeamS2CPacket.updateTeam(team, true));
        }

        // Actualizar el Tablist en el cliente local del jugador
        player.networkHandler.sendPacket(TeamS2CPacket.updateTeam(team, true));
    }




}
