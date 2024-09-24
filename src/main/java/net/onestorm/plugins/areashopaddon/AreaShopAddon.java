package net.onestorm.plugins.areashopaddon;

import me.wiefferink.areashop.AreaShop;
import net.onestorm.plugins.areashopaddon.command.ClearAllCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class AreaShopAddon extends JavaPlugin {

    private AreaShop areaShop = null;

    @Override
    public void onEnable() {
        areaShop = AreaShop.getInstance();
        getServer().getCommandMap().register(getName(), new ClearAllCommand(this));
    }

    public AreaShop getAreaShop() {
        return areaShop;
    }
}
