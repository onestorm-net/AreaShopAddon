package net.onestorm.plugins.areashopaddon;

import me.wiefferink.areashop.AreaShop;
import net.onestorm.plugins.areashopaddon.command.ClearAllCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class AreaShopAddon extends JavaPlugin {

    private static final String AREA_SHOP_PLUGIN_NAME = "AreaShop";
    private static final String WORLD_EDIT_PLUGIN_NAME = "FastAsyncWorldEdit";
    private static final String WORLD_GUARD_PLUGIN_NAME = "WorldGuard";

    private AreaShop areaShop = null;

    @Override
    public void onEnable() {
        if (!Bukkit.getPluginManager().isPluginEnabled(AREA_SHOP_PLUGIN_NAME)) {
            this.getLogger().warning("Could not find AreaShop! Disabling...");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (!Bukkit.getPluginManager().isPluginEnabled(WORLD_EDIT_PLUGIN_NAME)) {
            this.getLogger().warning("Could not find FastAsyncWorldEdit! Disabling...");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (!Bukkit.getPluginManager().isPluginEnabled(WORLD_GUARD_PLUGIN_NAME)) {
            this.getLogger().warning("Could not find WorldGuard! Disabling...");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        areaShop = AreaShop.getInstance();
        getServer().getCommandMap().register(getName(), new ClearAllCommand(this));

    }

    public AreaShop getAreaShop() {
        return areaShop;
    }
}
