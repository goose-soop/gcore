package com.guillaumevdn.gcore.lib.gui.internal.vanilla;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.gui.internal.Handler;
import com.guillaumevdn.gcore.lib.gui.struct.GUI;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.wrapper.WrapperInteger;

/**
 * @author GuillaumeVDN
 */
public class VanillaHandler extends Handler {

	private List<Inventory> pages = new ArrayList<>();
	private VanillaEvents events = new VanillaEvents(this);

	public VanillaHandler(GUI gui) {
		super(gui);
	}

	// activation
	@Override
	public void activate() {
		Bukkit.getPluginManager().registerEvents(events, getGUI().getPlugin());	
	}

	@Override
	public void deactivate() {
		HandlerList.unregisterAll(events);
	}

	// get
	@Override
	public Map<Player, Integer> getViewers() {
		WrapperInteger index = WrapperInteger.of(-1);
		return CollectionUtils.asMap(map -> pages.forEach(page -> {
			int pageIndex = index.alter(1);
			page.getViewers().forEach(pl -> {
				Player player = ObjectUtils.castOrNull(pl, Player.class);
				if (player != null) {
					map.put(player, pageIndex);
				}
			});
		}));
	}

	@Override
	public int getViewerPage(Player player) {
		for (int i = 0; i < pages.size(); ++i) {
			if (pages.get(i).getViewers().contains(player)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getPageCount() {
		return pages.size();
	}

	private Inventory getPage(int index) {
		return index < 0 || index >= pages.size() ? null : pages.get(index);
	}

	@Override
	public int firstEmpty(int pageIndex) {
		return getPage(pageIndex).firstEmpty();
	}

	@Override
	public ItemStack getPageItem(int pageIndex, int slot) {
		return getPage(pageIndex).getItem(slot);
	}

	int pageIndexOf(Inventory inventory) {
		return pages.indexOf(inventory);
	}

	// set
	@Override
	public void setPageItem(int pageIndex, int slot, ItemStack item) {
		getPage(pageIndex).setItem(slot, item);
	}

	@Override
	public void clearPageItem(int pageIndex, int slot) {
		getPage(pageIndex).clear(slot);
	}

	@Override
	public void clearPage(int pageIndex) {
		pages.remove(pageIndex).clear();
	}

	@Override
	public void clear() {
		CollectionUtils.clearForEach(pages, Inventory::clear);
		pages.clear();
	}

	// do
	@Override
	public void createPage() {
		pages.add(getGUI().getType().createVanilla(getGUI().getName()));
	}

	@Override
	public void openPage(Player player, int pageIndex) {
		player.openInventory(getPage(pageIndex));
	}

	@Override
	public void close(Player player) {
		if (player.getOpenInventory() != null && pages.contains(player.getOpenInventory().getTopInventory())) {
			player.closeInventory();
		}
	}

}
