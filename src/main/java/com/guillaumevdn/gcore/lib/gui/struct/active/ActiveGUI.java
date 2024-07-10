package com.guillaumevdn.gcore.lib.gui.struct.active;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.concurrency.RWLowerCaseHashMap;
import com.guillaumevdn.gcore.lib.gui.element.item.ElementGUIItemHolder;
import com.guillaumevdn.gcore.lib.gui.element.item.type.GUIItemType;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.GUI;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.gui.struct.GUIType;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public abstract class ActiveGUI extends GUI {

	private final Replacer replacer;
	private final RWLowerCaseHashMap<ActiveItemHolder> activeHolders = new RWLowerCaseHashMap<>(5, 1f); // otherwise sometimes concurrent modification exception
																										// #1143
	private final RWLowerCaseHashMap<Object> lifecycleData = new RWLowerCaseHashMap<>(1, 1f); // this allows to store temporary values, such as interactions
																								// with player inventory slots
	private boolean asyncInit = false; // when building GUIs with a great amount of players heads to fetch, this avoids creating too many async threads

	public ActiveGUI(GPlugin plugin, String id, String name, GUIType type, Replacer replacer, Option... options) {
		super(plugin, id, name, type, NumberUtils.range(0, type.getSize() - 1), options);
		this.replacer = replacer;
	}

	public abstract Collection<ItemHolder> getContents();

	public final RWLowerCaseHashMap<ActiveItemHolder> getActiveHolders() {
		return activeHolders;
	}

	public final RWLowerCaseHashMap<Object> getLifecycleData() {
		return lifecycleData;
	}

	public final boolean getLifecycleDataBoolean(String key) {
		final Object value = lifecycleData.get(key);
		return value != null && (boolean) value;
	}

	public final boolean hasLifecycleData(String key) {
		return lifecycleData.containsKey(key);
	}

	@Nonnull
	public Replacer getReplacer() {
		return replacer;
	}

	public ActiveItemHolder getNonBorderHolder(int page, int slot) {
		return activeHolders.streamResultValues(
				str -> str.filter(active -> active.getLastItems() != null && active.getLastItems().stream().anyMatch(it -> it.isInLocation(page, slot)))
						.findAny().orElse(null));
	}

	protected void setAsyncInit() {
		this.asyncInit = true;
	}

	// ----- lifecycle

	// fill starts a new GUI lifecycle (initialize or refill)

	@Override
	protected boolean doFill() {
		// reset lifecycle data
		lifecycleData.clear();

		// create new holders
		activeHolders.clear();
		getContents().forEach(holder -> {
			ActiveItemHolder active = holder.newActive(this);
			activeHolders.put(holder.getId(), active);
		});

		// init holders
		(asyncInit ? BukkitThread.ASYNC : BukkitThread.current()).operate(getPlugin(), () -> {
			activeHolders.forEach((__, active) -> active.init());
		});

		return true;
	}

	public final void directAdd(ItemHolder holder) { // called in some GUIs, such as GUIs that add elements dynamically when the player does actions ; to avoid
														// refreshing the whole thing
		ActiveItemHolder active = holder.newActive(this);
		active.init();
		activeHolders.put(holder.getId(), active);
	}

	public final void directRemove(ItemHolder holder) { // called in some GUIs, such as GUIs that remove elements dynamically when the player does actions ; to
														// avoid refreshing the whole thing
		directRemove(holder.getId());
	}

	public final void directRemove(String holderId) { // called in some GUIs, such as GUIs that remove elements dynamically when the player does actions ; to
														// avoid refreshing the whole thing
		final ActiveItemHolder active = activeHolders.remove(holderId);
		if (active != null) {
			final Collection<? extends GUIItem> last = active.getLastItems();
			if (last != null) {
				boolean persistent = active.getHolder().parsePersistent(this);
				last.forEach(item -> removeItem(item, persistent));
			}
		}
	}

	/*
	 * @Override public boolean openFor(Player player, int pageIndex, ClickCall fromCall) { int viewers =
	 * getViewers().size(); boolean opened = super.openFor(player, pageIndex, fromCall); if (opened && viewers == 0) { // if
	 * just opened for a viewer, start task getPlugin().registerTask("gui_tick_" + getId(), true, 1, () -> { tick(); }); }
	 * return opened; }
	 */

	@Override
	public void onActivate() {
		getPlugin().registerTask("gui_tick_" + getId(), true, 1, () -> {
			tick();
		});
	}

	@Override
	public void onDeactivate() {
		getPlugin().stopTask("gui_tick_" + getId());
		activeHolders.forEach((__, active) -> active.onDestroy());
	}

	// ----- during lifecycle

	public <T> T getLifecycleData(String key) {
		return (T) lifecycleData.get(key);
	}

	public <T> void setLifecycleData(String key, T value) {
		lifecycleData.put(key, value);
	}

	public void removeLifecycleData(String key) {
		lifecycleData.remove(key);
	}

	public void refreshOfType(GUIItemType type) {
		refreshIf(active -> {
			ElementGUIItemHolder holder = ObjectUtils.castOrNull(active.getHolder(), ElementGUIItemHolder.class);
			return holder != null && holder.getElement().getType().equals(type);
		});
	}

	public void refreshWithPlaceholders(String... placeholders) {
		refreshIf(active -> active.getLastPlaceholders() != null
				&& (placeholders == null || placeholders.length == 0 || Stream.of(placeholders).anyMatch(pl -> active.getLastPlaceholders().contains(pl))));
	}

	public void refreshIf(Predicate<ActiveItemHolder> filter) {
		activeHolders.forEach((__, active) -> {
			if (filter.test(active)) {
				active.doRefresh();
			}
		});
	}

	private void tick() {
		if (!isActive() || getViewers().isEmpty())
			return;
		activeHolders.forEach((__, active) -> active.tick());
		onTick();
	}

	protected void onTick() {
	}

	@Override
	public void onPlayerInventoryClick(ClickCall call, ItemStack item) {
		activeHolders.iterateNoModify((__, active, iter) -> {
			if (active.onPlayerInventoryClick(call, item)) {
				iter.stop();
			}
		});
	}

}
