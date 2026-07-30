package me.midpoint.modules;

import net.minecraft.text.Text;

public class AutoBuyerModule extends BaseModule {
    public AutoBuyerModule() {
        super("AutoBuyer");
    }

    @Override
    public void tick() {
        if (!enabled || mc.player == null) return;
    }

    public void onChatMessage(String msg) {
        if (!enabled || !msg.contains("за") || !msg.contains("монет")) return;
        try {
            String[] parts = msg.split(" за ");
            if (parts.length < 2) return;
            String item = parts[0].trim();
            String priceStr = parts[1].replace(" монет", "").trim();
            double price = Double.parseDouble(priceStr);
            if (price < 100) {
                mc.player.sendMessage(Text.literal("/buy " + item + " " + (int) price), false);
                mc.player.sendMessage(Text.literal("/sell " + item + " " + (int) (price * 1.5)), false);
            }
        } catch (Exception ignored) {}
    }
}
