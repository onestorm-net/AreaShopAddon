package net.onestorm.plugins.areashopaddon.command;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.wiefferink.areashop.managers.IFileManager;
import me.wiefferink.areashop.regions.RentRegion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.onestorm.plugins.areashopaddon.AreaShopAddon;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

public class ClearAllCommand extends BukkitCommand {

    private static final String COMMAND_NAME = "areashopclearall";
    private static final List<String> COMMAND_ALIASES = List.of("asclearall", "cellclearall");
    private static final String COMMAND_PERMISSION = "areashopaddon.command.clearall";

    private final AreaShopAddon addon;
    private final IFileManager fileManager;

    public ClearAllCommand(AreaShopAddon addon) {
        super(COMMAND_NAME);
        this.addon = addon;
        this.fileManager = addon.getAreaShop().getFileManager();
        setAliases(COMMAND_ALIASES);
        setPermission(COMMAND_PERMISSION);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] arguments) {
        handleCommand(sender);
        return true;
    }

    private void handleCommand(CommandSender sender) {
        Collection<RentRegion> rentRegions = fileManager.getRents();

        if (rentRegions.isEmpty()) {
            sender.sendMessage(Component.text("There are no (rentable) regions to clear.", NamedTextColor.RED));
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
                    sender.sendMessage(Component.text("Cleared all (rentable) regions.", NamedTextColor.GREEN));
                }
            }
        }
    }
}
