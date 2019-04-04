package com.guillaumevdn.gcore.lib.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.messenger.Replacer;
import com.guillaumevdn.gcore.lib.util.Utils;
import com.guillaumevdn.gcore.lib.versioncompat.Compat;

public class ItemData implements Comparable<ItemData>, Cloneable {

	// fields : settings
	private String id;
	private boolean enabled = true;
	private int slot = -1;
	private double chance = -1D;
	private int maxAmount = 0;
	private boolean slotless = false;
	private boolean hideFlags = false;

	// fields : item
	private Mat type = Mat.AIR;
	private int amount = 1;
	private Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
	private String name = null;
	private List<String> lore = null;
	private Object customNbt = null;
	private boolean unbreakable = false;
	private transient ItemStack build = null;

	// bases
	public ItemData(String id) {
		this(id, -1, Mat.AIR, (short) 0, 1, null, null, null);
	}

	public ItemData(String id, int slot, double chance, ItemStack item) {
		this(id, slot, item);
		setChance(chance);
	}

	public ItemData(String id, int slot, double chance, int maxAmount, ItemStack item) {
		this(id, slot, item);
		setChance(chance);
		setMaxAmount(maxAmount);
	}

	public ItemData(String id, ItemStack item) {
		this(id, -1, item);
	}

	public ItemData(String id, int slot, ItemStack item) {
		this(id, slot, item == null ? Mat.AIR : Mat.from(item.getType()),
				item == null ? (short) 0 : item.getDurability(),
						item == null ? 1 : item.getAmount(), item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName() ? null : item.getItemMeta().getDisplayName(),
								item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore() ? null : Utils.asList(item.getItemMeta().getLore()),
										item == null ? null : Utils.asMapCopy(item.getEnchantments()));
		this.build = item;
		try {
			this.customNbt = Compat.INSTANCE.getNbt(item);
		} catch (Throwable ignored) {
			this.customNbt = null;
		}
	}

	public ItemData(Mat type, int amount, String name, List<String> lore) {
		this(UUID.randomUUID().toString().split("-")[0], -1, type, 0, amount, name, lore, null);
	}

	public ItemData(String id, int slot, Mat type, int amount, String name, List<String> lore) {
		this(id, slot, type, 0, amount, name, lore, null);
	}

	public ItemData(String id, int slot, Mat type, int durability, int amount, String name, List<String> lore) {
		this(id, slot, type, durability, amount, name, lore, null);
	}

	public ItemData(String id, int slot, Mat type, int durability, int amount, String name, List<String> lore, Map<Enchantment, Integer> enchants) {
		this.id = id;
		this.slot = slot;
		this.type = type.getDurability() != (int) durability ? Mat.from(type.getModernName(), durability) : type;
		this.amount = amount;
		this.name = name == null || name.isEmpty() ? null : Utils.format(name);
		this.lore = lore == null || lore.isEmpty() ? null : Utils.format(lore);
		if (enchants != null) {
			this.enchants.putAll(enchants);
		}
		setMaxAmount(amount);
	}

	// get
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public double getChance() {
		return chance;
	}

	public void setChance(double chance) {
		this.chance = chance;
	}

	public int getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(int maxAmount) {
		this.maxAmount = maxAmount;
	}

	public boolean isSlotless() {
		return slotless;
	}

	public void setSlotless(boolean slotless) {
		this.slotless = slotless;
	}

	public boolean isHideFlags() {
		return hideFlags;
	}

	public ItemData setHideFlags(boolean hideFlags) {
		this.hideFlags = hideFlags;
		return this;
	}

	public Mat getType() {
		return type;
	}

	public void setType(Mat type) {
		this.type = type;
		if (build != null) rebuildItem(true);
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
		if (build != null) rebuildItem(true);
	}

	public Map<Enchantment, Integer> getEnchants() {
		return enchants;
	}

	public void setEnchant(Enchantment enchant, int level) {
		enchants.put(enchant, level);
		if (build != null) rebuildItem(true);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		if (build != null) rebuildItem(true);
	}

	public List<String> getLore() {
		return lore;
	}

	public void setLore(List<String> lore) {
		this.lore = lore;
		if (build != null) rebuildItem(true);
	}

	public Object getCustomNbt() {
		return customNbt;
	}

	public void setCustomNbt(Object customNbt) {
		this.customNbt = customNbt;
		if (build != null) rebuildItem(true);
	}

	public boolean isUnbreakable() {
		return unbreakable;
	}

	public void setUnbreakable(boolean unbreakable) {
		this.unbreakable = unbreakable;
		if (build != null) rebuildItem(true);
	}

	public boolean isValid() {
		if (build == null) {
			rebuildItem(false);
		}
		return build != null;
	}

	public ItemStack getItemStack() {
		if (build == null) {
			rebuildItem(true);
		}
		return build;
	}

	public ItemStack getItemStack(Object... replacements) {
		ItemStack item = getItemStack();
		if (item != null) {
			item = item.clone();
			ItemMeta meta = item.getItemMeta();
			if (meta != null) {
				String name = meta.getDisplayName();
				List<String> lore = meta.getLore() == null ? Utils.emptyList() : meta.getLore();
				if (replacements != null && replacements.length > 0) {
					Replacer replacer = new Replacer(replacements);
					name = replacer.apply(name);
					lore = replacer.apply(lore);
				}
				meta.setDisplayName(name);
				meta.setLore(lore);
				item.setItemMeta(meta);
			}
		}
		return item;
	}

	// methods
	public void rebuildItem(boolean logError) {
		try {
			// type (this type is the one with the correct durability so don't reapply it after building a new stack)
			build = type.getNewCurrentStack();
			// amount
			build.setAmount(amount);
			// meta
			ItemMeta meta = Bukkit.getItemFactory().getItemMeta(type.getCurrentMaterial());
			if (meta != null) {
				// name and lore
				meta.setDisplayName(name);
				meta.setLore(lore);
				// unbreakable
				if (unbreakable) {
					try {
						meta.spigot().setUnbreakable(unbreakable);
					} catch (UnsupportedOperationException exception) {
						GCore.inst().error("Trying to set item with type " + type.toString() + " unbreakable, but it's not supported");
					}
				}
				// flags
				if (hideFlags) {
					meta = Compat.INSTANCE.addItemFlags(meta);
				}
				// set meta
				build.setItemMeta(meta);
			}
			// enchants
			build.addUnsafeEnchantments(enchants);
			// nbt
			if (customNbt != null) {
				build = Compat.INSTANCE.setNbt(build, customNbt);
			}
		} catch (Throwable exception) {
			if (logError) {
				exception.printStackTrace();
				GCore.inst().error("Couldn't build item " + id);
			}
		}
	}

	/**
	 * @return an item with remaining amount if there wasn't enough place in the player's inventory, or null if no extra drop
	 */
	public Item give(Player player) {
		ItemStack item = getItemStack();
		if (item == null) return null;
		// add to inventory
		int count = item.getAmount();
		for (int slot = 0; slot < player.getInventory().getSize() && count > 0; slot++) {
			ItemStack slotItem = player.getInventory().getItem(slot);
			if (slotItem == null || Mat.from(slotItem).isAir()) {
				int newSlotCount = count;
				if (newSlotCount > item.getMaxStackSize()) {
					count = newSlotCount - item.getMaxStackSize();
					slotItem = item.clone();
					slotItem.setAmount(slotItem.getMaxStackSize());
					player.getInventory().setItem(slot, slotItem);
					continue;
				} else {
					slotItem = item.clone();
					slotItem.setAmount(newSlotCount);
					player.getInventory().setItem(slot, slotItem);
					player.updateInventory();
					return null;
				}
			}
			if (isSimilar(slotItem)) {
				int newSlotCount = slotItem.getAmount() + count;
				if (newSlotCount > slotItem.getMaxStackSize()) {
					count = newSlotCount - slotItem.getMaxStackSize();
					slotItem.setAmount(slotItem.getMaxStackSize());
					continue;
				} else {
					slotItem.setAmount(newSlotCount);
					player.updateInventory();
					return null;
				}
			}
		}
		// remaining, so drop it
		item.setAmount(count);
		return player.getWorld().dropItem(player.getEyeLocation(), item);
	}

	public boolean contains(Inventory inventory) {
		return contains(inventory, amount);
	}

	public boolean contains(Inventory inventory, int amount) {
		return count(inventory) >= amount;
	}

	public int count(Inventory inventory) {
		int count = 0;
		for (ItemStack it : inventory.getContents()) {
			if (isSimilar(it, false)) {
				count += it.getAmount();
			}
		}
		return count;
	}

	public void remove(Inventory inventory) {
		remove(inventory, amount);
	}

	public void remove(Inventory inventory, int amount) {
		for (int slot = 0; slot < inventory.getSize(); slot++) {
			ItemStack item = inventory.getItem(slot);
			if (isSimilar(item, false)) {
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

	public boolean isAtLeast(ItemStack item) {
		return isSimilar(item) && item.getAmount() >= amount;
	}

	public boolean isSimilar(ItemStack item) {
		return isSimilar(item, false);
	}

	public boolean isSimilar(ItemStack item, boolean checkAmount) {
		// null
		if (item == null) {
			return false;
		}
		// bukkit
		if (item == build || (checkAmount ? item.equals(build) : item.isSimilar(build))) {
			return true;
		}
		// type
		if (!type.isMat(item)) {
			return false;
		}
		// amount
		if (checkAmount && item.getAmount() != amount) {
			return false;
		}
		// enchants
		Map<Enchantment, Integer> itemEnchants = item.getEnchantments();
		if (enchants.size() != itemEnchants.size()) {
			return false;
		}
		for (Enchantment enchant : enchants.keySet()) {
			if (enchants.get(enchant) != itemEnchants.get(enchant)) {
				return false;
			}
		}
		// name
		if (name != null && !(item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals(name))) {
			return false;
		}
		// lore
		if (lore != null && !lore.isEmpty() && !(item.hasItemMeta() && item.getItemMeta().hasLore() && item.getItemMeta().getLore().equals(lore))) {
			return false;
		}
		// custom nbt
		if (customNbt != null && !customNbt.equals(Compat.INSTANCE.getNbt(item))) {
			return false;
		}
		// unbreakable
		if (unbreakable && !(item.hasItemMeta() && item.getItemMeta().spigot().isUnbreakable())) {
			return false;
		}
		// equals
		return true;
	}

	// misc
	/**
	 * @param object the object to check
	 * @return true if the id of this item is the same as the object's id (if the argument is an ItemData object)
	 */
	@Override
	public boolean equals(Object object) {
		if (object == null || !object.getClass().equals(getClass())) {
			return false;
		}
		ItemData other = (ItemData) object;
		if (other == this) {
			return true;
		}
		return Utils.equals(id, other.id);// id might be null
	}

	@Override
	public int compareTo(ItemData other) {
		return Integer.compare(slot, other.slot);
	}

	@Override
	public ItemData clone() {
		ItemData clone = new ItemData(id, slot, type, type.getDurability(), amount, name, lore == null ? null : Utils.asList(lore), Utils.asMapCopy(enchants));
		clone.enabled = enabled;
		clone.chance = chance;
		clone.slotless = slotless;
		clone.unbreakable = unbreakable;
		clone.customNbt = customNbt;
		clone.build = build == null ? null : build.clone();
		return clone;
	}

	public ItemData cloneWithId(String id) {
		ItemData clone = clone();
		clone.setId(id);
		return clone;
	}

	@Override
	public String toString() {
		return "ItemData{id=" + id + ",enabled=" + enabled + ",slot=" + slot + ",slotless=" + slotless + ",chance=" + chance + ",type=" + type + ",amount=" + amount
				+ ",enchants=" + enchants.toString() + ",name=" + name + ",lore=" + (lore == null ? "null" : Utils.asString(lore)) + ",nbt=" + !(customNbt == null) + ",unbreakable=" + unbreakable + "}";
	}

}
