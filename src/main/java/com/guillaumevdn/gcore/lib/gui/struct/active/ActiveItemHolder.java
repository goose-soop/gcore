package com.guillaumevdn.gcore.lib.gui.struct.active;

import java.util.Collection;
import java.util.Set;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.function.TriConsumer;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;

/**
 * Represents an active ItemHolder, built for one GUI It's built when the GUI is loaded or refreshed, and destroyed when
 * the GUI is destroyed -> each ItemHolder will produce exactly one ActiveItemHolders in each GUI lifecycle (by default,
 * if not forced manually) -> the active holders are the ones in charge of creating, removing and refreshing their icons
 * (GUIItem)
 * 
 * @author GuillaumeVDN
 */
public abstract class ActiveItemHolder {

	private final ActiveGUI instance;
	private final ItemHolder holder;
	private final boolean persistent;

	private Collection<? extends GUIItem> lastItems = null;
	private Set<String> lastPlaceholders = null;

	private int refreshDelayTicks = -1;
	private int currentDelayTicks = 0; // we can use int, it'd take like 3 years to have an overflow, spigot will pass out well before that time :KEKW:

	public ActiveItemHolder(ActiveGUI instance, ItemHolder holder) {
		this.instance = instance;
		this.holder = holder;
		this.persistent = holder.parsePersistent(instance);
	}

	public final ActiveGUI getInstance() {
		return instance;
	}

	public final ItemHolder getHolder() {
		return holder;
	}

	public final Collection<? extends GUIItem> getLastItems() {
		return lastItems;
	}

	public final Set<String> getLastPlaceholders() {
		return lastPlaceholders;
	}

	public final long getLastRefreshDelayTicks() {
		return refreshDelayTicks;
	}

	public int getCurrentDelayTicks() {
		return currentDelayTicks;
	}

	public final void setRefreshDelayTicks(int refreshDelayTicks) {
		setRefreshDelayTicks(refreshDelayTicks, false);
	}

	public final void setRefreshDelayTicks(int refreshDelayTicks, boolean refreshNext) {
		this.refreshDelayTicks = refreshDelayTicks;
		if (refreshNext) {
			this.currentDelayTicks = -1;
		}
	}

	// -----

	public final void init() {
		onCreate();
		doRefresh();
	}

	public final void tick() {
		if (refreshDelayTicks > 0 && ++currentDelayTicks % refreshDelayTicks == 0) { // should refresh ?
			doRefresh();
		}
	}

	public void doRefresh() {
		try {
			buildItems((items, placeholders, newRefreshDelay) -> {
				if (newRefreshDelay == null) {
					newRefreshDelay = -1;
				}

				// remove last items
				if (lastItems != null) {
					lastItems.forEach(item -> instance.removeItem(item, persistent));
				}

				lastItems = items;
				lastPlaceholders = placeholders;

				// force refresh delay if there are placeholders
				if (placeholders != null && !placeholders.isEmpty()
						&& (newRefreshDelay <= 0 || ConfigGCore.guiItemRefreshTicksPlaceholders < newRefreshDelay)) {
					newRefreshDelay = ConfigGCore.guiItemRefreshTicksPlaceholders;
				}

				// set new delay
				this.refreshDelayTicks = newRefreshDelay;

				// then fill new items in GUI
				items.forEach(item -> instance.setItem(item, persistent));

				// call watcher ; done
				onRefreshed();
			});
		} catch (ParsingError error) {
			ParsingError.print(error, null);
		}
	}

	/**
	 * @return a list of items, a list of placeholders they had (can be null if not important for this item type) and a
	 *         forced refresh delay
	 * @throws ParsingError if a parsing error occurs when parsing this holder's settings during the initial build phase
	 */
	protected abstract void buildItems(TriConsumer<Collection<? extends GUIItem>, Set<String>, Integer> callback) throws ParsingError;

	/**
	 * @return true if something was done (therefore we should not check any other active holder)
	 */
	public boolean onPlayerInventoryClick(ClickCall call, ItemStack item) {
		return false;
	}

	// ----- watchers

	public void onCreate() {
	}

	public void onRefreshed() {
	}

	public void onDestroy() {
	}

}
