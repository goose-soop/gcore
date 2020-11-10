package com.guillaumevdn.gcore.lib.gui.struct.instance;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class GUIInstanceItem {

	private final GUIInstanceItemHolder holder;
	private BiConsumer<Replacer, Consumer<GUIItem>> itemBuilder;
	private final int forceRefreshDelay;
	private int refreshDelay;
	private int currentDelay = 0;

	public GUIInstanceItem(GUIInstanceItemHolder holder, BiConsumer<Replacer, Consumer<GUIItem>> itemBuilder) {
		this(holder, itemBuilder, -1);
	}

	public GUIInstanceItem(GUIInstanceItemHolder holder, BiConsumer<Replacer, Consumer<GUIItem>> itemBuilder, int forceRefreshDelay) {
		this.holder = holder;
		this.itemBuilder = itemBuilder;
		this.refreshDelay = this.forceRefreshDelay = forceRefreshDelay;
	}

	// get
	public GUIInstanceItemHolder getHolder() {
		return holder;
	}

	public int getRefreshDelay() {
		return refreshDelay;
	}

	public boolean mustRefreshNow() {
		return refreshDelay > 0 && ++currentDelay % refreshDelay == 0;
	}

	// set
	public void fillItem(GUIInstance instance, Replacer replacer, Consumer<GUIItem> callback) {
		itemBuilder.accept(replacer, buildItem -> {
			if (StringUtils.hasPlaceholders(buildItem.getItem())) {
				buildItem.setItem(replacer.parse(buildItem.getItem()));
				this.refreshDelay = forceRefreshDelay > 0 ? forceRefreshDelay : (StringUtils.hasPlaceholders(buildItem.getItem()) ? ConfigGCore.guiItemRefreshSecondsPlaceholders : -1);
			}
			instance.setItem(buildItem, holder.getPersistent(replacer));
			if (callback != null) {
				callback.accept(buildItem);
			}
		});
	}

}
