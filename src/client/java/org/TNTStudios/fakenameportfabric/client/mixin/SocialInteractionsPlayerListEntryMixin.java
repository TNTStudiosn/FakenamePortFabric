package org.TNTStudios.fakenameportfabric.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsPlayerListEntry;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.TNTStudios.fakenameportfabric.client.ClientFakeName;

import java.util.UUID;

@Mixin(SocialInteractionsPlayerListEntry.class)
public abstract class SocialInteractionsPlayerListEntryMixin {

    @Shadow
    private UUID uuid;

    @Shadow
    private String name;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void modifyPlayerListEntry(MinecraftClient client, UUID uuid, String name, CallbackInfo ci) {
        this.name = getFakeName(client, uuid, name);
    }

    private String getFakeName(MinecraftClient client, UUID uuid, String realName) {
        if (client.getNetworkHandler() == null) return realName;

        PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(uuid);
        if (entry != null) {
            String originalName = entry.getProfile().getName(); // Obtener nombre real
            String fakeName = ClientFakeName.getFakeName(originalName); // Usar el nuevo método que acepta String

            if (!fakeName.equals(originalName)) {
                return fakeName;
            }
        }

        return realName;
    }
}
