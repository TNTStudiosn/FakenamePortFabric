package org.TNTStudios.fakenameportfabric.mixin.client;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.TNTStudios.fakenameportfabric.client.ClientFakeName;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void modifyNameTag(AbstractClientPlayerEntity player, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        String fakeName = ClientFakeName.getFakeName(player); // Se obtiene desde el cliente

        if (fakeName != null && !fakeName.equals(player.getEntityName())) {
            ci.cancel(); // Cancela la renderización original
            player.setCustomName(Text.literal(fakeName)); // Cambia el nombre visible en el cliente
        }
    }
}
