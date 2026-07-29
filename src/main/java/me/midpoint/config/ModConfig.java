package me.midpoint.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "midpoint")
public class ModConfig implements ConfigData {
    
    // ===== Модули =====
    @ConfigEntry.Gui.Tooltip
    public boolean antiBanEnabled = true;
    
    @ConfigEntry.Gui.Tooltip
    public boolean autoBuyerEnabled = true;
    
    @ConfigEntry.Gui.Tooltip
    public boolean autoMinerEnabled = true;
    
    // ===== AntiBan =====
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart
    public int antiBanIntensity = 5; // 1-10
    
    // ===== AutoBuyer =====
    @ConfigEntry.Gui.Tooltip
    public int buyLimitPerMinute = 3;
    
    @ConfigEntry.Gui.Tooltip
    public double minProfitPercent = 15.0; // 15%
    
    @ConfigEntry.Gui.Tooltip
    public double sellMarkupPercent = 20.0; // 20%
    
    // ===== AutoMiner =====
    @ConfigEntry.Gui.Tooltip
    public int miningRadius = 6;
    
    @ConfigEntry.Gui.Tooltip
    public int maxTunnelLength = 35;
    
    @ConfigEntry.Gui.Tooltip
    public int miningDelayMin = 120;
    
    @ConfigEntry.Gui.Tooltip
    public int miningDelayMax = 300;
}
