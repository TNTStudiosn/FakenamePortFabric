package org.TNTStudios.fakenameportfabric.client.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.math.Vec3d;
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
        ci.cancel(); // 🔹 Cancela el NameTag original de Minecraft

        String realName = player.getEntityName();
        String fakeName = ClientFakeName.getFakeName(realName);

        if (MinecraftClient.getInstance().cameraEntity != null) {
            double distance = player.squaredDistanceTo(MinecraftClient.getInstance().cameraEntity);
            if (distance > 100.0) return; // 🔹 No renderizar si está demasiado lejos
        }

        matrices.push();
        Vec3d pos = player.getPos().add(0, player.getHeight() + 0.5, 0); // 🔹 Posicionar encima de la cabeza
        matrices.translate(pos.x, pos.y, pos.z);
        matrices.scale(-0.025F, -0.025F, 0.025F);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int width = textRenderer.getWidth(fakeName) / 2;

        textRenderer.draw(matrices, fakeName, -width, 0, 0xFFFFFF); // 🔹 Renderizar FakeName

        matrices.pop();
    }
}
