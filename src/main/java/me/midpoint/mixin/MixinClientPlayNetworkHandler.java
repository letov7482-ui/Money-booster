package me.midpoint.mixin;

import me.midpoint.MidpointClient;
import me.midpoint.modules.AutoBuyerModule;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
    @Inject(method = "onGameMessage", at = @At("HEAD"))
    private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        String message = packet.content().getString();
        for (var module : MidpointClient.modules) {
            if (module instanceof AutoBuyerModule && module.enabled) {
                ((AutoBuyerModule) module).onChatMessage(message);
            }
        }
    }
}
