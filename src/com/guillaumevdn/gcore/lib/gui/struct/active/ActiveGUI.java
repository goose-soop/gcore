package com.guillaumevdn.gcore.lib.gui.struct.active;

import java.util.Collection;
import java.util.List;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.collection.LowerCaseHashMap;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.GUI;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.gui.struct.GUIType;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public abstract class ActiveGUI extends GUI {

	private Replacer replacer;
	private LowerCaseHashMap<ActiveHolderItem> placeholderItems = new LowerCaseHashMap<>();

	public ActiveGUI(GPlugin plugin, String id, String name, GUIType type, Replacer replacer, ClickCall fromCall, Option... options) {
		this(plugin, id, name, type, replacer, NumberUtils.range(0, type.getRegularItemSlotsEnd()), fromCall, options);
	}

	public ActiveGUI(GPlugin plugin, String id, String name, GUIType type, Replacer replacer, List<Integer> regularItemSlots, ClickCall fromCall, Option... options) {
		super(plugin, id, name, type, regularItemSlots, fromCall, options);
		this.replacer = replacer;
	}

	// get
	public abstract Collection<ItemHolder> getContents();

	public Replacer getReplacer() {
		return replacer;
	}

	// fill
	@Override
	protected boolean doFill() {
		placeholderItems.clear();
		getContents().forEach(item -> fillItem(item));
		return true;
	}

	protected void fillItem(ItemHolder holder) {
		try {
			ActiveHolderItem item = holder.newActive(ActiveGUI.this);
			if (item != null) {
				item.fill(this, replacer, () -> {
					// add to placeholder AFTER setting item in GUI (maybe there'll be an error AND also the remove method will be called)
					if (item.getRefreshDelayTicks() > 0L) {
						placeholderItems.put(holder.getId(), item);
					}
				});
			}
		} catch (ParsingError error) {
			ParsingError.print(error, null);
		}
	}

	@Override
	public GUIItem removeRegularItem(String itemId) {
		placeholderItems.remove(itemId);
		return super.removeRegularItem(itemId);
	}

	@Override
	public GUIItem removePersistentItem(String itemId) {
		placeholderItems.remove(itemId);
		return super.removePersistentItem(itemId);
	}

	// events
	@Override
	public void onActivate() {
		getPlugin().registerTask("gui_refresh_" + getId(), true, 1, () -> {
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
