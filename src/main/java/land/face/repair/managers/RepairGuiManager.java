package land.face.repair.managers;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import land.face.repair.EconRepairPlugin;
import land.face.repair.menus.FilterGuiMenu;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class RepairGuiManager {

  private final Map<Player, FilterGuiMenu> openMenus = new WeakHashMap<>();

  private final double baseCost;
  private final double costPerLevel;
  private final double costExponent;

  public RepairGuiManager(EconRepairPlugin plugin) {
    baseCost = plugin.getSettings().getDouble("config.base-repair-cost", 5);
    costPerLevel = plugin.getSettings().getDouble("config.repair-cost-per-level", 1);
    costExponent = plugin.getSettings().getDouble("config.repair-cost-exponent", 1.5);
  }

  public Set<FilterGuiMenu> getOpenMenus() {
    return new HashSet<>(openMenus.values());
  }

  public FilterGuiMenu getFilterMenu(Inventory inventory) {
    if (inventory == null) {
      return null;
    }
    for (FilterGuiMenu filterGuiMenu : openMenus.values()) {
      if (filterGuiMenu.getInventory().equals(inventory)) {
        return filterGuiMenu;
      }
    }
    return null;
  }

  public void open(Player player, Location anvilLocation) {
    FilterGuiMenu menu = new FilterGuiMenu(this, player, anvilLocation);
    openMenus.put(player, menu);
  }

  public void close(Inventory inventory) {
    if (inventory == null) {
      return;
    }
    FilterGuiMenu menu = getFilterMenu(inventory);
    if (menu != null) {
      openMenus.remove(menu.getOwner());
    }
  }

  public void closeAllMenus() {
    for (FilterGuiMenu menu : openMenus.values()) {
      close(menu.getInventory());
    }
    openMenus.clear();
  }

  public double getBaseCost() {
    return baseCost;
  }

  public double getCostPerLevel() {
    return costPerLevel;
  }

  public double getCostExponent() {
    return costExponent;
  }
}
