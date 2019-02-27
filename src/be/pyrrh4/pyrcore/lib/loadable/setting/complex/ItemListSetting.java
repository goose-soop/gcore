package be.pyrrh4.pyrcore.lib.loadable.setting.complex;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import be.pyrrh4.pyrcore.lib.loadable.Loadable;
import be.pyrrh4.pyrcore.lib.loadable.LoadableListSetting;
import be.pyrrh4.pyrcore.lib.material.Mat;

public class ItemListSetting extends LoadableListSetting<ItemSetting> {

	// base
	public ItemListSetting(Loadable<?> parent, String id, boolean mandatory, Mat icon, List<String> description) {
		super(parent, id, mandatory, icon, description);
	}

	// methods
	public boolean isEmpty() {
		return list().isEmpty();
	}

	public boolean isValid(Player player, Player parsingPlayer) {
		// iterate through items
		for (ItemSetting item : list().values()) {
			if (!item.isValid(player, parsingPlayer)) {
				return false;
			}
		}
		// valid
		return true;
	}

	public void remove(Player player, Player parsingPlayer, boolean force) {
		for (ItemSetting item : list().values()) {
			item.remove(player, parsingPlayer, force);
		}
	}

	public List<Item> drop(Location location, Player parsingPlayer) {
		List<Item> drops = new ArrayList<Item>();
		for (ItemSetting item : list().values()) {
			drops.add(item.drop(location, parsingPlayer));
		}
		return drops;
	}

	public void give(Player player) {
		for (ItemSetting item : list().values()) {
			ItemStack stack = item.getItem(player).getItemStack();
			if (player.getInventory().firstEmpty() == -1) {
				player.getWorld().dropItem(player.getEyeLocation(), stack);
			} else {
				player.getInventory().addItem(stack);
				player.updateInventory();
			}
		}
	}

	// overriden methods
	@Override
	protected ItemSetting instantiate(Loadable<?> parent, String id, boolean mandatory, Mat icon, List<String> description) {
		return new ItemSetting(parent, id, mandatory, icon, description);
	}

}
