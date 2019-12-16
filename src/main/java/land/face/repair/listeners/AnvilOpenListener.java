package land.face.repair.listeners;

import land.face.repair.EconRepairPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.AnvilInventory;

public class AnvilOpenListener implements Listener {

  private EconRepairPlugin plugin;

  public AnvilOpenListener(EconRepairPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onInventoryClick(InventoryOpenEvent e) {
    if (e.isCancelled()) {
      return;
    }
    if (e.getInventory() instanceof AnvilInventory) {
      e.setCancelled(true);
      plugin.getRepairGuiManager().open((Player) e.getPlayer());
    }
  }
}
