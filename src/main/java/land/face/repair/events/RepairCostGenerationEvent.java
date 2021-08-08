package land.face.repair.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class RepairCostGenerationEvent extends Event {

  private static final HandlerList HANDLER_LIST = new HandlerList();

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }

  @Getter
  private final Player player;
  @Getter
  @Setter
  private double cost;
  @Getter
  private final ItemStack itemStack;

  public RepairCostGenerationEvent(Player player, ItemStack itemStack, double cost) {
    this.player = player;
    this.cost = cost;
    this.itemStack = itemStack.clone();
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLER_LIST;
  }

}
