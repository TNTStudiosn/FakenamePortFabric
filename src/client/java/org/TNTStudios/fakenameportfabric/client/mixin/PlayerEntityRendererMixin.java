package org.TNTStudios.fakenameportfabric.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.client.render.VertexConsumerProvider;
import org.TNTStudios.fakenameportfabric.client.ClientFakeName;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void overrideNameTag(AbstractClientPlayerEntity player, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        String fakeName = ClientFakeName.getFakeName(player.getEntityName());

        if (!fakeName.equals(player.getEntityName())) {
            ci.cancel();
            renderCustomLabel(player, Text.literal(fakeName), matrices, vertexConsumers, light);
        }
    }

    private void renderCustomLabel(AbstractClientPlayerEntity player, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        // Método de renderizado manual para evitar problemas con 'super'
        double distance = MinecraftClient.getInstance().gameRenderer.getCamera().getPos().squaredDistanceTo(player.getPos());

        if (distance < 64.0) {
            float scale = 0.02666667F;
            matrices.push();
            matrices.translate(0.0, player.getHeight() + 0.5F, 0.0);
            matrices.scale(-scale, -scale, scale);
            matrices.translate(0.0, -10.0F, 0.0);
            MinecraftClient.getInstance().textRenderer.draw(
                    text,
                    -MinecraftClient.getInstance().textRenderer.getWidth(text) / 2.0F,
                    0.0F,
                    0xFFFFFF,
                    false,
                    matrices.peek().getPositionMatrix(),
                    vertexConsumers,
                    TextRenderer.TextLayerType.NORMAL, // Cambio aquí
                    0,
                    light
            );

            matrices.pop();
        }
    }
}

