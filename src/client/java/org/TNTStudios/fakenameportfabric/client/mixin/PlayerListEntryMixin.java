package org.TNTStudios.fakenameportfabric.client.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.TNTStudios.fakenameportfabric.client.ClientFakeName;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin {
    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    public void modifyDisplayName(CallbackInfoReturnable<Text> cir) {
        GameProfile profile = ((PlayerListEntry) (Object) this).getProfile();
        String realName = profile.getName();
        String fakeName = ClientFakeName.getFakeName(realName);

        if (!fakeName.equals(realName)) {
            cir.setReturnValue(Text.literal(fakeName));
        }
    }
}
