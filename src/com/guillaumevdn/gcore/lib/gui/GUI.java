package com.guillaumevdn.gcore.lib.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.Logger;
import com.guillaumevdn.gcore.lib.Logger.Level;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.util.Utils;

public class GUI implements Listener {

	// ------------------------------------------------------------
	// Fields and constructor
	// ------------------------------------------------------------

	// static fields
	public static final ItemData PREVIOUS_PAGE_ITEM = new ItemData("previous_page", 45, Mat.ARROW, 1, GLocale.GUI_GENERIC_PREVIOUSPAGEITEM.getLine(), null);
	public static final ItemData PREVIOUS_PAGE_ITEM_EMPTY = new ItemData("previous_page_empty", 45, Mat.AIR, 1, null, null);
	public static final ItemData NEXT_PAGE_ITEM = new ItemData("next_page", 53, Mat.ARROW, 1, GLocale.GUI_GENERIC_NEXTPAGEITEM.getLine(), null);
	public static final ItemData NEXT_PAGE_ITEM_EMPTY = new ItemData("next_page_empty", 53, Mat.AIR, 1, null, null);
	private static List<GUI> registeredGUIs = new ArrayList<GUI>();

	// static methods
	public static void unregisterAll(Plugin plugin) {
		for (GUI gui : Utils.asList(registeredGUIs)) {
			if (gui.plugin.equals(plugin)) {
				gui.unregister();
				registeredGUIs.remove(gui);
			}
		}
	}

	// fields
	private final Plugin plugin;
	private boolean registered = false;
	private final String name;
	private final int size;
	private final int maxRegularItemSlot;
	private final boolean unregisterOnClose;
	private ClickTolerance clickTolerance;
	private final ItemData previousPageItem;
	private final ItemData previousPageItemEmpty;
	private int previousPageItemSlot;
	private final ItemData nextPageItem;
	private final ItemData nextPageItemEmpty;
	private int nextPageItemSlot;
	private List<Inventory> pages = new ArrayList<Inventory>();
	private Map<String, ClickeableItem> regularItems = new HashMap<String, ClickeableItem>();
	private Map<String, ClickeableItem> persistentItems = new HashMap<String, ClickeableItem>();

	// bases
	public GUI(Plugin plugin, String name, int size, int maxRegularItemSlot) {
		this(plugin, name, size, maxRegularItemSlot, true, ClickTolerance.ALLOW, null, null, null, null);
	}

	public GUI(Plugin plugin, String name, int size, int maxRegularItemSlot, ItemData previousPageItem, ItemData nextPageItem) {
		this(plugin, name, size, maxRegularItemSlot, true, ClickTolerance.ALLOW, previousPageItem, null, nextPageItem, null);
	}

	public GUI(Plugin plugin, String name, int size, int maxRegularItemSlot, boolean unregisterOnClose) {
		this(plugin, name, size, maxRegularItemSlot, unregisterOnClose, ClickTolerance.ALLOW, null, null, null, null);
	}

	public GUI(Plugin plugin, String name, int size, int maxRegularItemSlot, boolean unregisterOnClose, ClickTolerance clickTolerance) {
		this(plugin, name, size, maxRegularItemSlot, unregisterOnClose, clickTolerance, null, null, null, null);
	}

	public GUI(Plugin plugin, String name, int size, int maxRegularItemSlot, boolean unregisterOnClose, ClickTolerance clickTolerance, ItemData previousPageItem, ItemData previousPageItemEmpty, ItemData nextPageItem, ItemData nextPageItemEmpty) {
		this.plugin = plugin;
		this.name = name;
		this.size = size;
		this.maxRegularItemSlot = maxRegularItemSlot >= size ? size - 1 : maxRegularItemSlot;
		this.unregisterOnClose = unregisterOnClose;
		this.clickTolerance = clickTolerance;
		// previous page items
		this.previousPageItemSlot = size - 9;
		if (previousPageItem == null) previousPageItem = PREVIOUS_PAGE_ITEM;
		if (previousPageItem.getSlot() != previousPageItemSlot) previousPageItem.setSlot(previousPageItemSlot);
		this.previousPageItem = previousPageItem;
		if (previousPageItemEmpty == null) previousPageItemEmpty = PREVIOUS_PAGE_ITEM_EMPTY;
		if (previousPageItemEmpty.getSlot() != previousPageItemSlot) previousPageItemEmpty.setSlot(previousPageItemSlot);
		this.previousPageItemEmpty = previousPageItemEmpty;
		// next page items
		this.nextPageItemSlot = size - 1;
		if (nextPageItem == null) nextPageItem = NEXT_PAGE_ITEM;
		if (nextPageItem.getSlot() != nextPageItemSlot) nextPageItem.setSlot(nextPageItemSlot);
		this.nextPageItem = nextPageItem;
		if (nextPageItemEmpty == null) nextPageItemEmpty = NEXT_PAGE_ITEM_EMPTY;
		if (nextPageItemEmpty.getSlot() != nextPageItemSlot) nextPageItemEmpty.setSlot(nextPageItemSlot);
		this.nextPageItemEmpty = nextPageItemEmpty;
		// register
		register();
	}

	// ------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------

	// get
	public Plugin getPlugin() {
		return plugin;
	}

	public String getName() {
		return name;
	}

	public int getSize() {
		return size;
	}

	public int getMaxRegularItemSlot() {
		return maxRegularItemSlot;
	}

	public ClickTolerance getClickTolerance() {
		return clickTolerance;
	}

	public void setClickTolerance(ClickTolerance clickTolerance) {
		this.clickTolerance = clickTolerance;
	}

	public List<Inventory> getPages() {
		return pages;
	}

	public Inventory getPage(int index) {
		return index >= 0 || index < pages.size() ? pages.get(index) : null;
	}

	public int getPageIndex(Inventory inventory) {
		for (int i = 0; i < pages.size(); i++) {
			if (pages.get(i).equals(inventory)) {
				return i;
			}
		}
		return -1;
	}

	public Map<String, ClickeableItem> getRegularItems() {
		return regularItems;
	}

	public Map<String, ClickeableItem> getPersistentItems() {
		return persistentItems;
	}

	public boolean isRegistered() {
		return registered;
	}

	public void unregister() {
		if (!registered) {
			return;
		}
		registered = false;
		// close viewers
		for (Player player : getViewers()) {
			player.closeInventory();
		}
		// clear pages
		for (Inventory page : pages) {
			page.clear();
		}
		// clear everything
		pages.clear();
		regularItems.clear();
		persistentItems.clear();
		// unregister
		registeredGUIs.remove(this);
		HandlerList.unregisterAll(this);
		// on unregister
		onUnregister();
	}

	public void register() {
		if (registered) {
			return;
		}
		registered = true;
		// register
		Bukkit.getPluginManager().registerEvents(this, plugin);
		registeredGUIs.add(this);
		// on register
		onRegister();
	}

	// items
	public void setRegularItem(ClickeableItem item) {
		// remove if existing
		removeRegularItem(item);
		// get page
		int pageIndex = getFirstEmptyPageIndex(item.getItemData().getSlot(), true);
		Inventory page = pages.get(pageIndex);
		item.setPageIndex(pageIndex);
		// get slot
		int slot = item.getItemData().getSlot();
		if (!canUseSlot(slot, true)) slot = firstBlank(page);
		item.setSlot(slot);
		// set item
		page.setItem(slot, item.getGUIStack());
		// register item
		regularItems.put(item.getItemData().getId(), item);
	}

	public void removeRegularItem(ClickeableItem item) {
		removeRegularItem(item.getItemData().getId());
	}

	public void removeRegularItem(String itemId) {
		if (regularItems.containsKey(itemId)) {
			// remove from page
			ClickeableItem item = regularItems.get(itemId);
			pages.get(item.getPageIndex()).setItem(item.getSlot(), null);
			// remove item
			regularItems.remove(itemId);
		}
	}

	public void removeRegularItem(int slot, boolean firstOnly) {
		for (String id : Utils.asList(regularItems.keySet())) {
			ClickeableItem item = regularItems.get(id);
			if (item.getSlot() == slot) {
				removeRegularItem(item);
				if (firstOnly) return;
			}
		}
	}

	public boolean containsRegularItem(ClickeableItem item) {
		return regularItems.containsKey(item.getItemData().getId());
	}

	public void setPersistentItem(ClickeableItem item) {
		// remove if existing
		removePersistentItem(item);
		// slot
		item.setSlot(item.getItemData().getSlot());
		item.setPageIndex(-1);
		// add item to existing pages
		if (pages.isEmpty()) createPage();
		int pageIndex = -1;
		for (Inventory page : pages) {
			++pageIndex;
			if (item.getSlot() == previousPageItemSlot ? pageIndex == 0 : (item.getSlot() == nextPageItemSlot ? pageIndex == pages.size() - 1 : true)) {
				page.setItem(item.getSlot(), item.getGUIStack());
			}
		}
		// register item
		persistentItems.put(item.getItemData().getId(), item);
	}

	public void removePersistentItem(ClickeableItem item) {
		if (persistentItems.containsKey(item.getItemData().getId())) {
			// remove from existing pages
			for (Inventory page : pages) {
				page.setItem(item.getSlot(), null);
			}
			// remove item
			persistentItems.remove(item.getItemData().getId());
		}
	}

	public void removePersistentItem(String itemId) {
		if (persistentItems.containsKey(itemId)) {
			// remove from existing pages
			ClickeableItem item = persistentItems.get(itemId);
			for (Inventory page : pages) {
				page.setItem(item.getSlot(), null);
			}
			// remove item
			persistentItems.remove(itemId);
		}
	}

	public void removePersistentItem(int slot) {
		for (String id : Utils.asList(persistentItems.keySet())) {
			ClickeableItem item = persistentItems.get(id);
			if (item.getSlot() == slot) {
				removePersistentItem(item);
				return;
			}
		}
	}

	public boolean containsPersistentItem(ClickeableItem item) {
		return persistentItems.containsKey(item.getItemData().getId());
	}

	// pages
	public int createPage() {
		// get inventory name
		int index = pages.size();
		String invName = name + (index > 0 ? " - " + (index + 1) : "");
		if (invName.length() > 32) {
			int startIndex = invName.length() - 29;
			if (startIndex > 0 && invName.charAt(startIndex - 1) == '�') startIndex++;
			invName = "..." + invName.substring(startIndex, invName.length());
		}
		if (invName.length() > 32) {
			Logger.log(Level.WARNING, plugin.getName(), "Tried to register inventory with name '" + invName + "' (size " + invName.length() + ")");
			invName = invName.substring(0, 32);
		}
		// create page
		Inventory page = Bukkit.createInventory(null, size, invName);
		pages.add(page);
		// add persistent items
		for (ClickeableItem item : persistentItems.values()) {
			page.setItem(item.getSlot(), item.getGUIStack());
		}
		// update page items
		updatePageItems();
		// return index
		return index;
	}

	public int getFirstEmptyPageIndex(boolean createNew) {
		for (int index = 0; index < pages.size(); index++) {
			int free = firstBlank(pages.get(index));
			if (canUseSlot(free, true)) {
				return index;
			}
		}
		return createNew ? createPage() : -1;
	}

	public int getFirstEmptyPageIndex(int slot, boolean createNew) {
		if (canUseSlot(slot, true)) {
			for (int index = 0; index < pages.size(); index++) {
				ItemStack it = pages.get(index).getItem(slot);
				if (Mat.from(it).isAir()) {
					return index;
				}
			}
		} else {
			for (int index = 0; index < pages.size(); index++) {
				int free = firstBlank(pages.get(index));
				if (canUseSlot(free, true)) {
					return index;
				}
			}
		}
		return createNew ? createPage() : -1;
	}

	public void updatePageItems() {
		for (int index = 0; index < pages.size(); index++) {
			Inventory page = pages.get(index);
			ItemStack prev = index == 0 ? previousPageItemEmpty.getItemStack() : previousPageItem.getItemStack();
			ItemStack post = index + 1 == pages.size() ? nextPageItemEmpty.getItemStack() : nextPageItem.getItemStack();
			if (!Mat.from(prev).isAir()) page.setItem(previousPageItemSlot, prev);
			if (!Mat.from(post).isAir()) page.setItem(nextPageItemSlot, post);
		}
		updateFirstSlot();
	}

	public void updateFirstSlot() {
		// update item at first slot (because it tends to disappear sometimes for some reason, might be my fault)
		// TODO : investigate
		for (int pageIndex = 0; pageIndex < pages.size(); pageIndex++) {
			ClickeableItem firstItem = getItemInSlot(pageIndex, 0);
			if (firstItem != null) {
				pages.get(pageIndex).setItem(0, firstItem.getGUIStack());
			}
		}
	}

	// misc
	public boolean canUseSlot(int slot, boolean regular) {
		return slot > -1 && (regular ? slot <= maxRegularItemSlot : slot < size);
	}

	public ClickeableItem getItemInSlot(int pageIndex, int slot) {
		// regular
		if (pageIndex != -1) {
			for (ClickeableItem item : regularItems.values()) {
				if (item.getPageIndex() == pageIndex && item.getSlot() == slot) {
					return item;
				}
			}
		}
		// persistent
		for (ClickeableItem item : persistentItems.values()) {
			if (item.getSlot() == slot) {
				return item;
			}
		}
		// none
		return null;
	}

	// players
	public void open(Player player) {
		open(player, 0);
	}

	public void open(Player player, int pageIndex) {
		// create page if doesn't exist
		if (pageIndex == 0 && pages.isEmpty()) {
			createPage();
		}
		// verify page existence
		Inventory page = getPage(pageIndex);
		if (page == null) {
			Logger.log(Level.SEVERE, plugin.getName(), "Couldn't open page " + pageIndex + " of GUI '" + name + "' for player " + player.getName() + " (page index is out of bounds)");
			return;
		}
		// update first slot
		updateFirstSlot();
		// open GUI
		player.openInventory(page);
	}

	public List<Player> getViewers() {
		List<Player> viewers = new ArrayList<Player>();
		for (Inventory page : pages) {
			for (HumanEntity ent : page.getViewers()) {
				if (ent instanceof Player) {
					viewers.add((Player) ent);
				}
			}
		}
		return viewers;
	}

	// abstract
	/**
	 * @return true if the event must be cancelled
	 */
	protected boolean onBottomInventoryClick(Player player, ItemStack item, int slot) {
		// TODO : this, but it'll need reworks in all GUI constructors !clickTolerance.isClickTypeAllowed(InventoryClickType.BOTTOM);
		return true;
	}

	/**
	 * @return true if the inventory must be reopened
	 */
	protected boolean onClose(List<Player> players, int pageIndex) {
		return false;
	}

	protected void onRegister() {
	}

	protected void onUnregister() {
	}

	// ------------------------------------------------------------
	// Events
	// ------------------------------------------------------------

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void event(final InventoryCloseEvent event) {
		Inventory inventory = event.getInventory();
		if (pages.contains(inventory)) {
			// call onClose()
			Player player = (Player) event.getPlayer();
			boolean mustReopen = onClose(Utils.asList(player), getPageIndex(event.getInventory()));
			// reopen if must
			if (mustReopen && registered) {
				new BukkitRunnable() {
					@Override
					public void run() {
						event.getPlayer().openInventory(event.getInventory());
					}
				}.runTaskLater(plugin, 1L);
			}
			// unregister if no more viewers
			else if (unregisterOnClose) {
				new BukkitRunnable() {
					@Override
					public void run() {
						List<Player> viewers = getViewers();
						if (viewers.isEmpty()) {
							unregister();
						}
					}
				}.runTaskLater(plugin, 5L);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void event(InventoryClickEvent event) {
		Inventory inventory = event.getInventory();
		if (pages.contains(inventory)) {
			ItemStack clickedStack = event.getCurrentItem();
			InventoryClickType clickType = event.getClickedInventory() == null ? InventoryClickType.OUTSIDE : (inventory.equals(event.getClickedInventory()) ? (clickedStack == null ? InventoryClickType.OUTSIDE : InventoryClickType.TOP) : (clickedStack == null ? InventoryClickType.OUTSIDE : InventoryClickType.BOTTOM)); 
			// disallowed click type
			if (!clickTolerance.isClickTypeAllowed(clickType)) {
				event.setCancelled(true);
				return;
			}

			// vars
			int slot = event.getRawSlot();
			int pageIndex = getPageIndex(inventory);
			Player player = (Player) event.getWhoClicked();
			// previous page
			if (slot == previousPageItemSlot) {
				if (pageIndex - 1 >= 0) {
					open(player, pageIndex - 1);
					return;
				}
			}
			// next page
			if (slot == nextPageItemSlot) {
				if (pageIndex + 1 < pages.size()) {
					open(player, pageIndex + 1);
					return;
				}
			}
			// bottom click
			if (clickType.equals(InventoryClickType.BOTTOM)) {
				try {
					event.setCancelled(onBottomInventoryClick(player, clickedStack, slot));
				} catch (Throwable exception) {
					exception.printStackTrace();
					GCore.inst().error("An unknown error occured while processing click at slot " + slot + ", page " + pageIndex);
					event.setCancelled(true);
				}
				return;
			}
			// item
			try {
				ClickeableItem item = getItemInSlot(pageIndex, slot);
				event.setCancelled(item == null ? true : item.onClick(player, event.getClick(), this, pageIndex));
			} catch (Throwable exception) {
				exception.printStackTrace();
				GCore.inst().error("An unknown error occured while processing click at slot " + slot + ", page " + pageIndex);
				event.setCancelled(true);
			}
			return;
		}
	}

	// ------------------------------------------------------------
	// Static methods
	// ------------------------------------------------------------

	public static Mat PLAYER_HEAD_MATERIAL = Mat.JACK_O_LANTERN;

	public static int firstBlank(Inventory inventory) {
		ItemStack[] contents = inventory.getContents();
		for (int slot = 0; slot < contents.length; slot++) {
			ItemStack it = contents[slot];
			if (Mat.from(it).isAir()) {
				return slot;
			}
		}
		return -1;
	}

	public static int getAvailableSpace(Inventory inventory, Mat type) {
		int space = 0, max = inventory.getMaxStackSize();
		for (ItemStack item : inventory.getContents()) {
			if (type.isMat(item)) {
				space += item == null ? max : max - item.getAmount();
			}
		}
		return space;
	}

	public static boolean containsItemType(Inventory inv, Mat type, int amount) {
		int invAmount = 0;
		for (ItemStack item : inv.getContents()) {
			if (type.isMat(item)) {
				invAmount += item.getAmount();
			}
		}
		return invAmount >= amount;
	}

	public static boolean containsItemName(Inventory inv, String name, int amount) {
		int invAmount = 0;
		for (ItemStack item : inv.getContents()) {
			if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals(name)) {
				invAmount += item.getAmount();
			}
		}
		return invAmount >= amount;
	}

	public static boolean containsItem(Inventory inventory, ItemStack itemToCheck, int amount) {
		return countItem(inventory, itemToCheck) >= amount;
	}

	public static int countItem(Inventory inventory, ItemStack itemToCheck) {
		int invAmount = 0;
		for (ItemStack item : inventory.getContents()) {
			if (isSimilar(item, itemToCheck)) {
				invAmount += item.getAmount();
			}
		}
		return invAmount;
	}

	public static void removeItem(Inventory inventory, ItemStack itemToRemove, int amount) {
		for (int slot = 0; slot < inventory.getSize(); slot++) {
			ItemStack item = inventory.getItem(slot);
			if (isSimilar(item, itemToRemove)) {
				int itemAmount = item.getAmount();

				if (amount >= itemAmount) {
					amount -= itemAmount;
					inventory.setItem(slot, null);
				} else {
					itemAmount -= amount;
					amount = 0;
					item.setAmount(itemAmount);
					inventory.setItem(slot, item);
				}

				if (amount <= 0) {
					return;
				}
			}
		}
	}

	public static ItemStack removeLore(ItemStack item) {
		if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
			ItemMeta meta = item.getItemMeta();
			meta.setLore(null);
			item.setItemMeta(meta);
		}
		return item;
	}

	public static ItemData getPlayerHead(int slot, String name, List<String> lore) {
		return getPlayerHead(slot, "player_" + name, name, lore);
	}

	public static ItemData getPlayerHead(int slot, String itemId, String name, List<String> lore) {
		return getPlayerHead(slot, itemId, "§6", name, lore);
	}

	public static ItemData getPlayerHead(int slot, String itemId, String nameColor, String name, List<String> lore) {
		if (!GCore.inst().getConfiguration().getBoolean("use_fake_player_heads", false)) {
			ItemStack stack = new ItemStack(Mat.PLAYER_HEAD.getCurrentMaterial(), 1, (short) 3);
			SkullMeta meta = (SkullMeta) stack.getItemMeta();
			meta.setOwner(name);
			meta.setDisplayName(nameColor + name);
			if (lore != null) meta.setLore(lore);
			stack.setItemMeta(meta);
			return new ItemData(itemId, slot, stack);
		} else {
			return new ItemData(itemId, slot, PLAYER_HEAD_MATERIAL, 1, "§6" + name, lore);
		}
	}

	/*public static ItemData getPlayerHead(int slot, String name, List<String> lore) {
		ItemStack item = Mat.PLAYER_HEAD.getNewCurrentStack();
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwner(name);
		meta.setDisplayName("§6§b" + name);
		if (lore != null) meta.setLore(lore);
		item.setItemMeta(meta);
		return new ItemData("player_" + name, slot, item);
	}*/

	public static boolean isSimilar(ItemStack i1, ItemStack i2) {
		return i1 == null ? i2 == null : new ItemData("item", i1).isSimilar(i2);
	}

}
