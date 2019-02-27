package be.pyrrh4.pyrcore.convert.v6;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class Pre6CustomCommandsData {

	private HashMap<String, ItemStack> items = new HashMap<String, ItemStack>();
	private HashMap<String, Location> locations = new HashMap<String, Location>();

	public HashMap<String, ItemStack> getItems() {
		return items;
	}

	public HashMap<String, Location> getLocations() {
		return locations;
	}

}
