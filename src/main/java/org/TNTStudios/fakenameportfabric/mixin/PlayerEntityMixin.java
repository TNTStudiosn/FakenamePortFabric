package org.TNTStudios.fakenameportfabric.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.TNTStudios.fakenameportfabric.FakeName;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "onSpawn", at = @At("RETURN"))
    public void modifyTabName(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        updateScoreboardTeam(player);
    }

    private void updateScoreboardTeam(ServerPlayerEntity player) {
        String fakeName = FakeName.getFakeName(player);
        if (fakeName == null || fakeName.isEmpty()) {
            return;
        }

        Scoreboard scoreboard = player.getServer().getScoreboard();
        Team team = scoreboard.getTeam(fakeName);

        if (team == null) {
            team = scoreboard.addTeam(fakeName);
        }

        team.setDisplayName(Text.literal(fakeName));
        team.setPrefix(Text.literal("§e[Fake]§r ")); // Opcional, para diferenciar nombres falsos
        team.setNameTagVisibilityRule(Team.VisibilityRule.ALWAYS); // Asegura que el nombre sea visible

        scoreboard.addPlayerToTeam(player.getEntityName(), team);
    }
}
