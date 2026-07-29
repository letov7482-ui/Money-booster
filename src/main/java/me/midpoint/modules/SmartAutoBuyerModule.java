package me.midpoint.modules;

import net.minecraft.text.Text;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmartAutoBuyerModule extends BaseModule {
    private Map<String, Deque<Double>> priceHistory = new HashMap<>();
    private Map<String, Integer> buyCount = new HashMap<>();
    private int buyLimitPerMinute = 3;
    private long lastBuyTime = 0;

    private static final Pattern PRICE_PATTERN = Pattern.compile("([\\w ]+) за (\\d+\\.?\\d*) монет");

    public SmartAutoBuyerModule() {
        super("SmartAutoBuyer", -1);
    }

    @Override
    public void tick() {
        if (!enabled || mc.player == null) return;
        // Сканирование происходит через перехват сообщений в миксине
    }

    public void onChatMessage(String message) {
        Matcher matcher = PRICE_PATTERN.matcher(message);
        if (matcher.find()) {
            String item = matcher.group(1).trim();
            double price = Double.parseDouble(matcher.group(2).replace(",", "."));
            processPrice(item, price);
        }
    }

    private void processPrice(String item, double price) {
        priceHistory.computeIfAbsent(item, k -> new ArrayDeque<>()).add(price);
        if (priceHistory.get(item).size() > 20) {
            priceHistory.get(item).poll();
        }

        double avg = priceHistory.get(item).stream().mapToDouble(Double::doubleValue).average().orElse(price);
        double discount = (avg - price) / avg * 100;

        boolean isFalling = isPriceFalling(item);

        if (discount >= 15 && !isFalling && buyCount.getOrDefault(item, 0) < buyLimitPerMinute) {
            buyItem(item, price);
        }
    }

    private boolean isPriceFalling(String item) {
        Deque<Double> history = priceHistory.get(item);
        if (history == null || history.size() < 3) return false;
        Double[] arr = history.toArray(new Double[0]);
        return arr[arr.length - 1] < arr[0];
    }

    private void buyItem(String item, double price) {
        long now = System.currentTimeMillis();
        if (now - lastBuyTime < 20000) return;
        lastBuyTime = now;

        mc.player.sendMessage(Text.literal("/buy " + item + " " + (int)price));
        buyCount.put(item, buyCount.getOrDefault(item, 0) + 1);
        System.out.println("[AutoBuyer] Купил: " + item + " за " + (int)price + " монет");

        double sellPrice = price * (1.15 + Math.random() * 0.05);
        mc.player.sendMessage(Text.literal("/sell " + item + " " + (int)sellPrice));
        System.out.println("[AutoBuyer] Выставил: " + item + " за " + (int)sellPrice + " монет");
    }
    }
