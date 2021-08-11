package land.face.repair.commands;

import static com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils.sendMessage;

import com.tealcube.minecraft.bukkit.shade.acf.BaseCommand;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.CommandAlias;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.CommandPermission;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.Subcommand;
import com.tealcube.minecraft.bukkit.shade.acf.bukkit.contexts.OnlinePlayer;
import land.face.repair.EconRepairPlugin;
import org.bukkit.command.CommandSender;

@CommandAlias("repair")
public class RepairCommand extends BaseCommand {

  private final EconRepairPlugin plugin;

  public RepairCommand(EconRepairPlugin plugin) {
    this.plugin = plugin;
  }

  @Subcommand("reload")
  @CommandPermission("repair.reload")
  public void reloadCommand(CommandSender sender) {
    plugin.onDisable();
    plugin.onEnable();
    sendMessage(sender, plugin.getSettings().getString("config.language.command.reload",
        "&aReloaded!"));
  }

  @Subcommand("menu")
  @CommandPermission("repair.menu")
  public void menuCommand(CommandSender sender, OnlinePlayer player) {
    plugin.getRepairGuiManager().open(player.getPlayer(), null);
    sendMessage(sender, plugin.getSettings().getString("config.language.command.force-menu",
        "&aopened menu for {n}").replace("{n}", player.getPlayer().getDisplayName()));
  }
}
