package org.TNTStudios.fakenameportfabric.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void onRenderLabelIfPresent(AbstractClientPlayerEntity player, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        // Verifica si el jugador es el objetivo
        if (player.getEntityName().equals("NombreOriginal")) {
            // Cambia el nombre que se muestra
            Text newName = Text.literal("Paco");
            // Llama al método original con el nuevo nombre
            player.renderLabelIfPresent(newName, matrices, vertexConsumers, light);
            // Cancela la ejecución posterior del método original
            ci.cancel();
        }
    }
}
