package com.guillaumevdn.gcore.lib.gui.internal.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.ProtocolLibrary;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.gui.internal.Handler;
import com.guillaumevdn.gcore.lib.gui.struct.GUI;

/**
 * @author GuillaumeVDN
 */
public class ProtocolHandler extends Handler {

	public static final int WINDOW_ID = 72; // signed byte ; don't go above 128
	List<Window> pages = new ArrayList<>();
	private ProtocolEvents events = new ProtocolEvents(this);

	public ProtocolHandler(GUI gui) {
		super(gui);
	}

	// activation
	@Override
	public void activate() {
		ProtocolLibrary.getProtocolManager().addPacketListener(events);
	}

	@Override
	public void deactivate() {
		ProtocolLibrary.getProtocolManager().removePacketListener(events);
	}

	// get
	@Override
	public Map<Player, Integer> getViewers() {
		return CollectionUtils.asMap(map -> pages.forEach(page -> page.getViewers().forEach(player -> map.put(player, page.getIndex()))));
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
		for (Window page : pages) {
			if (page.getViewers().contains(player)) {
				return page;
			}
		}
		return null;
	}

	public void removeViewer(Player player) {
		for (Window page : pages) {
			page.getViewers().remove(player);
		}
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

	// set
	@Override
	public void setPageItem(int pageIndex, int slot, ItemStack item) {
		Window page = getPage(pageIndex);
		if (page != null) {
			page.getItems().put(slot, item);
			ProtocolPackets.SET_SLOT.process(page.getViewers(), page.getId(), slot, item);
		}
	}

	@Override
	public void clearPageItem(int pageIndex, int slot) {
		Window page = getPage(pageIndex);
		if (page != null && page.getItems().remove(slot) != null) {
			ProtocolPackets.SET_SLOT.process(page.getViewers(), page.getId(), slot, null);
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
		CollectionUtils.clearForEach(pages, page -> {
			page.getItems().clear();
			ProtocolPackets.SET_WINDOW_ITEMS.process(page.getViewers(), page);
		});
	}

	// do
	@Override
	public void createPage() {
		pages.add(new Window(WINDOW_ID, pages.size(), getGUI()));
	}

	@Override
	public void openPage(Player player, int pageIndex) {
		List<Player> list = CollectionUtils.asList(player);
		Window page = getPage(pageIndex);
		page.getViewers().add(player);
		ProtocolPackets.OPEN_WINDOW.process(list, page);
		ProtocolPackets.SET_WINDOW_ITEMS.process(list, page);
	}

	@Override
	public void close(Player player) {
		Window page = getPage(player);
		if (page != null) {
			ProtocolPackets.CLOSE_WINDOW.process(CollectionUtils.asList(player), page);
		}
		removeViewer(player);
	}

}
