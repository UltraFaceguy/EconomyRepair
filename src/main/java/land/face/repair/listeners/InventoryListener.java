package land.face.repair.listeners;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import io.pixeloutlaw.minecraft.spigot.garbage.StringExtensionsKt;
import java.util.ArrayList;
import java.util.List;
import land.face.repair.EconRepairPlugin;
import land.face.repair.data.RepairIcon;
import land.face.repair.menus.FilterGuiMenu;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryListener implements Listener {

  private final EconRepairPlugin plugin;
  private final String tooPoorMessage;
  private final String repairMessage;
  private final String remainingMessage;
  private final String bankRemainingMessage;
  private final String alreadyRepaired;

  public InventoryListener(EconRepairPlugin plugin) {
    this.plugin = plugin;
    tooPoorMessage = StringExtensionsKt.chatColorize(
        plugin.getSettings().getString("config.language.not-enough-money-message", "&cUr 2 po0r"));
    repairMessage = StringExtensionsKt.chatColorize(
        plugin.getSettings().getString("config.language.repair-message", "&eFixed for {a}"));
    remainingMessage = StringExtensionsKt.chatColorize(
        plugin.getSettings().getString("config.language.money-remaining-message", "&eMoney left: {a}"));
    bankRemainingMessage = StringExtensionsKt.chatColorize(
        plugin.getSettings().getString("config.language.bank-remaining-message", "&eBank left {a}"));
    alreadyRepaired = StringExtensionsKt.chatColorize(
        plugin.getSettings().getString("config.language.already-repaired-message", "&cAlready fixed!"));
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onInventoryClick(InventoryClickEvent e) {
    if (e.isCancelled()) {
      return;
    }

    FilterGuiMenu menu = plugin.getRepairGuiManager()
        .getFilterMenu(e.getWhoClicked().getOpenInventory().getTopInventory());

    if (menu == null) {
      return;
    }

    e.setCancelled(true);

    if (e.getClickedInventory() != e.getWhoClicked().getOpenInventory().getTopInventory()) {
      return;
    }

    RepairIcon repairIcon = menu.getRepairIcon(e.getSlot());
    if (repairIcon == null) {
      return;
    }

    if (repairIcon.isRepaired()) {
      MessageUtils.sendMessage(e.getWhoClicked(), alreadyRepaired);
      e.getWhoClicked().getLocation().getWorld()
          .playSound(e.getWhoClicked().getLocation(), Sound.BLOCK_ANVIL_PLACE, 1f, 1f);
      return;
    }

    Player player = (Player) e.getWhoClicked();
    String uuidString = player.getUniqueId().toString();
    Economy economy = plugin.getEconomy();
    double balance = economy.getBalance(player);

    boolean bankSub = false;
    if (balance >= repairIcon.getRepairCost()) {
      economy.withdrawPlayer((Player) e.getWhoClicked(), repairIcon.getRepairCost());
    } else if (economy.bankHas(player.getUniqueId().toString(), repairIcon.getRepairCost() - balance).transactionSuccess()) {
      bankSub = true;
      if (balance < repairIcon.getRepairCost()) {
        economy.withdrawPlayer(player, balance);
        economy.bankWithdraw(uuidString, repairIcon.getRepairCost() - balance);
      } else {
        economy.bankWithdraw(uuidString, repairIcon.getRepairCost());
      }
    } else {
      MessageUtils.sendMessage(e.getWhoClicked(), tooPoorMessage);
      e.getWhoClicked().getLocation().getWorld().playSound(e.getWhoClicked().getLocation(),
          Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF, 1f, 0.5f);
      return;
    }
    MessageUtils.sendMessage(e.getWhoClicked(), repairMessage.replace("{a}",
        FilterGuiMenu.FORMAT.format(repairIcon.getRepairCost())));
    MessageUtils.sendMessage(e.getWhoClicked(), remainingMessage.replace("{a}",
        FilterGuiMenu.FORMAT.format(economy.getBalance(player))));
    if (bankSub) {
      MessageUtils.sendMessage(e.getWhoClicked(), bankRemainingMessage.replace("{a}",
          FilterGuiMenu.FORMAT.format(economy.bankBalance(uuidString).balance)));
    }
    e.getWhoClicked().getLocation().getWorld()
        .playSound(e.getWhoClicked().getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1f);
    repairIcon.getTargetStack().setDurability((short) 0);
    setPurchased(menu, e.getSlot(), repairIcon);
    menu.displayItem(repairIcon.getTargetStack());
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent e) {
    plugin.getRepairGuiManager().close(e.getInventory());
  }

  private void setPurchased(FilterGuiMenu menu, int slot, RepairIcon repairIcon) {
    repairIcon.setDurability((short) 0);
    repairIcon.setRepaired(true);
    List<String> lore = new ArrayList<>(repairIcon.getLore());
    int line = -1;
    for (int i = 0; i <= lore.size() - 1; i++) {
      String s = ChatColor.stripColor(lore.get(i));
      if (s.startsWith("Repair Cost:")) {
        line = i;
        break;
      }
    }
    if (line != -1) {
      lore.set(line, StringExtensionsKt.chatColorize("&a&lItem Repaired!"));
    }
    repairIcon.setLore(lore);
    menu.getInventory().setItem(slot, repairIcon);
  }
}
