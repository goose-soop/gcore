package be.guillaumevdn.gcore.lib.util.input;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ItemInput {

	// methods
	public void onChoose(Player player, ItemStack value);

}
