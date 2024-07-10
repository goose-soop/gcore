package com.guillaumevdn.gcore.lib.gui.internal;

import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.concurrency.RWHashMap;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.gui.struct.GUI;
import com.guillaumevdn.gcore.lib.gui.struct.GUI.Option;
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

	// ----- activation
	public abstract void activate();

	public abstract void deactivate();

	// ----- get
	public GUI getGUI() {
		return gui;
	}

	public abstract int getPageCount();

	public abstract RWHashMap<Player, Integer> getViewers();

	public abstract int getViewerPage(Player player);

	public final boolean isViewer(Player player) {
		return getViewerPage(player) >= 0;
	}

	public abstract int firstEmpty(int pageIndex);

	public abstract ItemStack getPageItem(int pageIndex, int slot);

	// ----- set
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

	// ----- do
	public abstract void createPage();

	public abstract void openPage(Player player, int pageIndex);

	public abstract void close(Player player);

	// ----- event
	protected void beforeSwitchPage() {
	}

	public final void onClick(Player player, ClickType click, int slot, int pageIndex) throws Throwable {
		// control item
		if (slot == gui.getType().getPreviousPageItemSlot()) {
			if (pageIndex > 0) {
				beforeSwitchPage();
				gui.openFor(player, pageIndex - 1, gui.getFromCall(player));
				return;
			}
		} else if (slot == gui.getType().getNextPageItemSlot()) {
			if (pageIndex + 1 < getPageCount()) {
				beforeSwitchPage();
				gui.openFor(player, pageIndex + 1, gui.getFromCall(player));
				return;
			}
		} else if (slot == gui.getBackItemSlot()) {
			gui.onBack(player);
			return;
		}

		// find matching item
		GUIItem item = gui.getItemWithPerformer(pageIndex, slot, click);
		if (item == null) {
			return;
		}

		// click
		Consumer<ClickCall> performer = item.getClickPerformer(click);
		if (performer != null) {
			try {
				performer.accept(new ClickCall(player, click, gui, pageIndex, slot));
			} catch (Throwable exception) {
				getGUI().getPlugin().getMainLogger().error(
						"Couldn't perform click effects of item " + item.getId() + " in GUI " + getGUI().getId() + " at slot " + slot + " of page " + pageIndex,
						exception);
			}
		}
	}

	public void onClose(Player player) {
		gui.getPlugin().operateSyncLater(() -> {
			// trigger watchers (with a slight delay, to make sure we're not just "closing" because of switching page)
			if (!isViewer(player)) {
				gui.onClose(player);
			}

			// unregister on close
			if (!gui.getOptions().contains(Option.DONT_UNREGISTER_ON_CLOSE)) {
				if (getViewers().isEmpty()) {
					gui.deactivate(true);
				}
			}
		}, 5);
	}

}
