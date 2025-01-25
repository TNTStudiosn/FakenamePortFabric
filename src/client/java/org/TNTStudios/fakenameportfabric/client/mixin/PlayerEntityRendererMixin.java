package org.TNTStudios.fakenameportfabric.client.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.Text;
import org.TNTStudios.fakenameportfabric.client.ClientFakeName;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void overrideNameTag(AbstractClientPlayerEntity player, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        UUID playerUUID = player.getUuid();
        String fakeName = ClientFakeName.getFakeName(playerUUID);

        if (fakeName != null && !fakeName.isEmpty()) {
            ci.cancel();
            renderCustomLabel(player, Text.literal(fakeName), matrices, vertexConsumers, light);
        }
    }

    private void renderCustomLabel(AbstractClientPlayerEntity player, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        double distance = net.minecraft.client.MinecraftClient.getInstance().gameRenderer.getCamera().getPos().squaredDistanceTo(player.getPos());

        if (distance < 64.0) {
            float scale = 0.02666667F;
            matrices.push();


            matrices.translate(0.0, player.getHeight() + 0.5F, 0.0);


            float yaw = net.minecraft.client.MinecraftClient.getInstance().gameRenderer.getCamera().getYaw();
            float pitch = net.minecraft.client.MinecraftClient.getInstance().gameRenderer.getCamera().getPitch();


            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pitch));


            matrices.scale(-scale, -scale, scale);


            net.minecraft.client.MinecraftClient.getInstance().textRenderer.draw(
                    text,
                    -net.minecraft.client.MinecraftClient.getInstance().textRenderer.getWidth(text) / 2.0F,
                    0.0F,
                    0xFFFFFF,
                    false,
                    matrices.peek().getPositionMatrix(),
                    vertexConsumers,
                    net.minecraft.client.font.TextRenderer.TextLayerType.NORMAL,
                    0,
                    light
            );

            matrices.pop();
        }
    }


}
