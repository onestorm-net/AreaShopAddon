package net.onestorm.plugins.areashopaddon;

import me.wiefferink.areashop.AreaShop;
import me.wiefferink.areashop.libraries.interactivemessenger.processing.Message;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class AreaShopAddon extends JavaPlugin {

    private static final String AREA_SHOP_PLUGIN_NAME = "AreaShop";
    private static final String WORLD_EDIT_PLUGIN_NAME = "FastAsyncWorldEdit";
    private static final String WORLD_GUARD_PLUGIN_NAME = "WorldGuard";

    private AreaShop areaShop = null;
    private ClearAllCommand clearAllCommand = null;

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
        clearAllCommand = new ClearAllCommand(this);
        areaShop.getCommandManager().getCommands().add(clearAllCommand);
    }

    public void message(Object target, String key, Object... replacements) {
        Message.fromKey(key).prefix().replacements(replacements).send(target);
    }

    @Override
    public void onDisable() {
        if (areaShop == null) {
            return;
        }
        areaShop.getCommandManager().getCommands().remove(clearAllCommand);
    }

    public AreaShop getAreaShop() {
        return areaShop;
    }
}
