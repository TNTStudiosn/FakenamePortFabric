package org.TNTStudios.fakenameportfabric.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.data.DataTracker;
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
            // Verificar si el TrackedData está registrado antes de acceder a él
            DataTracker tracker = player.getDataTracker();
            if (tracker.getEntry(FakeName.FAKE_NAME) == null) return;

            String fakeName = FakeName.getFakeName(player);
            if (fakeName != null && !fakeName.isEmpty()) {
                cir.setReturnValue(Text.literal(fakeName));
            }
        }
    }
}
