package org.TNTStudios.fakenameportfabric.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.TNTStudios.fakenameportfabric.FakeName;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    public void modifyDisplayName(CallbackInfoReturnable<Text> cir) {
        if ((Object) this instanceof ServerPlayerEntity player) {
            String fakeName = FakeName.getFakeName(player);
            if (!fakeName.equals(player.getEntityName())) { // Solo cambiar si hay un nombre falso
                cir.setReturnValue(Text.literal(fakeName));
            }
        }
    }

    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    public void modifyName(CallbackInfoReturnable<Text> cir) {
        if ((Object) this instanceof ServerPlayerEntity player) {
            String fakeName = FakeName.getFakeName(player);
            if (fakeName != null && !fakeName.equals(player.getEntityName())) {
                cir.setReturnValue(Text.literal(fakeName));
            }
        }
    }

}
