package land.face.repair.managers;

import java.util.ArrayList;
import java.util.List;
import land.face.repair.menus.FilterGuiMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class RepairGuiManager {

  private List<FilterGuiMenu> openMenus;

  public RepairGuiManager() {
    this.openMenus = new ArrayList<>();
  }

  public List<FilterGuiMenu> getOpenMenus() {
    return openMenus;
  }

  public FilterGuiMenu getFilterMenu(Inventory inventory) {
    if (inventory == null) {
      return null;
    }
    for (FilterGuiMenu filterGuiMenu : openMenus) {
      if (filterGuiMenu.getInventory().equals(inventory)) {
        return filterGuiMenu;
      }
    }
    return null;
  }

  public void open(Player player) {
    FilterGuiMenu menu = new FilterGuiMenu(player);
    openMenus.add(menu);
  }

  public void close(Inventory inventory) {
    if (inventory == null || !(inventory.getHolder() instanceof Player)) {
      return;
    }
    FilterGuiMenu menu = getFilterMenu(inventory);
    if (menu != null) {
      ((Player) inventory.getHolder()).closeInventory();
      openMenus.remove(menu);
    }
  }

  public void closeAllMenus() {
    for (FilterGuiMenu menu : openMenus) {
      close(menu.getInventory());
    }
    openMenus.clear();
  }
}
