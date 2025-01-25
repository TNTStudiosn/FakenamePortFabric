package org.TNTStudios.fakenameportfabric.client.mixin;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("PlayerEntityRendererMixin");

    @Shadow
    protected abstract void renderLabelIfPresent(AbstractClientPlayerEntity player, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light);

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void modifyNameTag(AbstractClientPlayerEntity player, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        String realName = player.getEntityName();  // 🔹 Obtener el nombre real
        String fakeName = ClientFakeName.getFakeName(realName); // 🔹 Obtener el FakeName

        if (!fakeName.equals(realName)) {
            LOGGER.info("[FakeName] Cambiando NameTag de {} -> {}", realName, fakeName);
            ci.cancel();
            renderLabelIfPresent(player, Text.literal(fakeName), matrices, vertexConsumers, light);
        }
    }
}
