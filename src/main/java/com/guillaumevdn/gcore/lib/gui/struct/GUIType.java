package com.guillaumevdn.gcore.lib.gui.struct;

import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

/**
 * @author GuillaumeVDN
 */
public enum GUIType {

	CHEST_1_ROW("minecraft:container", "GENERIC_9X1", "a", 9, 9, 8, title -> Bukkit.createInventory(null, 9, title)),
	CHEST_2_ROW("minecraft:container", "GENERIC_9X2", "b", 18, 18, 8, title -> Bukkit.createInventory(null, 18, title)),
	CHEST_3_ROW("minecraft:container", "GENERIC_9X3", "c", 27, 27, 17, title -> Bukkit.createInventory(null, 27, title)),
	CHEST_4_ROW("minecraft:container", "GENERIC_9X4", "d", 36, 36, 26, title -> Bukkit.createInventory(null, 36, title)),
	CHEST_5_ROW("minecraft:container", "GENERIC_9X5", "e", 45, 45, 35, title -> Bukkit.createInventory(null, 45, title)),
	CHEST_6_ROW("minecraft:container", "GENERIC_9X6", "f", 54, 54, 44, title -> Bukkit.createInventory(null, 54, title)),
	DISPENSER("minecraft:dispenser", "GENERIC_3X3", "g", 9, 9, 8, title -> Bukkit.createInventory(null, InventoryType.DISPENSER, title)),
	ANVIL("minecraft:anvil", "ANVIL", "h", 3, 0, 2, title -> Bukkit.createInventory(null, InventoryType.ANVIL, title)),
	BEACON("minecraft:beacon", "BEACON", "i", 1, 1, 0, title -> Bukkit.createInventory(null, InventoryType.BEACON, title)),
	BLAST_FURNACE(null, "BLAST_FURNACE", "j", 3, 3, 2, title -> Bukkit.createInventory(null, InventoryType.BLAST_FURNACE, title)),
	BREWING_STAND("minecraft:brewing_stand", "BREWING_STAND", "k", 5, 5, 4, title -> Bukkit.createInventory(null, InventoryType.BREWING, title)),
	WORKBENCH("minecraft:crafting_table", "WORKBENCH", "l", 10, 10, 9, title -> Bukkit.createInventory(null, InventoryType.WORKBENCH, title)),
	ENCHANTMENT("minecraft:enchanting_table", "ENCHANTMENT", "m", 2, 0, 1, title -> Bukkit.createInventory(null, InventoryType.ENCHANTING, title)),
	FURNACE("minecraft:furnace", "FURNACE", "n", 3, 3, 2, title -> Bukkit.createInventory(null, InventoryType.FURNACE, title)),
	GRINDSTONE(null, "GRINDSTONE", "o", 3, 3, 2, title -> Bukkit.createInventory(null, InventoryType.GRINDSTONE, title)),
	HOPPER(null, "HOPPER", "p", 5, 5, 4, title -> Bukkit.createInventory(null, InventoryType.HOPPER, title));

	private String containerIdPre114, containerId117, containerId;
	private int size, regularItemSlotsEnd, pre114PacketSlots, previousPageItemSlot = -1, nextPageItemSlot = -1, backItemSlot = -1;
	private Function<String, Inventory> createVanilla;

	GUIType(String containerIdPre114, String containerId, String containerId117, int size, int pre114PacketSlots, int regularItemSlotsEnd,
			Function<String, Inventory> createVanilla) {
		this.containerIdPre114 = containerIdPre114;
		this.containerId = containerId;
		this.containerId117 = containerId117;
		this.size = size;
		this.pre114PacketSlots = pre114PacketSlots;
		this.regularItemSlotsEnd = regularItemSlotsEnd;
		this.createVanilla = createVanilla;
	}

	// ----- get
	public String getContainerId() {
		return containerId;
	}

	public String getContainerIdPre114() {
		return containerIdPre114;
	}

	public String getContainerId117() {
		return containerId117;
	}

	public int getSize() {
		return size;
	}

	public int getPre114PacketSlots() {
		return pre114PacketSlots;
	}

	public int getRegularItemSlotsEnd() {
		return regularItemSlotsEnd;
	}

	public int getPreviousPageItemSlot() {
		return previousPageItemSlot;
	}

	public int getNextPageItemSlot() {
		return nextPageItemSlot;
	}

	public int getBackItemSlot() {
		return backItemSlot;
	}

	// ----- set
	public void setBackItemSlot(int backItemSlot) {
		this.backItemSlot = backItemSlot;
	}

	public void setPreviousPageItemSlot(int previousPageItemSlot) {
		this.previousPageItemSlot = previousPageItemSlot;
	}

	public void setNextPageItemSlot(int nextPageItemSlot) {
		this.nextPageItemSlot = nextPageItemSlot;
	}

	// ----- do
	public Inventory createVanilla(String title) {
		return createVanilla.apply(title);
	}

}
