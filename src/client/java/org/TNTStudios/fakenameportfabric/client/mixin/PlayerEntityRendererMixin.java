package org.TNTStudios.fakenameportfabric.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.TNTStudios.fakenameportfabric.client.ClientFakeName;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {

    @Shadow
    protected abstract void renderLabelIfPresent(AbstractClientPlayerEntity player, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light);

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void modifyNameTag(AbstractClientPlayerEntity player, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        String fakeName = ClientFakeName.getFakeName(player); // Se obtiene el nombre falso desde el cliente

        if (fakeName != null && !fakeName.equals(player.getEntityName())) {
            ci.cancel(); // Cancela la renderización original del nombre
            renderCustomLabel(player, Text.literal(fakeName), matrices, vertexConsumers, light);
        }
    }

    private void renderCustomLabel(AbstractClientPlayerEntity player, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.cameraEntity == null) return; // Evita NPE si la cámara aún no está inicializada

        double distance = player.squaredDistanceTo(client.cameraEntity); // Calcula la distancia de renderizado
        if (distance < 100.0) { // Solo renderizar si está cerca
            matrices.push();
            matrices.translate(0.0, player.getHeight() + 0.5, 0.0); // Posiciona el nombre sobre la cabeza
            renderLabelIfPresent(player, text, matrices, vertexConsumers, light); // Llama a la implementación original
            matrices.pop();
        }
    }
}
