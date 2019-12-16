package land.face.repair.utils;

import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.math.NumberUtils;
import com.tealcube.minecraft.bukkit.shade.google.common.base.CharMatcher;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class ItemUtil {

  public static double getPercent(ItemStack stack) {
    return ((double) stack.getDurability()) / stack.getType().getMaxDurability();
  }

  public static boolean canBeRepaired(ItemStack stack) {
    if (stack.getType().getMaxDurability() < 10) {
      return false;
    }
    return stack.getDurability() > 9;
  }

  public static int getItemLevel(ItemStack stack) {
    if (stack.getItemMeta() == null) {
      return 1;
    }
    List<String> lore = ItemStackExtensionsKt.getLore(stack);
    if (lore.isEmpty()) {
      return 1;
    }
    String lvlReqString = ChatColor.stripColor(lore.get(0));
    if (!lvlReqString.startsWith("Level Requirement:")) {
      return 1;
    }
    return getDigit(lore.get(0));
  }

  public static int getDigit(String string) {
    String lev = CharMatcher.digit().or(CharMatcher.is('-')).negate()
        .collapseFrom(ChatColor.stripColor(string), ' ').trim();
    return NumberUtils.toInt(lev.split(" ")[0], 0);
  }
}
