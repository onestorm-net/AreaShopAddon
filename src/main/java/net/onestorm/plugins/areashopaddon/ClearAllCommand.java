package net.onestorm.plugins.areashopaddon;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.wiefferink.areashop.commands.CommandAreaShop;
import me.wiefferink.areashop.managers.IFileManager;
import me.wiefferink.areashop.regions.RentRegion;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.logging.Level;

public class ClearAllCommand extends CommandAreaShop {

    private static final String COMMAND_START = "areashop clearall";

    private final AreaShopAddon addon;
    private final IFileManager fileManager;

    public ClearAllCommand(AreaShopAddon addon) {
        this.addon = addon;
        this.fileManager = addon.getAreaShop().getFileManager();
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
            addon.message(sender, "clearall-noPermission");
            return;
        }

        Collection<RentRegion> rentRegions = fileManager.getRents();

        if (rentRegions.isEmpty()) {
            addon.message(sender, "clearall-noRentRegions");
            return;
        }

        int size = rentRegions.size();
        int counter = 0;

        for (RentRegion rentRegion : fileManager.getRents()) {
            if (rentRegion.isRented()) {
                // Sadly no normal unRent method, it is expected that this method is run by a command
                rentRegion.unRent(false, Bukkit.getConsoleSender());
            }

            // Remove blocks.
            // WorldGuard
            ProtectedRegion worldGuardRegion = rentRegion.getRegion();

            // WorldEdit
            World worldEditWorld = new BukkitWorld(rentRegion.getWorld());
            Polygonal2DRegion worldEditRegion = new Polygonal2DRegion(worldEditWorld,
                    worldGuardRegion.getPoints(),
                    worldGuardRegion.getMinimumPoint().y(),
                    worldGuardRegion.getMaximumPoint().y());

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(worldEditWorld))
            {
                editSession.setBlocks((Region) worldEditRegion, BlockTypes.AIR);
            } catch (MaxChangedBlocksException e) {
                addon.getLogger().log(Level.WARNING, "Region is to big to clear: " + worldGuardRegion.getId(), e);
            } finally {
                counter++;
                if (size == counter) {
                    addon.message(sender, "clearall-success");
                }
            }
        }
    }
}
