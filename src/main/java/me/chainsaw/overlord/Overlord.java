package me.chainsaw.overlord;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Overlord extends JavaPlugin {

    @Override
    public void onEnable() {
        System.out.println("OVERLORD IS HERE");

        Bukkit.getPluginManager().registerEvents(new JoinSignManager(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
