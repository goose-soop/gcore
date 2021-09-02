package com.guillaumevdn.gcore.lib.gui.internal.vanilla;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.concurrency.RWHashMap;
import com.guillaumevdn.gcore.lib.gui.internal.Handler;
import com.guillaumevdn.gcore.lib.gui.struct.GUI;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public class VanillaHandler extends Handler {

	private List<Inventory> pages = new ArrayList<>();
	private VanillaEvents events = new VanillaEvents(this);

	public VanillaHandler(GUI gui) {
		super(gui);
	}

	// ----- activation

	@Override
	public void activate() {
		Bukkit.getPluginManager().registerEvents(events, getGUI().getPlugin());	
	}

	@Override
	public void deactivate() {
		HandlerList.unregisterAll(events);
	}

	// ----- get

	@Override
	public RWHashMap<Player, Integer> getViewers() {
		RWHashMap<Player, Integer> viewers = new RWHashMap<>(10, 1f);
		for (int i = 0; i < pages.size(); ++i) {  // ConcurrentModificationException ?
			for (HumanEntity pl : pages.get(i).getViewers()) {
				Player player = ObjectUtils.castOrNull(pl, Player.class);
				if (player != null) {
					viewers.put(player, i);
				}
			}
		}
		return viewers;
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

	// ----- set

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
		pages.forEach(Inventory::clear);
		pages.clear();
	}

	// ----- do

	@Override
	public void createPage() {
		pages.add(getGUI().getType().createVanilla(getGUI().getName()));
	}

	@Override
	public void openPage(Player player, int pageIndex) {
		getGUI().getPlugin().operateSync(() -> {
			player.openInventory(getPage(pageIndex));
		});
	}

	@Override
	public void close(Player player) {
		if (pages.contains(player.getOpenInventory().getTopInventory())) {
			getGUI().getPlugin().operateSync(() -> {
				player.closeInventory();
			});
		}
	}

	// ----- click

	private boolean switchingPage = false;

	@Override
	protected void beforeSwitchPage() {
		switchingPage = true;
	}

	@Override
	public void onClose(Player player) {
		if (switchingPage) {  // when switching pages, we close the old one ; don't trigger 'onClose'
			switchingPage = false;
			return;
		}
		super.onClose(player);
	}

}
