package com.guillaumevdn.gcore.lib.gui.internal.protocol;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.ProtocolLibrary;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.concurrency.RWArrayList;
import com.guillaumevdn.gcore.lib.concurrency.RWHashMap;
import com.guillaumevdn.gcore.lib.concurrency.RWHashSet;
import com.guillaumevdn.gcore.lib.gui.internal.Handler;
import com.guillaumevdn.gcore.lib.gui.struct.GUI;
import com.guillaumevdn.gcore.lib.wrapper.WrapperBoolean;

/**
 * @author GuillaumeVDN
 */
public class ProtocolHandler extends Handler {

	public static final int WINDOW_ID = 72;  // signed byte ; don't go above 128
	private RWArrayList<Window> pages = new RWArrayList<>(5);
	private ProtocolEvents events = new ProtocolEvents(this);

	public ProtocolHandler(GUI gui) {
		super(gui);
	}

	// ----- activation

	@Override
	public void activate() {
		ProtocolLibrary.getProtocolManager().addPacketListener(events);
		Bukkit.getPluginManager().registerEvents(events, getGUI().getPlugin());
	}

	@Override
	public void deactivate() {
		ProtocolLibrary.getProtocolManager().removePacketListener(events);
		HandlerList.unregisterAll(events);
	}

	// ----- events

	@Override
	public void onClose(Player player) {
		super.onClose(player);
		removeViewer(player);
	}

	// -----

	@Override
	public RWHashMap<Player, Integer> getViewers() {
		RWHashMap<Player, Integer> viewers = new RWHashMap<>(10, 1f);
		pages.forEach(page -> {
			page.getViewers().forEach(player -> viewers.put(player, page.getIndex()));
		});
		return viewers;
	}

	@Override
	public int getViewerPage(Player player) {
		Window page = getPage(player);
		return page != null ? page.getIndex() : -1;
	}

	@Override
	public int getPageCount() {
		return pages.size();
	}

	private Window getPage(int index) {
		return index < 0 || index >= pages.size() ? null : pages.get(index);
	}

	Window getPage(Player player) {
		return pages.streamResult(s -> s.filter(page -> page.getViewers().contains(player)).findFirst().orElse(null));
	}

	public boolean removeViewer(Player player) {
		WrapperBoolean removed = WrapperBoolean.of(false);
		pages.forEach(page -> {
			if (page.getViewers().remove(player)) {
				removed.set(true);
			}
		});
		return removed.get();
	}

	@Override
	public int firstEmpty(int pageIndex) {
		return getPage(pageIndex).firstEmpty();
	}

	@Override
	public ItemStack getPageItem(int pageIndex, int slot) {
		Window page = getPage(pageIndex);
		return page == null ? null : page.getItems().get(slot);
	}

	// -----

	@Override
	public void setPageItem(int pageIndex, int slot, ItemStack item) {
		Window page = getPage(pageIndex);
		if (page != null) {
			page.getItems().put(slot, item);
			ProtocolPackets.SET_SLOT.process(page.getViewers(), page.getId(), page.incrementStateId(), slot, item);
		}
	}

	@Override
	public void clearPageItem(int pageIndex, int slot) {
		Window page = getPage(pageIndex);
		if (page != null && page.getItems().remove(slot) != null) {
			ProtocolPackets.SET_SLOT.process(page.getViewers(), page.getId(), page.incrementStateId(), slot, null);
		}
	}

	@Override
	public void clearPage(int pageIndex) {
		Window page = pages.remove(pageIndex);
		if (page != null) {
			page.getItems().clear();
			ProtocolPackets.SET_WINDOW_ITEMS.process(page.getViewers(), page);
		}
	}

	@Override
	public void clear() {
		pages.forEach(page -> {
			page.getItems().clear();
			ProtocolPackets.SET_WINDOW_ITEMS.process(page.getViewers(), page);
		});
		pages.clear();
	}

	// ----- do

	@Override
	public void createPage() {
		pages.add(new Window(WINDOW_ID, pages.size(), getGUI()));
	}

	@Override
	public void openPage(Player player, int pageIndex) {
		RWHashSet<Player> set = CollectionUtils.asRWSet(player);
		Window page = getPage(pageIndex);
		if (page != null) {
			page.getViewers().add(player);
			ProtocolPackets.OPEN_WINDOW.process(set, page);
			ProtocolPackets.SET_WINDOW_ITEMS.process(set, page);
		}
	}

	@Override
	public void close(Player player) {
		Window page = getPage(player);
		if (page != null) {
			ProtocolPackets.CLOSE_WINDOW.process(CollectionUtils.asRWSet(player), page);
		}
		removeViewer(player);
	}

}
