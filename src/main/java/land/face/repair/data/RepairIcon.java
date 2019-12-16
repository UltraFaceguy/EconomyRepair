package land.face.repair.data;

import org.bukkit.inventory.ItemStack;

public class RepairIcon extends ItemStack {

  private ItemStack targetStack;
  private boolean repaired = false;
  private int repairCost;

  public RepairIcon(ItemStack displayStack, ItemStack targetStack, int repairCost)
      throws IllegalArgumentException {
    super(displayStack);
    this.targetStack = targetStack;
    this.repairCost = repairCost;
  }

  public ItemStack getTargetStack() {
    return targetStack;
  }

  public int getRepairCost() {
    return repairCost;
  }

  public boolean isRepaired() {
    return repaired;
  }

  public void setRepaired(boolean repaired) {
    this.repaired = repaired;
  }

}
