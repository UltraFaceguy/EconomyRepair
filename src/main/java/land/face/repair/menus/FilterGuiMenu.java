package land.face.repair.menus;

import com.tealcube.minecraft.bukkit.TextUtils;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import land.face.repair.EconRepairPlugin;
import land.face.repair.data.RepairIcon;
import land.face.repair.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FilterGuiMenu {

  private Inventory inventory;
  private List<RepairIcon> repairIcons = new ArrayList<>();

  private double baseCost;
  private double costPerLevel;
  private double costExponent;

  public static final DecimalFormat FORMAT = new DecimalFormat("###,###,###");
  private final static String MENU_NAME = ChatColor.DARK_GRAY + "Click To Repair!";

  public FilterGuiMenu(Player owner) {
    baseCost = EconRepairPlugin.getInstance().getSettings()
        .getDouble("config.base-repair-cost", 5);
    costPerLevel = EconRepairPlugin.getInstance().getSettings()
        .getDouble("config.repair-cost-per-level", 1);
    costExponent = EconRepairPlugin.getInstance().getSettings()
        .getDouble("config.repair-cost-exponent", 1.5);
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
        repairIcons.add(buildRepairIcon(stack));
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

  public Inventory getInventory() {
    return inventory;
  }

  private RepairIcon buildRepairIcon(ItemStack stack) {
    double itemLevel = ItemUtil.getItemLevel(stack);
    int cost = (int) ((baseCost + itemLevel * costPerLevel +
        Math.pow(itemLevel, costExponent)) * ItemUtil.getPercent(stack));
    ItemStack displayStack = stack.clone();
    List<String> lore = new ArrayList<>(ItemStackExtensionsKt.getLore(displayStack));
    lore.add("");
    lore.add(TextUtils.color("&6&lRepair Cost: &f&l" + FORMAT.format(cost) + " Bits"));
    ItemStackExtensionsKt.setLore(displayStack, lore);
    return new RepairIcon(displayStack, stack, cost);
  }
}
