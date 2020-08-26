package land.face.repair;

import io.pixeloutlaw.minecraft.spigot.config.MasterConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.VersionedConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.VersionedSmartYamlConfiguration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import land.face.repair.commands.BaseCommand;
import land.face.repair.listeners.AnvilOpenListener;
import land.face.repair.listeners.InventoryListener;
import land.face.repair.managers.RepairGuiManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import se.ranzdo.bukkit.methodcommand.CommandHandler;

public class EconRepairPlugin extends JavaPlugin {

  private static EconRepairPlugin instance;

  private RepairGuiManager repairGuiManager;

  private MasterConfiguration settings;
  private VersionedSmartYamlConfiguration configYAML;

  private CommandHandler commandHandler;

  public static EconRepairPlugin getInstance() {
    return instance;
  }

  public void onEnable() {
    instance = this;
    repairGuiManager = new RepairGuiManager(this);

    List<VersionedSmartYamlConfiguration> configurations = new ArrayList<>();
    configurations.add(configYAML = defaultSettingsLoad("config.yml"));

    for (VersionedSmartYamlConfiguration config : configurations) {
      if (config.update()) {
        getLogger().info("Updating " + config.getFileName());
      }
    }

    settings = MasterConfiguration.loadFromFiles(configYAML);

    Bukkit.getPluginManager().registerEvents(new InventoryListener(this), this);
    Bukkit.getPluginManager().registerEvents(new AnvilOpenListener(this), this);

    commandHandler = new CommandHandler(this);
    commandHandler.registerCommands(new BaseCommand(this));

    Bukkit.getServer().getLogger().info("EconRepair Enabled!");
  }

  public void onDisable() {
    HandlerList.unregisterAll(this);
    repairGuiManager.closeAllMenus();
    Bukkit.getServer().getLogger().info("EconRepair Disabled!");
  }

  public MasterConfiguration getSettings() {
    return settings;
  }

  public RepairGuiManager getRepairGuiManager() {
    return repairGuiManager;
  }

  private VersionedSmartYamlConfiguration defaultSettingsLoad(String name) {
    return new VersionedSmartYamlConfiguration(new File(getDataFolder(), name),
        getResource(name), VersionedConfiguration.VersionUpdateType.BACKUP_AND_UPDATE);
  }
}