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

public class FakeNamePacket {
    private static final Logger LOGGER = LoggerFactory.getLogger("FakeNamePacket");
    public static final Identifier FAKE_NAME_PACKET_ID = new Identifier(Fakenameportfabric.MOD_ID, "fake_name_packet");

    /**
     * Envía el nombre falso de un jugador a **todos** los jugadores en el servidor.
     * Utilizado cuando un jugador cambia su nombre falso.
     *
     * @param player    El jugador cuyo nombre falso se está actualizando.
     * @param fakeName  El nuevo nombre falso.
     */
    public static void sendFakeName(ServerPlayerEntity player, String fakeName) {
        updateNametag(player, fakeName);

        for (ServerPlayerEntity otherPlayer : player.getServer().getPlayerManager().getPlayerList()) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeUuid(player.getUuid());
            buf.writeString(fakeName);
            ServerPlayNetworking.send(otherPlayer, FAKE_NAME_PACKET_ID, buf);
            LOGGER.info("[FakeNamePacket] Enviado paquete a {} para actualizar el nombre de {}", otherPlayer.getName().getString(), player.getEntityName());
        }
    }

    /**
     * Envía el nombre falso de un jugador a un jugador específico.
     * Utilizado cuando un nuevo jugador se une y necesita conocer los nombres falsos de los jugadores existentes.
     *
     * @param targetPlayer El jugador que recibirá la información.
     * @param player       El jugador cuyo nombre falso se está enviando.
     * @param fakeName     El nombre falso.
     */
    public static void sendFakeNameToPlayer(ServerPlayerEntity targetPlayer, ServerPlayerEntity player, String fakeName) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(player.getUuid());
        buf.writeString(fakeName);
        ServerPlayNetworking.send(targetPlayer, FAKE_NAME_PACKET_ID, buf);
        LOGGER.info("[FakeNamePacket] Enviado paquete a {} para actualizar el nombre de {}", targetPlayer.getName().getString(), player.getEntityName());
    }

    /**
     * Actualiza el scoreboard para reflejar el nombre falso de un jugador.
     *
     * @param player    El jugador cuyo scoreboard se está actualizando.
     * @param fakeName  El nuevo nombre falso.
     */
    public static void updateNametag(ServerPlayerEntity player, String fakeName) {
        Scoreboard scoreboard = player.getServer().getScoreboard();
        String teamName = "FakeNameGlobal"; // Un solo equipo global
        Team team = scoreboard.getTeam(teamName);

        // Si el equipo no existe, crearlo una sola vez
        if (team == null) {
            team = scoreboard.addTeam(teamName);
            team.setNameTagVisibilityRule(Team.VisibilityRule.ALWAYS);
            team.setPrefix(Text.literal(""));
            team.setSuffix(Text.literal(""));
        }

        // Asignar el nombre falso en el equipo
        team.setDisplayName(Text.literal(fakeName));

        // Remover al jugador de otros equipos
        for (Team existingTeam : scoreboard.getTeams()) {
            if (existingTeam.getPlayerList().contains(player.getEntityName()) && !existingTeam.getName().equals(teamName)) {
                scoreboard.removePlayerFromTeam(player.getEntityName(), existingTeam);
            }
        }

        // Agregar al jugador al equipo si no está ya en él
        if (!team.getPlayerList().contains(player.getEntityName())) {
            scoreboard.addPlayerToTeam(player.getEntityName(), team);
        }

        // Enviar la actualización a todos los jugadores
        for (ServerPlayerEntity otherPlayer : player.getServer().getPlayerManager().getPlayerList()) {
            otherPlayer.networkHandler.sendPacket(TeamS2CPacket.updateTeam(team, true));
        }
    }
}
