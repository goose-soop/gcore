package com.guillaumevdn.gcore.lib.gui.struct.active;

import java.util.Collection;
import java.util.function.BiConsumer;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * Represents an active ItemHolder, built for one GUI
 * It's built when the GUI is loaded or refreshed, and destroyed when the GUI is destroyed
 * -> each ItemHolder will produce exactly one ActiveItemHolders in each GUI lifecycle
 * -> the ActiveGUIItems are the one in charge of creating, removing and refreshing their icons (GUIItem)
 * @author GuillaumeVDN
 */
public abstract class ActiveItemHolder {

	private final ActiveGUI instance;
	private final ItemHolder holder;
	private final boolean persistent;

	private Collection<? extends GUIItem> lastItems = null;
	private int refreshDelayTicks = -1;
	private int currentDelayTicks = 0;  // we can use int, it'd take like 3 years to have an overflow, spigot will crash well before that time :KEKW:

	public ActiveItemHolder(ActiveGUI instance, ItemHolder holder) {
		this.instance = instance;
		this.holder = holder;
		this.persistent = holder.parsePersistent(instance);
	}

	// get
	public final ActiveGUI getInstance() {
		return instance;
	}

	public final ItemHolder getHolder() {
		return holder;
	}

	public final Collection<? extends GUIItem> getLastItems() {
		return lastItems;
	}

	public final long getLastRefreshDelayTicks() {
		return refreshDelayTicks;
	}

	// set
	protected void setRefreshDelayTicks(int refreshDelayTicks) {
		this.refreshDelayTicks = refreshDelayTicks;
	}

	// methods
	final void init() {
		onCreate();
		doRefresh();
	}

	public final void tick() {
		if (refreshDelayTicks > 0 && ++currentDelayTicks % refreshDelayTicks == 0) {  // should refresh ?
			doRefresh();
		}
	}

	public void doRefresh() {
		try {
			buildItems((items, newRefreshDelay) -> {
				// parse items if still contains placeholders (maybe vanilla/generic placeholders were put there, our build() method doesn't parse those, only the holder/type-specific one)
				for (GUIItem item : items) {
					boolean hasPlaceholders = StringUtils.hasPlaceholders(item.getItem());
					if (hasPlaceholders) {
						item.setItem(instance.getReplacer().parse(item.getItem()));
						// force a refresh delay if there's no holder/type-specific one
						if (newRefreshDelay <= 0) {
							newRefreshDelay = ConfigGCore.guiItemRefreshTicksPlaceholders;
						}
					}
				}
				// set new delay
				this.refreshDelayTicks = newRefreshDelay;
				// remove last items
				if (lastItems != null) {
					items.forEach(item -> instance.removeItem(item, persistent));
				}
				// then fill new items in GUI
				items.forEach(item -> instance.setItem(item, persistent));
				lastItems = items;
				// done
				onRefreshed();
			});
		} catch (ParsingError error) {
			ParsingError.print(error, null);
		}
	}

	/**
	 * @return a list of items and forced refresh delay
	 * @throws ParsingError if a parsing error occurs when parsing this holder's settings during the initial build phase
	 */
	protected abstract void buildItems(BiConsumer<Collection<? extends GUIItem>, Integer> callback) throws ParsingError;

	// watchers
	public void onCreate() {
	}

	public void onRefreshed() {
	}

	public void onDestroy() {
	}

}
