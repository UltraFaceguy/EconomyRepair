package land.face.repair.commands;

import static com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils.sendMessage;

import land.face.repair.EconRepairPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.ranzdo.bukkit.methodcommand.Arg;
import se.ranzdo.bukkit.methodcommand.Command;

public class BaseCommand {

  private EconRepairPlugin plugin;

  public BaseCommand(EconRepairPlugin plugin) {
    this.plugin = plugin;
  }

  @Command(identifier = "repair reload", permissions = "econrepair.reload", onlyPlayers = false)
  public void reloadCommand(CommandSender sender) {
    plugin.onDisable();
    plugin.onEnable();
    sendMessage(sender, plugin.getSettings().getString("config.language.command.reload",
        "&aReloaded!"));
  }

  @Command(identifier = "repair menu", permissions = "econrepair.menu", onlyPlayers = false)
  public void menuCommand(CommandSender sender, @Arg(name = "target") Player target) {
    plugin.getRepairGuiManager().open(target, null);
    sendMessage(sender, plugin.getSettings().getString("config.language.command.force-menu",
        "&aopened menu for {n}").replace("{n}", target.getDisplayName()));
  }
}
