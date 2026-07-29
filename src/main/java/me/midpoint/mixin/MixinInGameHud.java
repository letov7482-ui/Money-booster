package me.midpoint.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {
    @Inject(method = "render", at = @At("RETURN"))
    private void onRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        // Водяной знак (просто для проверки, что мод работает)
        context.drawText(
            net.minecraft.client.MinecraftClient.getInstance().textRenderer,
            Text.literal("§5Midpoint God Mode"),
            5, 5, 0xAA00FF, true
        );
    }
}
