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
        LOGGER.info("📡 Enviando FakeName '{}' a todos los jugadores por {}", fakeName, player.getEntityName());

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(player.getEntityName());
        buf.writeString(fakeName);

        for (ServerPlayerEntity otherPlayer : player.getServer().getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(otherPlayer, FAKE_NAME_PACKET_ID, buf);
        }

        updateNametag(player, fakeName);
    }

    private static void updateNametag(ServerPlayerEntity player, String fakeName) {
        LOGGER.info("🏷️ Actualizando NameTag para {} -> {}", player.getEntityName(), fakeName);

        Scoreboard scoreboard = player.getServer().getScoreboard();
        Team team = scoreboard.getTeam(fakeName);

        if (team == null) {
            LOGGER.info("📌 Creando equipo para {}", fakeName);
            team = scoreboard.addTeam(fakeName);
        }

        team.setDisplayName(Text.literal(fakeName));
        team.setPrefix(Text.literal("§e[Fake]§r "));
        team.setNameTagVisibilityRule(Team.VisibilityRule.ALWAYS);
        scoreboard.addPlayerToTeam(player.getEntityName(), team);

        for (ServerPlayerEntity otherPlayer : player.getServer().getPlayerManager().getPlayerList()) {
            LOGGER.debug("🔄 Enviando actualización del equipo a {}", otherPlayer.getEntityName());
            otherPlayer.networkHandler.sendPacket(TeamS2CPacket.updateTeam(team, true));
        }
    }
}
