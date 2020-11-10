package com.guillaumevdn.gcore.lib.gui.internal;

import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.gui.struct.GUI;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.tuple.IntegerPair;

/**
 * @author GuillaumeVDN
 */
public abstract class Handler {

	private GUI gui;

	public Handler(GUI gui) {
		this.gui = gui;
	}

	// activation
	public abstract void activate();
	public abstract void deactivate();

	// get
	public GUI getGUI() {
		return gui;
	}

	public abstract int getPageCount();
	public abstract Map<Player, Integer> getViewers();
	public abstract int getViewerPage(Player player);
	public abstract int firstEmpty(int pageIndex);
	public abstract ItemStack getPageItem(int pageIndex, int slot);

	// set
	public void setPageItem(IntegerPair location, ItemStack item) {
		setPageItem(location.getA(), location.getB(), item);
	}

	public void clearPageItem(IntegerPair location) {
		clearPageItem(location.getA(), location.getB());
	}

	public abstract void setPageItem(int pageIndex, int slot, ItemStack item);
	public abstract void clearPageItem(int pageIndex, int slot);
	public abstract void clearPage(int pageIndex);
	public abstract void clear();

	// do
	public abstract void createPage();
	public abstract void openPage(Player player, int pageIndex);
	public abstract void close(Player player);

	// event
	public final void onClick(Player player, ClickType click, int slot, int pageIndex) throws Throwable {
		// control item
		if (slot == gui.getType().getPreviousPageItemSlot()) {
			if (pageIndex > 0) {
				gui.openFor(player, pageIndex - 1);
				return;
			}
		} else if (slot == gui.getType().getNextPageItemSlot()) {
			if (pageIndex + 1 < getPageCount()) {
				gui.openFor(player, pageIndex + 1);
				return;
			}
		} else if (slot == gui.getBackItemSlot()) {
			gui.onBack(player);
			return;
		}
		// find matching item
		GUIItem item = gui.getPersistentItem(slot);
		if (item == null) item = gui.getRegularItem(pageIndex, slot);
		if (item == null) return;
		// click
		Consumer<ClickCall> performer = item.getClickPerformer(click);
		if (performer != null) {
			try {
				performer.accept(new ClickCall(player, click, gui, pageIndex, slot));
			} catch (Throwable exception) {
				throw new Error("couldn't perform click effects of item " + item.getId() + " in GUI " + getGUI().getId() + " at slot " + slot + " of page " + pageIndex, exception);
			}
		}
	}

}
