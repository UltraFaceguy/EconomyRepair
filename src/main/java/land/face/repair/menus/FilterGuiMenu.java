package land.face.repair.menus;

import io.pixeloutlaw.minecraft.spigot.garbage.StringExtensionsKt;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import land.face.repair.data.RepairIcon;
import land.face.repair.events.RepairCostGenerationEvent;
import land.face.repair.managers.RepairGuiManager;
import land.face.repair.tasks.ItemDisplayTask;
import land.face.repair.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FilterGuiMenu {

  private static final Map<String, ItemDisplayTask> itemDisplays = new HashMap<>();

  private final RepairGuiManager repairGuiManager;
  private final Player owner;
  private final Location anvilLocation;

  private final List<RepairIcon> repairIcons = new ArrayList<>();
  private Inventory inventory;

  public static final DecimalFormat FORMAT = new DecimalFormat("###,###,###");
  private final static String MENU_NAME = ChatColor.DARK_GRAY + "Click To Repair!";

  public FilterGuiMenu(RepairGuiManager repairGuiManager, Player owner, Location anvilLocation) {
    this.repairGuiManager = repairGuiManager;
    this.owner = owner;
    this.anvilLocation = anvilLocation;
    if (anvilLocation != null && !itemDisplays.containsKey(anvilLocation.toString())) {
      itemDisplays.put(anvilLocation.toString(), null);
    }
    buildInventory(owner);
    openInventory(owner);
  }

  public RepairIcon getRepairIcon(int i) {
    if (i >= repairIcons.size()) {
      return null;
    }
    return repairIcons.get(i);
  }

  private void buildInventory(Player owner) {
    repairIcons.clear();
    for (ItemStack stack : owner.getInventory().getContents()) {
      if (stack == null || stack.getType() == Material.AIR) {
        continue;
      }
      if (ItemUtil.canBeRepaired(stack)) {
        repairIcons.add(buildRepairIcon(owner, stack));
      }
    }
    int size = 9 * (int) Math.ceil((double) repairIcons.size() / 9);
    inventory = Bukkit.createInventory(owner, Math.max(9, size), MENU_NAME);
    inventory.clear();
    for (int i = 0; i <= repairIcons.size() - 1; i++) {
      inventory.setItem(i, repairIcons.get(i));
    }
  }

  private void openInventory(Player player) {
    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_STEP, 1.0f, 1f);
    player.openInventory(inventory);
  }

  private RepairIcon buildRepairIcon(Player player, ItemStack stack) {
    double itemLevel = ItemUtil.getItemLevel(stack);
    double cost = ((repairGuiManager.getBaseCost() + itemLevel * repairGuiManager.getCostPerLevel() +
        Math.pow(itemLevel, repairGuiManager.getCostExponent())) * ItemUtil.getPercent(stack));

    double repairMult = 1;
    for (String loreLine : ItemStackExtensionsKt.getLore(stack)) {
      if (!loreLine.contains(" Repair Cost")) {
        continue;
      }
      String strippedLore = ChatColor.stripColor(loreLine);
      double value = Double.parseDouble(strippedLore.replaceAll("[^0-9.-]", ""));
      repairMult += value / 100;
    }
    cost *= Math.min(0, repairMult);

    RepairCostGenerationEvent costEvent = new RepairCostGenerationEvent(player, stack, cost);
    Bukkit.getPluginManager().callEvent(costEvent);

    cost = costEvent.getCost();

    ItemStack displayStack = stack.clone();
    List<String> lore = new ArrayList<>(ItemStackExtensionsKt.getLore(displayStack));
    lore.add("");
    lore.add(StringExtensionsKt.chatColorize("&6&lRepair Cost: &e" + FORMAT.format(cost) + "◎"));
    ItemStackExtensionsKt.setLore(displayStack, lore);
    return new RepairIcon(displayStack, stack, (int) cost);
  }

  public Inventory getInventory() {
    return inventory;
  }

  public Player getOwner() {
    return owner;
  }

  public void displayItem(ItemStack stack) {
    if (anvilLocation == null) {
      return;
    }
    if (!itemDisplays.containsKey(anvilLocation.toString())) {
      return;
    }
    ItemDisplayTask task = itemDisplays.get(anvilLocation.toString());
    if (task == null || task.isCancelled()) {
      task = new ItemDisplayTask(anvilLocation.clone().add(0.5, 1.9, 0.5));
      itemDisplays.put(anvilLocation.toString(), task);
    }
    task.displayItem(stack);
  }

}
