package com.guillaumevdn.gcore.lib.gui.struct;

import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

/**
 * @author GuillaumeVDN
 */
public enum GUIType {

	CHEST_1_ROW("minecraft:container", "GENERIC_9X1", 9, 9, 8, title -> Bukkit.createInventory(null, 9, title)),
	CHEST_2_ROW("minecraft:container", "GENERIC_9X2", 18, 18, 8, title -> Bukkit.createInventory(null, 18, title)),
	CHEST_3_ROW("minecraft:container", "GENERIC_9X3", 27, 27, 17, title -> Bukkit.createInventory(null, 27, title)),
	CHEST_4_ROW("minecraft:container", "GENERIC_9X4", 36, 36, 26, title -> Bukkit.createInventory(null, 36, title)),
	CHEST_5_ROW("minecraft:container", "GENERIC_9X5", 45, 45, 35, title -> Bukkit.createInventory(null, 45, title)),
	CHEST_6_ROW("minecraft:container", "GENERIC_9X6", 54, 54, 44, title -> Bukkit.createInventory(null, 54, title)),
	DISPENSER("minecraft:dispenser", "GENERIC_3X3", 9, 9, 8, title -> Bukkit.createInventory(null, InventoryType.DISPENSER)),
	ANVIL("minecraft:anvil", "ANVIL", 3, 0, 2, title -> Bukkit.createInventory(null, InventoryType.ANVIL)),
	BEACON("minecraft:beacon", "BEACON", 1, 1, 0, title -> Bukkit.createInventory(null, InventoryType.BEACON)),
	BLAST_FURNACE(null, "BLAST_FURNACE", 3, 3, 2, title -> Bukkit.createInventory(null, InventoryType.BLAST_FURNACE)),
	BREWING_STAND("minecraft:brewing_stand", "BREWING_STAND", 5, 5, 4, title -> Bukkit.createInventory(null, InventoryType.BREWING)),
	CRAFTING("minecraft:crafting_table", "CRAFTING", 1, 0, 00, title -> Bukkit.createInventory(null, InventoryType.CRAFTING)),
	ENCHANTMENT("minecraft:enchanting_table", "ENCHANTMENT", 2, 0, 1, title -> Bukkit.createInventory(null, InventoryType.ENCHANTING)),
	FURNACE("minecraft:furnace", "FURNACE", 3, 3, 2, title -> Bukkit.createInventory(null, InventoryType.FURNACE)),
	GRINDSTONE(null, "GRINDSTONE", 3, 3, 2, title -> Bukkit.createInventory(null, InventoryType.GRINDSTONE)),
	HOPPER(null, "HOPPER", 5, 5, 4, title -> Bukkit.createInventory(null, InventoryType.HOPPER))
	;

	private String pre114ContainerId, containerId;
	private int size, regularItemSlotsEnd, pre114PacketSlots, previousPageItemSlot = -1, nextPageItemSlot = -1, backItemSlot = -1;
	private Function<String, Inventory> createVanilla;

	GUIType(String pre114ContainerId, String containerId, int size, int pre114PacketSlots, int regularItemSlotsEnd, Function<String, Inventory> createVanilla) {
		this.pre114ContainerId = pre114ContainerId;
		this.containerId = containerId;
		this.size = size;
		this.pre114PacketSlots = pre114PacketSlots;
		this.regularItemSlotsEnd = regularItemSlotsEnd;
		this.createVanilla = createVanilla;
	}

	// get
	public String getContainerId() {
		return containerId;
	}

	public String getPre114ContainerId() {
		return pre114ContainerId;
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

	// set
	public void setBackItemSlot(int backItemSlot) {
		this.backItemSlot = backItemSlot;
	}

	public void setPreviousPageItemSlot(int previousPageItemSlot) {
		this.previousPageItemSlot = previousPageItemSlot;
	}

	public void setNextPageItemSlot(int nextPageItemSlot) {
		this.nextPageItemSlot = nextPageItemSlot;
	}

	// do
	public Inventory createVanilla(String title) {
		return createVanilla.apply(title);
	}

}
