package land.face.repair.listeners;

import com.tealcube.minecraft.bukkit.TextUtils;
import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import info.faceland.mint.MintEconomy;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.ArrayList;
import java.util.List;
import land.face.repair.EconRepairPlugin;
import land.face.repair.data.RepairIcon;
import land.face.repair.menus.FilterGuiMenu;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.nunnerycode.mint.MintPlugin;

public class InventoryListener implements Listener {

  private EconRepairPlugin plugin;
  private String tooPoorMessage;
  private String repairMessage;
  private String remainingMessage;
  private String bankRemainingMessage;
  private String alreadyRepaired;

  public InventoryListener(EconRepairPlugin plugin) {
    this.plugin = plugin;
    tooPoorMessage = TextUtils.color(
        plugin.getSettings().getString("config.language.not-enough-money-message", "&cUr 2 po0r"));
    repairMessage = TextUtils.color(
        plugin.getSettings().getString("config.language.repair-message", "&eFixed for {a}"));
    remainingMessage = TextUtils.color(
        plugin.getSettings()
            .getString("config.language.money-remaining-message", "&eMoney left: {a}"));
    bankRemainingMessage = TextUtils.color(
        plugin.getSettings()
            .getString("config.language.bank-remaining-message", "&eBank left {a}"));
    alreadyRepaired = TextUtils.color(
        plugin.getSettings()
            .getString("config.language.already-repaired-message", "&cAlready fixed!"));
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

    String uuid = e.getWhoClicked().getUniqueId().toString();
    MintEconomy economy = MintPlugin.getInstance().getEconomy();
    double balance = economy.getBalance(uuid);

    boolean bankSub = false;
    if (balance >= repairIcon.getRepairCost()) {
      economy.withdrawPlayer(uuid, repairIcon.getRepairCost());
    } else if (economy.bankHas(uuid, repairIcon.getRepairCost() - balance).transactionSuccess()) {
      bankSub = true;
      if (balance > 0) {
        economy.setBalance(uuid, 0);
        economy.bankWithdraw(uuid, repairIcon.getRepairCost() - balance);
      } else {
        economy.bankWithdraw(uuid, repairIcon.getRepairCost());
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
        FilterGuiMenu.FORMAT.format(economy.getBalance(uuid))));
    if (bankSub) {
      MessageUtils.sendMessage(e.getWhoClicked(), bankRemainingMessage.replace("{a}",
          FilterGuiMenu.FORMAT.format(economy.bankBalance(uuid).balance)));
    }
    e.getWhoClicked().getLocation().getWorld()
        .playSound(e.getWhoClicked().getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1f);
    repairIcon.getTargetStack().setDurability((short) 0);
    setPurchased(menu, e.getSlot(), repairIcon);
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent e) {
    plugin.getRepairGuiManager().close(e.getInventory());
  }

  private void setPurchased(FilterGuiMenu menu, int slot, RepairIcon repairIcon) {
    repairIcon.setDurability((short) 0);
    repairIcon.setRepaired(true);
    List<String> lore = new ArrayList<>(ItemStackExtensionsKt.getLore(repairIcon));
    int line = -1;
    for (int i = 0; i <= lore.size() - 1; i++) {
      String s = ChatColor.stripColor(lore.get(i));
      if (s.startsWith("Repair Cost:")) {
        line = i;
        break;
      }
    }
    if (line != -1) {
      lore.set(line, TextUtils.color("&a&lItem Repaired!"));
    }
    ItemStackExtensionsKt.setLore(repairIcon, lore);
    menu.getInventory().setItem(slot, repairIcon);
  }
}
