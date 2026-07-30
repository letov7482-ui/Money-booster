package me.midpoint.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "midpoint")
public class ModConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    public boolean antiBanEnabled = true;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart
    public int antiBanIntensity = 5;

    @ConfigEntry.Gui.Tooltip
    public boolean antiBanJump = true;

    @ConfigEntry.Gui.Tooltip
    public boolean antiBanSneak = true;

    @ConfigEntry.Gui.Tooltip
    public boolean antiBanLook = true;

    @ConfigEntry.Gui.Tooltip
    public boolean killAuraEnabled = true;

    @ConfigEntry.Gui.Tooltip
    public KillAuraMode killAuraMode = KillAuraMode.ALL;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart
    public int killAuraRange = 4;

    @ConfigEntry.Gui.Tooltip
    public int killAuraDelayMin = 100;

    @ConfigEntry.Gui.Tooltip
    public int killAuraDelayMax = 250;

    @ConfigEntry.Gui.Tooltip
    public int missChance = 5;

    @ConfigEntry.Gui.Tooltip
    public boolean autoRotate = true;

    @ConfigEntry.Gui.Tooltip
    public boolean randomTarget = true;

    @ConfigEntry.Gui.Tooltip
    public boolean autoMinerEnabled = true;

    @ConfigEntry.Gui.Tooltip
    public int miningRadius = 6;

    @ConfigEntry.Gui.Tooltip
    public int maxTunnelLength = 30;

    @ConfigEntry.Gui.Tooltip
    public boolean mineDiamond = true;

    @ConfigEntry.Gui.Tooltip
    public boolean mineNetherite = true;

    @ConfigEntry.Gui.Tooltip
    public boolean mineGold = true;

    @ConfigEntry.Gui.Tooltip
    public boolean mineEmerald = true;

    @ConfigEntry.Gui.Tooltip
    public boolean mineIron = true;

    @ConfigEntry.Gui.Tooltip
    public boolean eventHelperEnabled = true;

    @ConfigEntry.Gui.Tooltip
    public boolean showMystic = true;

    @ConfigEntry.Gui.Tooltip
    public boolean showEvents = true;

    @ConfigEntry.Gui.Tooltip
    public boolean showDistance = true;

    @ConfigEntry.Gui.Tooltip
    public int eventHelperRange = 30;

    public enum KillAuraMode {
        COMBO, CRITS, ALL
    }
}
