package com.guillaumevdn.gcore.lib.gui.struct.active;

import java.util.function.Consumer;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public abstract class ActiveHolderItem {

	private final ItemHolder holder;
	private final long forceRefreshDelayTicks;
	private long refreshDelayTicks;
	private long currentDelayTicks = 0;

	public ActiveHolderItem(ItemHolder holder) {
		this(holder, -1);
	}

	public ActiveHolderItem(ItemHolder holder, long forceRefreshDelayTicks) {
		this.holder = holder;
		this.refreshDelayTicks = this.forceRefreshDelayTicks = (long) forceRefreshDelayTicks;
	}

	// get
	public ItemHolder getHolder() {
		return holder;
	}

	public long getRefreshDelayTicks() {
		return refreshDelayTicks;
	}

	public boolean mustRefreshNow() {
		return refreshDelayTicks > 0L && ++currentDelayTicks % refreshDelayTicks == 0L;
	}

	// set
	public void fill(ActiveGUI instance, Replacer replacer, Runnable callback) {
		build(instance, replacer, builtItem -> {
			boolean hasPlaceholders = StringUtils.hasPlaceholders(builtItem.getItem());
			if (hasPlaceholders) {
				builtItem.setItem(replacer.parse(builtItem.getItem()));
			}
			this.refreshDelayTicks = forceRefreshDelayTicks > 0L ? forceRefreshDelayTicks : (hasPlaceholders ? ConfigGCore.guiItemRefreshTicksPlaceholders : -1L);
			instance.setItem(builtItem, holder.getPersistent(replacer));
			if (callback != null) {
				callback.run();
			}
		});
	}

	protected abstract void build(ActiveGUI instance, Replacer replacer, Consumer<GUIItem> callback);

}
