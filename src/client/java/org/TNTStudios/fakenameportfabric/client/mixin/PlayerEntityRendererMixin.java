package org.TNTStudios.fakenameportfabric.client.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.TNTStudios.fakenameportfabric.client.ClientFakeName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("PlayerEntityRendererMixin");

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void overrideNameTag(AbstractClientPlayerEntity player, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        ci.cancel(); // 🔹 Bloqueamos la renderización del nombre original

        String realName = player.getEntityName();
        String fakeName = ClientFakeName.getFakeName(realName);

        if (MinecraftClient.getInstance().cameraEntity != null) {
            double distance = player.squaredDistanceTo(MinecraftClient.getInstance().cameraEntity);
            if (distance > 100.0) return; // 🔹 No renderizar si está demasiado lejos
        }

        matrices.push();
        Vec3d pos = player.getPos().add(0, player.getHeight() + 0.5, 0); // 🔹 Ajustar posición sobre la cabeza
        matrices.translate(0, pos.y, 0);
        matrices.scale(-0.025F, -0.025F, 0.025F);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int width = textRenderer.getWidth(fakeName) / 2;

        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        textRenderer.draw(Text.of(fakeName), -width, 0, 0xFFFFFF, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);

        matrices.pop();
    }
}
