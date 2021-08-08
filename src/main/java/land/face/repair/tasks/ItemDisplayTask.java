package land.face.repair.tasks;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.Objects;
import land.face.repair.EconRepairPlugin;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ItemDisplayTask extends BukkitRunnable {

  private final Hologram hologram;
  private final Location location;
  private int life;

  public ItemDisplayTask(Location location) {
    this.location = location;
    life = 10;
    hologram = HologramsAPI.createHologram(EconRepairPlugin.getInstance(), location);
    hologram.clearLines();
    runTaskTimer(EconRepairPlugin.getInstance(), 0L, 4L);
  }

  @Override
  public void run() {
    life--;
    if (life == 0) {
      cancel();
      hologram.delete();
    }
  }

  public void displayItem(ItemStack stack) {
    hologram.clearLines();
    hologram.appendTextLine(ItemStackExtensionsKt.getDisplayName(stack));
    hologram.appendItemLine(stack);
    Objects.requireNonNull(location.getWorld())
        .spawnParticle(Particle.CRIT, location.clone().add(0, -0.5, 0), 10, 0, 0, 0, 0.4);
    life = 10;
  }

}
