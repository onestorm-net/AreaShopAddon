package com.shebbasoft.areashop.addon;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.wiefferink.areashop.AreaShop;
import me.wiefferink.areashop.commands.CommandAreaShop;
import me.wiefferink.areashop.managers.FileManager;
import me.wiefferink.areashop.regions.RentRegion;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ClearAllCommand extends CommandAreaShop {

    private static final String COMMAND_START = "areashop clearall";
    private static final int MAX_BLOCK_UPDATES = 1000;
    private static final int MATERIAL_AIR_ID = 0;

    private final AreaShopAddon addon;
    private final AreaShop plugin;
    private final FileManager fileManager;

    public ClearAllCommand(AreaShopAddon addon) {
        this.addon = addon;
        this.plugin = addon.getAreaShop();
        this.fileManager = plugin.getFileManager();
    }

    @Override
    public String getCommandStart() {
        return COMMAND_START;
    }

    @Override
    public String getHelp(CommandSender sender) {
        if (!sender.hasPermission("areashop.clearall")) {
            return null;
        }
        return "help-clearall";
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        if (!sender.hasPermission("areashop.clearall")) {
            plugin.message(sender, "clearall-noPermission");
            return;
        }

        for (RentRegion rentRegion : fileManager.getRents()) {
            if (rentRegion.isRented()) {
                // Sadly no normal unRent method, it is expected that this method is run by a command
                rentRegion.unRent(false, Bukkit.getConsoleSender());
            }

            // Remove blocks.
            // WorldGuard
            ProtectedRegion worldGuardRegion = rentRegion.getRegion();

            // WorldEdit
            World worldEditWorld = BukkitUtil.getLocalWorld(rentRegion.getWorld());
            Polygonal2DRegion worldEditRegion = new Polygonal2DRegion(worldEditWorld,
                    worldGuardRegion.getPoints(),
                    worldGuardRegion.getMinimumPoint().getBlockY(),
                    worldGuardRegion.getMaximumPoint().getBlockY());

            try {
                EditSession editSession = WorldEdit.getInstance().getEditSessionFactory()
                    .getEditSession(worldEditWorld, MAX_BLOCK_UPDATES);
                editSession.setBlocks(worldEditRegion, new BaseBlock(MATERIAL_AIR_ID));
            } catch (MaxChangedBlocksException e) {
                addon.getLogger().warning(worldGuardRegion.getId() + ": "  + e.getMessage());
                e.printStackTrace();
            }
        }
        plugin.message(sender, "clearall-success");
    }
}
