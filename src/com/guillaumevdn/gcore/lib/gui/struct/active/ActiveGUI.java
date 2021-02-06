package com.guillaumevdn.gcore.lib.gui.struct.active;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Nonnull;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.collection.LowerCaseHashMap;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.GUI;
import com.guillaumevdn.gcore.lib.gui.struct.GUIType;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public abstract class ActiveGUI extends GUI {

	private Replacer replacer;
	private Map<String, ActiveItemHolder> activeHolders = Collections.synchronizedMap(new LowerCaseHashMap<>());  // otherwise sometimes concurrent modification exception #1143

	public ActiveGUI(GPlugin plugin, String id, String name, GUIType type, Replacer replacer, ClickCall fromCall, Option... options) {
		super(plugin, id, name, type, NumberUtils.range(0, type.getSize() -1), fromCall, options);
		this.replacer = replacer != null ? replacer : Replacer.GENERIC;
	}

	// get
	public abstract Collection<ItemHolder> getContents();

	@Nonnull
	public Replacer getReplacer() {
		return replacer;
	}

	// fill -> starts a new GUI lifecycle (initialize or refresh)
	@Override
	protected boolean doFill() {
		// create new holders
		activeHolders.clear();
		getContents().forEach(holder -> {
			ActiveItemHolder active = holder.newActive(ActiveGUI.this);
			active.init();
			activeHolders.put(holder.getId(), active);
		});
		return true;
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
			synchronized (activeHolders) {
				activeHolders.values().forEach(ActiveItemHolder::tick);
			}
		});
	}

	@Override
	public void onDeactivate() {
		synchronized (activeHolders) {
			activeHolders.values().forEach(ActiveItemHolder::onDestroy);
		}
	}

}
