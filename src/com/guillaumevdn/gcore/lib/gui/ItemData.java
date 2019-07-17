package com.guillaumevdn.gcore.lib.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.messenger.Replacer;
import com.guillaumevdn.gcore.lib.util.Utils;
import com.guillaumevdn.gcore.lib.versioncompat.Compat;
import com.guillaumevdn.gcore.libs.com.google.gson.Gson;
import com.guillaumevdn.gcore.libs.com.google.gson.GsonBuilder;
import com.guillaumevdn.gcore.libs.com.google.gson.TypeAdapter;
import com.guillaumevdn.gcore.libs.com.google.gson.adapter.AdapterClassFactory;
import com.guillaumevdn.gcore.libs.com.google.gson.adapter.AdapterEnchantment;
import com.guillaumevdn.gcore.libs.com.google.gson.adapter.AdapterMat;
import com.guillaumevdn.gcore.libs.com.google.gson.adapter.AdapterPotionEffectType;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonReader;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonToken;
import com.guillaumevdn.gcore.libs.com.google.gson.stream.JsonWriter;

public class ItemData implements Comparable<ItemData>, Cloneable {

	// serialization
	public static class Adapter extends TypeAdapter<ItemData> {

		@Override
		public ItemData read(JsonReader reader) throws IOException {
			if (reader.peek() == JsonToken.NULL) {
				reader.nextNull();
				return null;
			} else if (reader.peek() == JsonToken.STRING) {
				DataWrapper wrapper = GCore.GSON.fromJson(reader.nextString(), DataWrapper.class);
				return wrapper != null ? wrapper.toItemData() : null;
			} else {
				return GSON_ITEMDATA.fromJson(reader, ItemData.class);
			}
		}

		@Override
		public void write(JsonWriter writer, ItemData obj) throws IOException {
			if (obj == null) {
				writer.nullValue();
			} else {
				writer.value(GSON_ITEMDATA.toJson(new DataWrapper(obj)));
			}
		}

	}

	private static class DataWrapper {

		private String id = null;
		private Boolean enabled = null;
		private Integer slot = null;
		private Double chance = null;
		private Integer maxAmount = null;
		private Boolean hideFlags = null;
		private Mat type = null;
		private Integer amount = null;
		private List<PotionEffect> effects = null;
		private Map<Enchantment, Integer> enchants = null;
		private String name = null;
		private List<String> lore = null;
		private String customNbt = null;
		private Boolean unbreakable = null;

		private DataWrapper(ItemData item) {
			if (item.id != null) id = item.id;
			if (!item.enabled) enabled = false;
			if (item.slot != -1) slot = item.slot;
			if (item.chance > 0d) chance = item.chance;
			if (item.maxAmount > 1) maxAmount = item.maxAmount;
			if (item.hideFlags) hideFlags = true;
			if (item.type != null) type = item.type;
			if (item.amount != 1) amount = item.amount;
			if (item.effects != null && !item.effects.isEmpty()) effects = item.effects;
			if (item.enchants != null && !item.enchants.isEmpty()) enchants = item.enchants;
			if (item.name != null) name = item.name;
			if (item.lore != null && !item.lore.isEmpty()) lore = item.lore;
			if (item.customNbt != null) {
				try {
					customNbt = Compat.INSTANCE.serializeNbt(item.customNbt);
				} catch (IOException exception) {
					exception.printStackTrace();
					GCore.inst().error("Couldn't serialize custom NBT for item " + item.id + " (" + item.getType() + ")");
				}
			}
			if (item.unbreakable) unbreakable = true;
		}

		private ItemData toItemData() {
			ItemData item = new ItemData(id);
			if (enabled != null) item.setEnabled(enabled);
			if (slot != null) item.setSlot(slot);
			if (chance != null) item.setChance(chance);
			if (maxAmount != null) item.setMaxAmount(maxAmount);
			if (hideFlags != null) item.setHideFlags(hideFlags);
			if (type != null) item.setType(type);
			if (amount != null) item.setAmount(amount);
			if (effects != null) item.effects.addAll(effects);
			if (enchants != null) item.enchants.putAll(enchants);
			if (name != null) item.setName(name);
			if (lore != null) item.setLore(lore);
			if (customNbt != null) {
				try {
					item.setCustomNbt(Compat.INSTANCE.unserializeNbt(customNbt));
				} catch (IOException exception) {
					exception.printStackTrace();
					GCore.inst().error("Couldn't unserialize custom NBT for item " + id + " (" + type + ")");
				}
			}
			if (unbreakable != null) item.setUnbreakable(unbreakable);
			return item;
		}

	}

	private static Gson GSON_ITEMDATA = new GsonBuilder()
			.registerTypeAdapterFactory(new AdapterClassFactory())
			.registerTypeAdapter(Enchantment.class, new AdapterEnchantment())
			.registerTypeAdapter(Mat.class, new AdapterMat())
			.registerTypeAdapter(PotionEffectType.class, new AdapterPotionEffectType())
			.enableComplexMapKeySerialization()
			.disableInnerClassSerialization()
			.serializeSpecialFloatingPointValues()
			.create();

	// fields : settings
	private String id;
	private boolean enabled = true;
	private int slot = -1;
	private double chance = -1d;
	private int maxAmount = 0;
	private boolean hideFlags = false;

	// fields : item
	private Mat type = Mat.AIR;
	private int amount = 1;
	private List<PotionEffect> effects = new ArrayList<PotionEffect>();
	private Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
	private String name = null;
	private List<String> lore = null;
	private Object customNbt = null;
	private boolean unbreakable = false;
	private transient ItemStack build = null;

	// bases
	public ItemData(String id) {
		this(id, -1, Mat.AIR, (short) 0, 1, null, null, null, null);
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

	public ItemData(ItemStack item) {
		this(null, item);
	}

	public ItemData(String id, ItemStack item) {
		this(id, -1, item);
	}

	public ItemData(String id, int slot, ItemStack item) {
		this(id,
				slot,
				item == null ? Mat.AIR : Mat.from(item),
						item == null ? (short) 0 : item.getDurability(),
								item == null ? 1 : item.getAmount(),
										item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName() ? null : item.getItemMeta().getDisplayName(),
												item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore() ? null : Utils.asList(item.getItemMeta().getLore()),
														item == null || !item.hasItemMeta() || !(item.getItemMeta() instanceof PotionMeta) ? null : Utils.asList(((PotionMeta) item.getItemMeta()).getCustomEffects()),
																item == null ? null : Utils.asMapCopy(item.getEnchantments()));
		this.build = item == null ? null : item.clone();
		try {
			this.customNbt = Compat.INSTANCE.getNbt(item);
		} catch (Throwable ignored) {
			this.customNbt = null;
		}
	}

	public ItemData(Mat type, int amount, String name, List<String> lore) {
		this(UUID.randomUUID().toString().split("-")[0], -1, type, 0, amount, name, lore, null, null);
	}

	public ItemData(String id, int slot, Mat type, int amount, String name, List<String> lore) {
		this(id, slot, type, 0, amount, name, lore, null, null);
	}

	public ItemData(String id, int slot, Mat type, int durability, int amount, String name, List<String> lore) {
		this(id, slot, type, durability, amount, name, lore, null, null);
	}

	public ItemData(String id, int slot, Mat type, int durability, int amount, String name, List<String> lore, List<PotionEffect> effects, Map<Enchantment, Integer> enchants) {
		this.id = id;
		this.slot = slot;
		this.type = type.getDurability() != (int) durability ? Mat.from(type.getModernName(), durability) : type;
		this.amount = amount;
		this.name = name == null || name.isEmpty() ? null : Utils.format(name);
		this.lore = lore == null || lore.isEmpty() ? null : Utils.format(lore);
		if (effects != null) {
			this.effects.addAll(effects);
		}
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
		if (build != null) build.setAmount(amount);
	}

	public List<PotionEffect> getEffects() {
		return effects;
	}

	public void addPotionEffect(PotionEffect effect) {
		effects.add(effect);
		if (build != null) rebuildItem(true);
	}

	public void removePotionEffect(PotionEffect effect) {
		effects.remove(effect);
		if (build != null) rebuildItem(true);
	}

	public Map<Enchantment, Integer> getEnchants() {
		return enchants;
	}

	public void setEnchant(Enchantment enchant, int level) {
		if (level <= 0) {
			enchants.remove(enchant);
		} else {
			enchants.put(enchant, level);
		}
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
			// set nbt first -> some settings could be stored in it ! so we'll override it
			// nbt
			if (customNbt != null) {
				build = Compat.INSTANCE.setNbt(build, customNbt);
			}
			// amount
			build.setAmount(amount);
			// meta
			ItemMeta meta = Bukkit.getItemFactory().getItemMeta(type.getCurrentMaterial());
			if (meta != null) {
				// unbreakable
				if (unbreakable) {
					Compat.INSTANCE.setUnbreakable(build);
					meta = build.getItemMeta();// the meta changed so update it
				}
				// name and lore
				meta.setDisplayName(name);
				meta.setLore(lore);
				// flags
				if (hideFlags) {
					meta = Compat.INSTANCE.addItemFlags(meta);
				}
				// effects
				if (!effects.isEmpty() && meta instanceof PotionMeta) {
					PotionMeta potion = (PotionMeta) meta;
					potion.clearCustomEffects();
					for (PotionEffect effect : effects) {
						potion.addCustomEffect(effect, true);
					}
				}
				// set meta
				build.setItemMeta(meta);
			}
			// enchants
			build.addUnsafeEnchantments(enchants);
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
		return give(player, player.getEyeLocation());
	}

	/**
	 * @return an item with remaining amount if there wasn't enough place in the player's inventory, or null if no extra drop
	 */
	public Item give(Player player, Location dropLocation) {
		return give(player, dropLocation, null, true);
	}


	/**
	 * @return an item with remaining amount if there wasn't enough place in the player's inventory, or null if no extra drop
	 */
	public Item give(Player player, Location dropLocation, Integer forcedAmount, boolean drop) {
		ItemStack item = getItemStack();
		if (item == null) return null;
		// add to inventory
		int count = forcedAmount != null ? forcedAmount : item.getAmount();
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
		if (drop) {
			ItemStack clone = item.clone();
			clone.setAmount(count);
			return player.getWorld().dropItem(dropLocation, clone);
		}
		// don't drop
		return null;
	}

	public boolean contains(Inventory inventory) {
		return contains(inventory, amount);
	}

	public boolean contains(Inventory inventory, int amount) {
		return count(inventory) >= amount;
	}

	public boolean contains(Inventory inventory, int amount, boolean checkDurability, boolean exactMatch, double minDurabilityIfNotUnbreakable) {
		return count(inventory, checkDurability, exactMatch, minDurabilityIfNotUnbreakable) >= amount;
	}

	public int count(Inventory inventory) {
		return count(inventory, true, true);
	}

	public int count(Inventory inventory, boolean checkDurability, boolean exactMatch) {
		return count(inventory, checkDurability, exactMatch, 0d);
	}

	public int count(Inventory inventory, boolean checkDurability, boolean exactMatch, double minDurabilityIfNotUnbreakable) {
		int count = 0;
		for (ItemStack it : inventory.getContents()) {
			if (isSimilar(it, checkDurability, exactMatch, false)) {
				// check durability
				if (minDurabilityIfNotUnbreakable > 0d && !isUnbreakable() && getType().getDurability() / ((double) getType().getCurrentMaterial().getMaxDurability()) * 100d < minDurabilityIfNotUnbreakable) {
					continue;
				}
				// add count
				count += it.getAmount();
			}
		}
		return count;
	}

	public int countFreeFor(Inventory inventory, boolean checkDurability, boolean exactMatch, double minDurabilityIfNotUnbreakable) {
		int free = 0;
		for (ItemStack it : inventory.getContents()) {
			if (it == null || Mat.from(it.getType()).isAir()) {
				free += getItemStack().getMaxStackSize();
			} else if (isSimilar(it, checkDurability, exactMatch, false)) {
				// check durability
				if (minDurabilityIfNotUnbreakable > 0d && !isUnbreakable() && getType().getDurability() / ((double) getType().getCurrentMaterial().getMaxDurability()) * 100d < minDurabilityIfNotUnbreakable) {
					continue;
				}
				// add count
				free += it.getMaxStackSize() - it.getAmount();
			}
		}
		return free;
	}

	public int remove(Inventory inventory) {
		return remove(inventory, amount);
	}

	public int remove(Inventory inventory, int amount) {
		return remove(inventory, amount, true, true, 0d);
	}

	public int remove(Inventory inventory, int amount, boolean checkDurability, boolean exactMatch, double minDurabilityIfNotUnbreakable) {
		int initAmount = 0;
		for (int slot = 0; slot < inventory.getSize(); slot++) {
			ItemStack item = inventory.getItem(slot);
			if (isSimilar(item, checkDurability, exactMatch, false)) {
				// check durability
				if (minDurabilityIfNotUnbreakable > 0d && !isUnbreakable() && getType().getDurability() / ((double) getType().getCurrentMaterial().getMaxDurability()) * 100d < minDurabilityIfNotUnbreakable) {
					continue;
				}
				// remove
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
					break;
				}
			}
		}
		return initAmount - amount;
	}

	public boolean isAtLeast(ItemStack item) {
		return isSimilar(item) && item.getAmount() >= amount;
	}

	public boolean isAtLeast(ItemStack item, boolean checkDurability, boolean exactMatch) {
		return isSimilar(item, checkDurability, exactMatch, false) && item.getAmount() >= amount;
	}

	public boolean isSimilar(ItemStack item) {
		return isSimilar(item, false);
	}

	public boolean isSimilar(ItemStack item, boolean checkAmount) {
		return isSimilar(item, true, true, checkAmount);
	}

	public boolean isSimilar(ItemStack item, boolean checkDurability, boolean exactMatch, boolean checkAmount) {
		// null
		if (item == null) {
			return false;
		}
		// bukkit
		if (item == build || (checkAmount ? item.equals(build) : item.isSimilar(build))) {
			return true;
		}
		// amount : before type check, whatever that is, if the amount isn't the same - spares a few checks in Mat
		if (checkAmount && item.getAmount() != amount) {
			return false;
		}
		// type
		if (!type.equals(Mat.from(item), checkDurability)) {
			return false;
		}
		// exact match ?
		if (exactMatch) {
			// unbreakable
			if (unbreakable != Compat.INSTANCE.isUnbreakable(item)) {
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
			// effects
			if (!effects.isEmpty()) {
				// not a potion holder
				PotionMeta potion = item.hasItemMeta() && item.getItemMeta() instanceof PotionMeta ? (PotionMeta) item.getItemMeta() : null;
				if (potion == null) {
					return false;
				}
				// check effects
				if (effects.size() != potion.getCustomEffects().size()) {
					return false;
				}
				for (PotionEffect potionEffect : potion.getCustomEffects()) {
					boolean has = false;
					for (PotionEffect effect : effects) {
						if (potionEffect.equals(effect)) {
							has = true;
							break;
						}
					}
					if (!has) {
						return false;
					}
				}
				// all effects are good
			}
			// name
			if (name == null ? (item.hasItemMeta() && item.getItemMeta().hasDisplayName())
					: !(item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals(name))) {
				return false;
			}
			// lore
			if (lore == null ? (item.hasItemMeta() && item.getItemMeta().hasLore())
					: (!lore.isEmpty() && !(item.hasItemMeta() && item.getItemMeta().hasLore() && item.getItemMeta().getLore().equals(lore)))) {
				return false;
			}
		}
		// not an exact match
		else {
			// unbreakable
			if (unbreakable && !Compat.INSTANCE.isUnbreakable(item)) {
				return false;
			}
			// enchants
			for (Enchantment enchant : enchants.keySet()) {
				if (item.getEnchantmentLevel(enchant) < enchants.get(enchant)) {
					return false;
				}
			}
			// effects
			if (!effects.isEmpty()) {
				// not a potion holder
				PotionMeta potion = item.hasItemMeta() && item.getItemMeta() instanceof PotionMeta ? (PotionMeta) item.getItemMeta() : null;
				if (potion == null) {
					return false;
				}
				// check effects
				for (PotionEffect potionEffect : potion.getCustomEffects()) {
					boolean has = false;
					for (PotionEffect effect : effects) {
						if (potionEffect.getType().equals(effect.getType()) && potionEffect.getAmplifier() >= effect.getAmplifier() && potionEffect.getDuration() >= effect.getDuration()) {
							has = true;
							break;
						}
					}
					if (!has) {
						return false;
					}
				}
				// all effects are good
			}
			// name
			if (name != null && !(item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals(name))) {
				return false;
			}
			// lore
			if (lore != null && !lore.isEmpty() && !(item.hasItemMeta() && item.getItemMeta().hasLore() && item.getItemMeta().getLore().equals(lore))) {
				return false;
			}
		}
		// custom nbt
		if (customNbt != null && !customNbt.equals(Compat.INSTANCE.getNbt(item))) {
			return false;
		}
		// equals
		return true;
	}

	// misc
	@Override
	public int compareTo(ItemData other) {
		return Integer.compare(slot, other.slot);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + amount;
		long temp;
		temp = Double.doubleToLongBits(chance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((customNbt == null) ? 0 : customNbt.hashCode());
		result = prime * result + ((effects == null) ? 0 : effects.hashCode());
		result = prime * result + (enabled ? 1231 : 1237);
		result = prime * result + ((enchants == null) ? 0 : enchants.hashCode());
		result = prime * result + (hideFlags ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lore == null) ? 0 : lore.hashCode());
		result = prime * result + maxAmount;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + slot;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + (unbreakable ? 1231 : 1237);
		return result;
	}

	/**
	 * Assuming both objects are ItemData, there's a special check about the IDs here :
	 * <br>- If one of the IDs is null but the other one isn't, this method returns false
	 * <br>- If both IDs are equal, this method returns true
	 * <br>- If both IDs are null, this method returns true if all fields are equals
	 * @param obj the object to check
	 * @return true if this object equals to the argument
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemData other = (ItemData) obj;
		// id check
		if ((id == null && other.getId() != null) || (id != null && other.getId() == null) || (id != null && other.getId() != null && !id.equals(other.getId()))) {
			return false;
		}
		// regular check
		if (amount != other.amount)
			return false;
		if (Double.doubleToLongBits(chance) != Double.doubleToLongBits(other.chance))
			return false;
		if (customNbt == null) {
			if (other.customNbt != null)
				return false;
		} else if (!customNbt.equals(other.customNbt))
			return false;
		if (effects == null) {
			if (other.effects != null)
				return false;
		} else if (!effects.equals(other.effects))
			return false;
		if (enabled != other.enabled)
			return false;
		if (enchants == null) {
			if (other.enchants != null)
				return false;
		} else if (!enchants.equals(other.enchants))
			return false;
		if (hideFlags != other.hideFlags)
			return false;
		if (lore == null) {
			if (other.lore != null)
				return false;
		} else if (!lore.equals(other.lore))
			return false;
		if (maxAmount != other.maxAmount)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (slot != other.slot)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (unbreakable != other.unbreakable)
			return false;
		return true;
	}

	@Override
	public ItemData clone() {
		ItemData clone = new ItemData(id, slot, type, type.getDurability(), amount, name, lore == null ? null : Utils.asList(lore), Utils.asList(effects), Utils.asMapCopy(enchants));
		clone.enabled = enabled;
		clone.maxAmount = maxAmount;
		clone.chance = chance;
		clone.unbreakable = unbreakable;
		clone.hideFlags = hideFlags;
		clone.customNbt = customNbt;
		clone.build = build == null ? null : build.clone();
		return clone;
	}

	public ItemData cloneWithId(String id) {
		ItemData clone = clone();
		clone.setId(id);
		return clone;
	}

	public ItemData cloneWithSlot(int slot) {
		ItemData clone = clone();
		clone.setSlot(slot);
		return clone;
	}

	public ItemData cloneWithAmount(int amount) {
		ItemData clone = clone();
		clone.setAmount(amount);
		return clone;
	}

	public ItemData cloneWithIdAndAmount(String id, int amount) {
		ItemData clone = cloneWithId(id);
		clone.setAmount(amount);
		return clone;
	}

	public ItemData cloneWithIdAndSlot(String id, int slot) {
		ItemData clone = cloneWithId(id);
		clone.setSlot(slot);
		return clone;
	}

	@Override
	public String toString() {
		return "ItemData{id=" + id + ",enabled=" + enabled + ",slot=" + slot + ",chance=" + chance + ",type=" + type + ",amount=" + amount + ",maxAmount=" + maxAmount + ",effects=" + effects.toString()
		+ ",enchants=" + enchants.toString() + ",name=" + name + ",lore=" + (lore == null ? "null" : Utils.asString(lore)) + ",nbt=" + !(customNbt == null) + ",unbreakable=" + unbreakable + "}";
	}

}
