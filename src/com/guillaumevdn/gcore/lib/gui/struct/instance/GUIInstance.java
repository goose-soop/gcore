package com.guillaumevdn.gcore.lib.gui.struct.instance;

import java.util.Collection;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.collection.LowerCaseHashMap;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.GUI;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.gui.struct.GUIType;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;
import com.guillaumevdn.gcore.lib.wrapper.Wrapper;

/**
 * @author GuillaumeVDN
 */
public abstract class GUIInstance extends GUI {

	private Replacer replacer;
	private LowerCaseHashMap<GUIInstanceItem> placeholderItems = new LowerCaseHashMap<>();

	public GUIInstance(GPlugin plugin, String id, String name, GUIType type, Replacer replacer, ClickCall fromCall) {
		super(plugin, id, name, type, NumberUtils.range(0, type.getSize() - 1), fromCall);
		this.replacer = replacer;
	}

	// get
	public abstract Collection<GUIInstanceItemHolder> getContents();

	// fill
	@Override
	protected boolean doFill() {
		placeholderItems.clear();
		getContents().forEach(item -> fillItem(item));
		return true;
	}

	private void fillItem(GUIInstanceItemHolder holder) {
		Wrapper<GUIInstanceItem> wrapper = holder.build(GUIInstance.this, replacer);
		if (wrapper == null) {
			getPlugin().getMainLogger().warning("Couldn't decode item " + holder.getId() + " in GUI " + getId());
		} else {
			GUIInstanceItem item = wrapper.get();
			if (item != null) {
				item.fillItem(this, replacer, callbackItem -> {
					// add to placeholder AFTER setting item in GUI (maybe there'll be an error AND also the remove method will be called)
					if (item.getRefreshDelay() > 0) {
						placeholderItems.put(callbackItem.getId(), item);
					}
				});
			}
		}
	}

	@Override
	public GUIItem removeRegularItem(String itemId) {
		placeholderItems.remove(itemId);
		return super.removeRegularItem(itemId);
	}

	public GUIItem removePersistentItem(String itemId) {
		placeholderItems.remove(itemId);
		return super.removePersistentItem(itemId);
	}

	// events
	@Override
	public void onActivate() {
		getPlugin().registerTask(getId(), true, 20, () -> {
			// not active
			if (!isActive()) {
				getPlugin().stopTask(getId());
				return;
			}
			if (getViewers().isEmpty()) {
				return;
			}
			// refresh items
			CollectionUtils.iterate(CollectionUtils.asList(placeholderItems.values()), (iterator, next, breaker) -> {
				if (next.mustRefreshNow()) {
					iterator.remove();
					fillItem(next.getHolder());
				}
			});
		});
	}

}
