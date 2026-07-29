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
        String msg = packet.content().getString();
        for (var m : MidpointClient.modules) {
            if (m instanceof AutoBuyerModule && m.enabled) {
                ((AutoBuyerModule) m).onChatMessage(msg);
            }
        }
    }
}
