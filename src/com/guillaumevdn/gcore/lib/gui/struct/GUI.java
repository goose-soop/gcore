package com.guillaumevdn.gcore.lib.gui.struct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.collection.LowerCaseHashMap;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.gui.internal.Handler;
import com.guillaumevdn.gcore.lib.gui.internal.protocol.ProtocolHandler;
import com.guillaumevdn.gcore.lib.gui.internal.vanilla.VanillaHandler;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.plugin.PluginUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.tuple.IntegerPair;

/**
 * @author GuillaumeVDN
 */
public class GUI {

	// options
	public static enum Option {
		DONT_UNREGISTER_ON_CLOSE;
	}

	// base
	private final GPlugin plugin;
	private final String id;
	private final String name;
	private final GUIType type;
	private int backItemSlot = -1;
	private final Set<Option> options;
	private final List<Integer> regularItemSlots;
	private final LowerCaseHashMap<GUIItem> regularItems = new LowerCaseHashMap<>();
	private final LowerCaseHashMap<GUIItem> persistentItems = new LowerCaseHashMap<>();
	private final Handler handler;
	private long lastFilled = 0L;
	private boolean active = false;
	private ClickCall fromCall = null;

	public GUI(GPlugin plugin, String id, String name, GUIType type, ClickCall fromCall, Option... options) {
		this(plugin, id, name, type, NumberUtils.range(0, type.getRegularItemSlotsEnd()), fromCall, options);
	}

	public GUI(GPlugin plugin, String id, String name, GUIType type, List<Integer> regularItemSlots, ClickCall fromCall, Option... options) {
		this.plugin = plugin;
		this.id = id;
		String n = StringUtils.unformat(name);
		int cutAt = 30;
		if (n.length() > cutAt) {
			cutAt = cutAt + (name.length() - n.length());
			if (n.charAt(cutAt) == '§') {
				if (cutAt + 1 >= n.length()) {
					--cutAt;
				} else {
					++cutAt;
				}
			}
			this.name = name.length() > 30 ? "..." + name.substring(name.length() - 27, name.length()) : name;
		} else {
			this.name = name;
		}
		this.type = type;
		Set<Option> opts = new HashSet<>();
		for (Option option : options) {
			if (option != null) {
				opts.add(option);
			}
		}
		this.options = Collections.unmodifiableSet(opts);
		this.regularItemSlots = regularItemSlots;
		this.fromCall = fromCall;
		if (fromCall != null && type.getBackItemSlot() != -1) regularItemSlots.remove((Integer) (this.backItemSlot = type.getBackItemSlot()));
		/*
		obviously, don't do that ; that's handled
		if (type.getPreviousPageItemSlot() != -1) regularItemSlots.remove((Integer) type.getPreviousPageItemSlot());
		if (type.getNextPageItemSlot() != -1) regularItemSlots.remove((Integer) type.getNextPageItemSlot());*/
		this.handler = ConfigGCore.allowProtocolGUIs && PluginUtils.isPluginEnabled("ProtocolLib") ? new ProtocolHandler(this) : new VanillaHandler(this);
	}

	// get
	public final GPlugin getPlugin() {
		return plugin;
	}

	public final String getId() {
		return id;
	}

	public GUIType getType() {
		return type;
	}

	public final String getName() {
		return name;
	}

	public final Set<Option> getOptions() {
		return options;
	}

	public final Map<String, GUIItem> getRegularItems() {
		return regularItems;
	}

	public final GUIItem getRegularItem(String id) {
		return regularItems.get(id);
	}

	public final Map<String, GUIItem> getPersistentItems() {
		return persistentItems;
	}

	public final GUIItem getPersistentItem(String id) {
		return persistentItems.get(id);
	}

	public final int getBackItemSlot() {
		return backItemSlot;
	}

	public final boolean isActive() {
		return active;
	}

	public final ClickCall getFromCall() {
		return fromCall;
	}

	public final Map<Player, Integer> getViewers() {
		return handler.getViewers();
	}

	public final int getViewerPage(Player player) {
		return handler.getViewerPage(player);
	}

	// items
	public GUIItem getRegularItem(int pageIndex, int slot) {
		for (GUIItem item : regularItems.values()) {
			if (item.isInLocation(pageIndex, slot)) {
				return item;
			}
		}
		return null;
	}

	public GUIItem getPersistentItem(int slot) {
		for (GUIItem item : persistentItems.values()) {
			if (item.isInSlot(slot)) {
				return item;
			}
		}
		return null;
	}

	public void setItem(GUIItem item, boolean persistent) {
		if (persistent) {
			setPersistentItem(item);
		} else {
			setRegularItem(item);
		}
	}

	public void setRegularItem(GUIItem item) {
		// invalid item
		if (Mat.isVoid(item.getItem())) {
			throw new IllegalArgumentException("can't set regular item " + item.getId() + " in GUI " + getId() + ", void item");
		}
		// invalid slot
		for (IntegerPair preferredLocation : item.getLocations()) {
			if (preferredLocation.getB() < -1 || preferredLocation.getB() >= type.getSize()) {
				throw new IllegalArgumentException("can't set regular item " + item.getId() + " in GUI " + getId() + ", preferred slot " + preferredLocation.getB() + " is outside bounds (-1 to " + (type.getSize() - 1) + ")");
			}
		}
		// remove if existing
		removeRegularItem(item);
		// get locations
		List<IntegerPair> locations = new ArrayList<>();
		if (item.getPreferredLocations().isEmpty()) {
			IntegerPair location = findOrCreateFreeForRegular(-1, -1);
			if (location == null) {
				GCore.inst().getMainLogger().error("can't set regular item " + item.getId() + " in GUI " + getId() + ", no location found");
			} else {
				locations.add(location);
			}
		} else {
			for (IntegerPair preferredLocation : item.getPreferredLocations()) {
				IntegerPair location = findOrCreateFreeForRegular(preferredLocation.getA(), preferredLocation.getB());
				if (location == null) {
					GCore.inst().getMainLogger().error("can't set regular item " + item.getId() + " in GUI " + getId() + ", no location found for preferred " + preferredLocation.toString());
				} else {
					locations.add(location);
				}
			}
		}
		// register it
		regularItems.put(item.getId(), item);
		// set item
		locations.forEach(location -> {
			handler.setPageItem(location, item.getItem());
		});
		item.setLocations(locations);
	}

	public void setPersistentItem(GUIItem item) {
		// invalid item
		if (Mat.isVoid(item.getItem())) {
			throw new IllegalArgumentException("can't set persistent item " + item.getId() + " in GUI " + getId() + ", void item");
		}
		// invalid slot
		if (item.getPreferredLocations().isEmpty()) {
			throw new IllegalArgumentException("can't set persistent item " + item.getId() + " in GUI " + getId() + ", no slot found");
		}
		for (IntegerPair preferredLocation : item.getLocations()) {
			if (preferredLocation.getA() != -1) {
				throw new IllegalArgumentException("can't set persistent item " + item.getId() + " in GUI " + getId() + ", no preferred page can be specified");
			} else if (preferredLocation.getB() < 0 /* no -1 for persistent items */ || preferredLocation.getB() >= type.getSize()) {
				throw new IllegalArgumentException("can't set persistent item " + item.getId() + " in GUI " + getId() + ", preferred slot " + preferredLocation.getB() + " is outside bounds (-1 to " + (type.getSize() - 1) + ")");
			} else if (preferredLocation.getB() == backItemSlot) {
				throw new IllegalArgumentException("can't set persistent item " + item.getId() + " in GUI " + getId() + ", preferred slot " + preferredLocation.getB() + " is the back item slot");
			}
			GUIItem existing = getPersistentItem(preferredLocation.getB());
			if (existing != null) {
				throw new IllegalArgumentException("can't set persistent item " + item.getId() + " in GUI " + getId() + ", preferred slot " + preferredLocation.getB() + " already has persistent item " + existing.getId());
			}
		}
		// remove if existing
		removePersistentItem(item);
		// register it
		persistentItems.put(item.getId(), item);
		// add it on all pages
		List<GUIItem> existingRegularItems = new ArrayList<>();
		List<IntegerPair> locations = new ArrayList<>();
		item.getPreferredLocations().forEach(preferredLocation -> {
			for (int pageIndex = 0; pageIndex < handler.getPageCount(); ++pageIndex) {
				// control item
				if ((pageIndex > 0 && preferredLocation.getB() == type.getPreviousPageItemSlot()) || (pageIndex + 1 < handler.getPageCount() && preferredLocation.getB() == type.getNextPageItemSlot()) || preferredLocation.getB() == backItemSlot) {
					continue;
				}
				// remove existing regular item if any
				GUIItem existingItem = getRegularItem(pageIndex, preferredLocation.getB());
				if (existingItem != null) {
					handler.clearPageItem(pageIndex, preferredLocation.getB());
					existingRegularItems.add(existingItem);
				}
				// set item
				handler.setPageItem(pageIndex, preferredLocation.getB(), item.getItem());
				locations.add(IntegerPair.of(pageIndex, preferredLocation.getB()));
			}
		});
		// re-add existing regular items
		existingRegularItems.forEach(existingItem -> setRegularItem(existingItem));
		item.setLocations(locations);
	}

	public boolean removeRegularItem(GUIItem item) {
		return removeRegularItem(item.getId()) != null;
	}

	public GUIItem removeRegularItem(String itemId) {
		GUIItem item = regularItems.remove(itemId);
		if (item != null) {
			item.getLocations().forEach(location -> handler.clearPageItem(location));
		}
		return item;
	}

	public boolean removePersistentItem(GUIItem item) {
		return removePersistentItem(item.getId()) != null;
	}

	public boolean removePersistentItem(int slot) {
		GUIItem item = getPersistentItem(slot);
		return item == null ? false : removePersistentItem(item);
	}

	public GUIItem removePersistentItem(String itemId) {
		GUIItem item = persistentItems.remove(itemId);
		if (item != null) {
			item.getLocations().forEach(location -> handler.clearPageItem(location));
		}
		return item;
	}

	public void clear() {
		handler.clear();
		persistentItems.clear();
		regularItems.clear();
	}

	// page
	public int createPage() {
		// create page
		handler.createPage();
		int pageIndex = handler.getPageCount() - 1;
		// update page controllers
		List<GUIItem> existing = new ArrayList<>();
		if (pageIndex != 0) {
			// add previous page item on this page
			if (type.getPreviousPageItemSlot() != -1) {
				handler.setPageItem(pageIndex, type.getPreviousPageItemSlot(), ConfigGCore.previousPageItem);
			}
			// add next page item on previous page
			if (type.getNextPageItemSlot() != -1) {
				GUIItem existingItem = getRegularItem(pageIndex - 1, type.getNextPageItemSlot());
				handler.setPageItem(pageIndex - 1, type.getNextPageItemSlot(), ConfigGCore.nextPageItem);
				if (existingItem != null) {
					existing.add(existingItem);
				}
			}
		}
		// add back item
		if (backItemSlot != -1) {
			handler.setPageItem(pageIndex, backItemSlot, ConfigGCore.backItem);
		}
		// add persistent items
		persistentItems.values().forEach(persistentItem -> {
			persistentItem.getPreferredLocations().forEach(preferredLocation -> {
				if ((pageIndex == 0 ? true : preferredLocation.getB() != type.getPreviousPageItemSlot())) {
					handler.setPageItem(pageIndex, preferredLocation.getB(), persistentItem.getItem());
					// update locations
					List<IntegerPair> locations = CollectionUtils.asList(persistentItem.getLocations());
					locations.add(IntegerPair.of(pageIndex, preferredLocation.getB()));
					persistentItem.setLocations(locations);
				}
			});
		});
		// update existing items
		existing.forEach(existingItem -> {
			setRegularItem(existingItem);
		});
		// we good
		return pageIndex;
	}

	public IntegerPair findOrCreateFreeForRegular(int preferredPageIndex, int preferredSlot) {
		return doFindOrCreateFreeForRegular(preferredPageIndex, preferredSlot, false);
	}

	private IntegerPair doFindOrCreateFreeForRegular(int preferredPageIndex, int preferredSlot, boolean createdPage) {
		// has a preferred page
		if (preferredPageIndex >= 0) {
			// force create pages until count is reached
			while (preferredPageIndex >= handler.getPageCount()) {
				createPage();
			}
			// has preferred slot
			if (preferredSlot >= 0) {
				return isSlotFreeForRegular(preferredPageIndex, preferredSlot) ? new IntegerPair(preferredPageIndex, preferredSlot) : null;
			}
			// no preferred slot
			else {
				int firstEmpty = handler.firstEmpty(preferredPageIndex);
				return firstEmpty != -1 && regularItemSlots.contains(firstEmpty) ? new IntegerPair(preferredPageIndex, firstEmpty) : null;
			}
		}
		// no preferred page
		// has preferred slot
		if (preferredSlot >= 0 && preferredSlot < type.getSize()) {
			for (int page = 0; page < handler.getPageCount(); ++page) {
				if (isSlotFreeForRegular(page, preferredSlot)) {
					return new IntegerPair(page, preferredSlot);
				}
			}
		}
		// no preferred slot
		else {
			for (int page = 0; page < handler.getPageCount(); ++page) {
				int firstEmpty = handler.firstEmpty(page);
				if (firstEmpty != -1 && regularItemSlots.contains(firstEmpty)) {
					return new IntegerPair(page, firstEmpty);
				}
			}
		}
		// couldn't find any, eventually return a new page
		if (!createdPage) {
			createPage();
			return doFindOrCreateFreeForRegular(preferredPageIndex, preferredSlot, true);
		}
		return null;
	}

	public boolean isSlotFreeForRegular(int pageIndex, int slot) {
		// no page
		if (pageIndex >= handler.getPageCount()) {
			return false;
		}
		// not in regular slots
		if (!regularItemSlots.contains(slot)) {
			return false;
		}
		// has page control item
		if (type.getPreviousPageItemSlot() != -1 && pageIndex > 0 && slot == type.getPreviousPageItemSlot()) {
			return false;
		}
		if (type.getNextPageItemSlot() != -1 && pageIndex + 1 < handler.getPageCount() && slot == type.getNextPageItemSlot()) {
			return false;
		}
		if (backItemSlot != -1 && slot == backItemSlot) {
			return false;
		}
		// has a persistent item there
		if (getPersistentItem(slot) != null) {
			return false;
		}
		// there's already an item there
		ItemStack item = handler.getPageItem(pageIndex, slot);
		if (!Mat.isVoid(item)) {
			return false;
		}
		// we good
		return true;
	}

	// open and fill
	/** @return true if the GUI must be opened after refilling it */
	public boolean refill() {
		Map<Player, Integer> viewers = getViewers();
		// clear/fill
		clear();
		boolean open = doFill();
		lastFilled = System.currentTimeMillis();
		// open new pages for viewers ; old pages are cleared, but kept open, so the cursor doesn't recenter when we reopen new pages
		if (open) {
			if (handler.getPageCount() == 0) {
				createPage();
			}
			viewers.forEach((player, pageIndex) -> {
				openFor(player, pageIndex < handler.getPageCount() ? pageIndex : handler.getPageCount() - 1);
			});
		}
		// or maybe just close pages for viewers
		else {
			viewers.keySet().forEach(player -> handler.close(player));
		}
		// done
		return open;
	}

	/** @return true if the GUI must be opened after filling it */
	protected boolean doFill() {
		return true;
	}

	/** @return true if the GUI was opened */
	public final boolean refillAndOpenFor(Player player) {
		return refill() && openFor(player, 0);
	}

	/** @return true if the GUI was opened */
	public final boolean openFor(Player player) {
		return openFor(player, 0);
	}

	/** @return true if the GUI was opened ; this method can be overriden for extra monitoring but must call super */
	public boolean openFor(Player player, int pageIndex) {
		return doOpenFor(player, pageIndex);
	}

	/** @return true if the GUI was opened */
	private final boolean doOpenFor(Player player, int pageIndex) {
		// remove player from all other GUIs if ProtocolLib ; because the window close packet isn't sent when opening a window while another is already opened, and this creates messy issues (player is considered to be on multiple pages at the same time)
		if (PluginUtils.isPluginEnabled("ProtocolLib")) {
			PluginUtils.getGPlugins().forEach(plugin -> plugin.getGuis().values().forEach(gui -> {
				ProtocolHandler handler = ObjectUtils.castOrNull(((GUI) gui).handler, ProtocolHandler.class);
				if (handler != null) {
					handler.removeViewer(player);
				}
			}));
		}
		// create a page if has none
		if (handler.getPageCount() == 0) {
			createPage();
		}
		// not active ; fill it (it might have been unregistered on close, or it's a new GUI)
		if (!isActive()) {
			activate();
			lastFilled = System.currentTimeMillis();
			if (!doFill()) {
				return false;
			}
		}
		// active
		else {
			// maybe fill if it was never filled (don't know when this could happen, but it maybe could)
			if (lastFilled == 0L) {
				lastFilled = System.currentTimeMillis();
				if (!doFill()) {
					return false;
				}
			}
		}
		// invalid page
		if (pageIndex >= handler.getPageCount()) {
			throw new IllegalArgumentException("can't open GUI " + getId() + ", page index " + pageIndex + " is outside bounds");
		}
		// open page
		handler.openPage(player, pageIndex);
		return true;
	}

	// registration and listeners
	public GUI activate() {
		// already active
		if (active) {
			return this;
		}
		// set active
		active = true;
		plugin.registerGUI(this);
		handler.activate();
		onActivate();
		return this;
	}

	public void onActivate() {
	}

	public GUI deactivate(boolean clear) {
		// not active
		if (!active) {
			return this;
		}
		// set inactive
		active = false;
		plugin.unregisterGUI(this);
		handler.deactivate();
		// close
		getViewers().keySet().forEach(player -> handler.close(player));
		// clear
		if (clear) {
			clear();
		}
		onDeactivate();
		return this;
	}

	public void onDeactivate() {
	}

	public void onPlayerInventoryClick(Player clicker, int slot, ItemStack item, ClickType clickType, int clickPageIndex) {
	}

	public void onClose(Player clicker) {
	}

	public void onBack(Player clicker) {
		if (fromCall != null) {
			fromCall.getGUI().openFor(clicker, fromCall.getPageIndex());
		}
	}

}
