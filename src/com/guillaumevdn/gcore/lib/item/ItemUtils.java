package com.guillaumevdn.gcore.lib.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.TextGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Compat;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.compatibility.nbt.NBTItem;
import com.guillaumevdn.gcore.lib.gui.ItemFlag;
import com.guillaumevdn.gcore.lib.item.meta.MetaBook;
import com.guillaumevdn.gcore.lib.item.meta.MetaEnchantmentStorage;
import com.guillaumevdn.gcore.lib.item.meta.MetaFirework;
import com.guillaumevdn.gcore.lib.item.meta.MetaFireworkEffect;
import com.guillaumevdn.gcore.lib.item.meta.MetaLeatherArmor;
import com.guillaumevdn.gcore.lib.item.meta.MetaPotion;
import com.guillaumevdn.gcore.lib.item.meta.MetaSkull;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public final class ItemUtils {

	// ----- text
	public static String describeSingleLine(ItemStack item) {
		return describeSingleLine(item, item.getAmount());
	}

	public static String describeSingleLine(ItemStack item, int amount) {
		String separator = TextGeneric.textDescribeItemSingleLineSeparator.parseLine();
		Stream<String> desc = describe(item, amount, TextGeneric.textDescribeItemSingleLine, TextGeneric.textDescribeItemSingleLineNameIfHas, TextGeneric.textDescribeItemSingleLineLoreIfHas, TextGeneric.textDescribeItemSingleLineEnchantmentsIfHas, TextGeneric.textDescribeItemSingleLineEnchantmentsLine)
				.stream()
				.filter(line -> !line.trim().isEmpty());
		return StringUtils.toTextString(separator, desc);
	}

	public static List<String> describe(ItemStack item) {
		return describe(item, item.getAmount());
	}

	public static List<String> describe(ItemStack item, int amount) {
		return describe(item, amount, TextGeneric.textDescribeItem, TextGeneric.textDescribeItemNameIfHas, TextGeneric.textDescribeItemLoreIfHas, TextGeneric.textDescribeItemEnchantmentsIfHas, TextGeneric.textDescribeItemEnchantmentsLine);
	}

	public static List<String> describe(ItemStack item, int amount, Text text, Text itemNameIfHas, Text itemLoreIfHas, Text itemEnchantmentsIfHas, Text enchantmentLine) {
		Mat mat = Mat.fromItem(item).orElse(null);
		ItemMeta meta = item.getItemMeta();
		return text
				.replace("{amount}", () -> amount)
				.replace("{type}", () -> mat == null ? "?" + item.getType().name() : StringUtils.separateAtUnderscore(mat.getId()).toLowerCase())
				.replace("{durability}", () -> Compat.getDurability(item))
				.replace("{custom_model_data}", () -> Version.ATLEAST_1_14 ? (item.getItemMeta().hasCustomModelData() ? item.getItemMeta().getCustomModelData() : 0) : null)
				.replace("{name}", () -> meta == null || !meta.hasDisplayName() ? "" : itemNameIfHas.replace("{name}", () -> meta.getDisplayName()).parseLines())
				.replace("{lore}", () -> {
					List<String> lore = meta == null || !meta.hasLore() ? null : meta.getLore();
					if (lore == null || lore.isEmpty()) {
						return "";
					} else {
						List<String> parsed = new ArrayList<>();
						lore.forEach(line -> parsed.add(line));
						return itemLoreIfHas.replace("{lore}", () -> parsed).parseLines();
					}
				})
				.replace("{enchantments}", () -> {
					if (item.getEnchantments().isEmpty()) {
						return "";
					} else {
						List<String> parsed = new ArrayList<>();
						item.getEnchantments().forEach((enchant, level) -> parsed.addAll(enchantmentLine.replace("{enchant}", () -> enchant.getName()).replace("{level}", () -> level).parseLines()));
						return itemEnchantmentsIfHas.replace("{enchantments}", () -> parsed).parseLines();
					}
				})
				.parseLines();
	}

	// ----- head
	public static ItemStack getPlayerHead(String name, List<String> lore) {
		return getPlayerHead("§6", name, lore);
	}

	public static ItemStack getPlayerHead(String nameColor, String name, List<String> lore) {
		ItemStack stack = new ItemStack(Material.PLAYER_HEAD, 1, (short) SkullType.PLAYER.ordinal());
		SkullMeta meta = (SkullMeta) stack.getItemMeta();
		meta.setOwner(name);
		meta.setDisplayName(nameColor + name);
		if (lore != null) meta.setLore(lore);
		stack.setItemMeta(meta);
		return stack;
	}

	// ----- player
	/**
	 * @return the dropped item containing the remaining amount if player inventory became full as a result of this call, or null if there was no extra
	 */
	public static Item give(Player player, ItemStack item, boolean updateInv) {
		return give(player, item, updateInv, item.getMaxStackSize());
	}

	/**
	 * @return the dropped item containing the remaining amount if player inventory became full as a result of this call, or null if there was no extra
	 */
	public static Item give(Player player, ItemStack item, int amount, boolean updateInv) {
		return give(player, item, amount, updateInv, item.getMaxStackSize());
	}

	/**
	 * @return the dropped item containing the remaining amount if player inventory became full as a result of this call, or null if there was no extra
	 */
	public static Item give(Player player, ItemStack item, boolean updateInv, int maxStackSize) {
		return give(player, item, item.getAmount(), updateInv, maxStackSize);
	}

	/**
	 * @return the dropped item containing the remaining amount if player inventory became full as a result of this call, or null if there was no extra
	 */
	public static Item give(Player player, ItemStack item, int amount, boolean updateInv, int maxStackSize) {
		// eventually fix max stack size
		if (maxStackSize < 1 || maxStackSize > 64) {
			maxStackSize = 64;
		}
		// add in inventory
		int remaining = amount;
		PlayerInventory inventory = player.getInventory();
		for (int slot = 0; slot < 36; ++slot) {
			ItemStack slotItem = inventory.getItem(slot);
			int free = Mat.isVoid(slotItem) ? maxStackSize : (match(slotItem, item, ItemCheck.ExactSame) ? maxStackSize - slotItem.getAmount() : 0) ;
			if (free > 0) {
				int add = free > remaining ? remaining : free;
				remaining -= add;
				ItemStack newSlotItem = slotItem != null ? slotItem : item.clone();
				newSlotItem.setAmount(slotItem != null ? slotItem.getAmount() + add : add);
				inventory.setItem(slot, newSlotItem);
				if (remaining <= 0) {
					break;
				}
			}
		}
		// update inv if needed
		if (remaining != amount && updateInv) {
			player.updateInventory();
		}
		// drop extra
		Item extra = null;
		if (remaining > 0) {
			ItemStack extraStack = item.clone();
			extraStack.setAmount(remaining);
			Location dropLocation = player.getEyeLocation().add(player.getLocation().getDirection().clone().setY(0d));
			extra = dropLocation.getWorld().dropItem(dropLocation, extraStack);
		}
		// return
		return extra;
	}

	public static int take(Player player, ItemStack item, ItemCheck check, boolean updateInv) {
		return take(player, ItemReference.of(item), item.getAmount(), check, updateInv);
	}

	public static int take(Player player, ItemStack item, int amount, ItemCheck check, boolean updateInv) {
		return take(player, ItemReference.of(item), amount, check, updateInv);
	}

	public static int take(Player player, ItemReference item, int amount, ItemCheck check, boolean updateInv) {
		int remaining = take(player.getInventory(), item, amount, check);
		if (updateInv && remaining != amount) {
			player.updateInventory();
		}
		return remaining;
	}

	public static int take(Inventory inventory, ItemStack item, int amount, ItemCheck check) {
		return take(inventory, ItemReference.of(item), amount, check);
	}

	public static int take(Inventory inventory, ItemReference item, int amount, ItemCheck check) {
		int remaining = amount;
		for (int slot = 0; slot < inventory.getContents().length; ++slot) {
			ItemStack slotItem = inventory.getItem(slot);
			if (slotItem != null && match(slotItem, item, check)) {
				int canTake = slotItem.getAmount() >= remaining ? remaining : slotItem.getAmount();
				remaining -= canTake;
				if (canTake >= slotItem.getAmount()) {
					inventory.clear(slot);
				} else {
					slotItem.setAmount(slotItem.getAmount() - canTake);
					inventory.setItem(slot, slotItem);
				}
				if (remaining <= 0) {
					break;
				}
			}
		}
		return remaining;
	}

	public static List<Integer> findMatchingLocations(Inventory inventory, ItemStack item, ItemCheck check) {
		return findMatchingLocations(inventory, ItemReference.of(item), check);
	}

	public static List<Integer> findMatchingLocations(Inventory inventory, ItemReference item, ItemCheck check) {
		List<Integer> result = new ArrayList<>();
		for (int slot = 0; slot < inventory.getSize(); ++slot) {
			ItemStack slotItem = inventory.getItem(slot);
			if (slotItem != null && match(slotItem, item, check)) {
				result.add(slot);
			}
		}
		return result;
	}

	public static boolean has(Player player, ItemStack item, ItemCheck check) {
		return has(player, ItemReference.of(item), item.getAmount(), check);
	}

	public static boolean has(Player player, ItemStack item, int amount, ItemCheck check) {
		return has(player, ItemReference.of(item), amount, check);
	}

	public static boolean has(Player player, ItemReference item, int amount, ItemCheck check) {
		return countMax(player, item, check, amount) >= amount;
	}

	public static int count(Player player, ItemStack item, ItemCheck check) {
		return count(player, ItemReference.of(item), check);
	}

	public static int count(Player player, ItemReference item, ItemCheck check) {
		return countMax(player, item, check, Integer.MAX_VALUE);
	}

	public static int countMax(Player player, ItemStack item, ItemCheck check, int countMax) {
		return countMax(player, ItemReference.of(item), check, countMax);
	}

	public static int countMax(Player player, ItemReference item, ItemCheck check, int countMax) {
		int count = 0;
		PlayerInventory inventory = player.getInventory();
		for (ItemStack slotItem : inventory.getContents()) {
			if (slotItem != null && match(slotItem, item, check)) {
				count += slotItem.getAmount();
				if (count >= countMax) {
					return countMax;
				}
			}
		}
		return count;
	}

	// ----- similarity
	static final Map<Enchantment, Integer> EMPTY_ENCHANTS = new HashMap<>();
	static final List<ItemFlag> EMPTY_FLAGS = new ArrayList<>();
	static final List<String> EMPTY_LORE = new ArrayList<>();

	public static boolean match(ItemStack item, ItemStack reference, ItemCheck check) {
		return match(item, ItemReference.of(reference), check);
	}

	public static boolean match(ItemStack item, ItemReference reference, ItemCheck check) {
		boolean voidItem = Mat.isVoid(item);
		boolean voidReference = reference.isVoid();
		if (voidItem != voidReference) return false; // one is void but not the other
		if (voidItem) return true; // both void
		if (item == reference) return true;

		// type
		Mat type = Mat.fromItem(item).orElse(null);
		Mat refType = reference.getType();
		ConfigGCore.logspamItemNbt(null, () -> "Item match ; check " + check);
		ConfigGCore.logspamItemNbt(null, () -> "Item match ; type " + type + ", ref " + refType);

		if (type == null) {
			return false;
		}
		if (refType == null && check.isExact()) {
			return false;
		}
		if (refType != null && !type.equals(refType)) {
			return false;
		}

		// durability
		int itemDura = Compat.getDurability(item);
		int referenceDura = reference.getDurability();
		ConfigGCore.logspamItemNbt(null, () -> "Item match ; durability, item " + itemDura + ", ref " + referenceDura);
		if (check.mustHaveSameDurability() && itemDura != referenceDura) {
			return false;
		} else {
			// other checks are made below, after unbreakable
		}

		// meta
		ConfigGCore.logspamItemNbt(null, () -> "Item match ; meta");
		// - don't null-check metas ; sometimes the meta exists but is empty
		ItemMeta itemMeta = item.hasItemMeta() ? item.getItemMeta() : null;

		// custom model data
		if (Version.ATLEAST_1_14) {
			ConfigGCore.logspamItemNbt(null, () -> "Item match ; custom model data");
			int itemModelData = itemMeta != null && itemMeta.hasCustomModelData() ? itemMeta.getCustomModelData() : 0;
			int referenceModelData = reference.getCustomModelData();
			if (check.isExact() && itemModelData != referenceModelData) return false;
			else if (!check.isExact() && referenceModelData != 0 && itemModelData != referenceModelData) return false;
		}

		// unbreakable
		ConfigGCore.logspamItemNbt(null, () -> "Item match ; unbreakable");
		boolean itemUnbreakable = itemMeta != null && Compat.isUnbreakable(itemMeta);
		boolean referenceUnbreakable = reference.isUnbreakable();
		if (check.isExact() && itemUnbreakable != referenceUnbreakable) return false;
		else if (!check.isExact() && referenceUnbreakable && (itemMeta == null || !itemUnbreakable)) return false;
		// if it's not unbreakable, and can be damaged, and we want it to be less damaged, check if the guy is trying to bamboozle us
		if (!itemUnbreakable && type.getData().isDamageable() && check.musntBeMoreDamaged() && itemDura > referenceDura) {
			return false;
		}

		// enchantments
		ConfigGCore.logspamItemNbt(null, () -> "Item match ; enchantments");
		boolean itemHasEnchants = itemMeta != null && itemMeta.hasEnchants();
		boolean referenceHasEnchants = reference.hasEnchants();
		if (check.isExact()) {
			if (itemHasEnchants != referenceHasEnchants) return false;
			Map<Enchantment, Integer> itemEnchants = itemMeta == null ? EMPTY_ENCHANTS : itemMeta.getEnchants();
			Map<Enchantment, Integer> referenceEnchants = reference.getEnchants();
			if (!CollectionUtils.contentEquals(itemEnchants, referenceEnchants)) return false;
		} else {
			if (referenceHasEnchants) {
				if (!itemHasEnchants) return false;
				for (Enchantment enchantment : reference.getEnchants().keySet()) {
					if (itemMeta.getEnchants().get(enchantment) != reference.getEnchants().get(enchantment)) {
						return false;
					}
				}
			}
		}

		// flags
		List<ItemFlag> itemFlags = itemMeta == null ? EMPTY_FLAGS : Compat.getItemFlags(itemMeta);
		List<ItemFlag> referenceFlags = reference.getFlags();
		ConfigGCore.logspamItemNbt(null, () -> "Item match ; flags ; item " + itemFlags + ", ref " + referenceFlags);
		if (check.isExact()) {
			if (!CollectionUtils.contentEquals(itemFlags, referenceFlags, false)) return false;
		}
		else if (!check.isExact()) {
			if (!itemFlags.containsAll(referenceFlags)) return false;
		}

		// name
		String itemName = itemMeta == null ? null : (!itemMeta.hasDisplayName() ? null : itemMeta.getDisplayName());
		String referenceName = reference.getDisplayName();
		ConfigGCore.logspamItemNbt(null, () -> "Item match ; name ; item '" + itemName + "', ref '" + referenceName + "'");
		if (check.isExact()) {
			if (!Objects.equals(itemName, referenceName)) {
				return false;
			}
		} else {
			if (check.nameContains()) {
				if (referenceName != null && (itemName == null || !itemName.contains(referenceName))) {
					return false;
				}
			} else {
				if (referenceName != null && !referenceName.equals(itemName)) {
					ConfigGCore.logspamItemNbt(null, () -> referenceName.replace('§', '&'));
					ConfigGCore.logspamItemNbt(null, () -> itemName.replace('§', '&'));
					return false;
				}
			}
		}

		// lore
		ConfigGCore.logspamItemNbt(null, () -> "Item match ; lore");
		List<String> itemLore = itemMeta == null ? EMPTY_LORE : (!itemMeta.hasLore() ? EMPTY_LORE : itemMeta.getLore());
		List<String> referenceLore = reference.getLore();
		if (check.isExact()) {
			if (!CollectionUtils.contentEquals(itemLore, referenceLore)) {
				return false;
			}
		} else {
			if (check.loreContains()) {
				if (!referenceLore.isEmpty() && !CollectionUtils.containsOne(itemLore, referenceLore)) {
					return false;
				}
			} else {
				if (!referenceLore.isEmpty() && !itemLore.containsAll(referenceLore)) {
					return false;
				}
			}
		}

		// specific meta
		ConfigGCore.logspamItemNbt(null, () -> "Item match ; book meta");
		if (!MetaBook.match(itemMeta, reference, check)) return false;

		ConfigGCore.logspamItemNbt(null, () -> "Item match ; enchantment storage meta");
		if (!MetaEnchantmentStorage.match(itemMeta, reference, check)) return false;

		ConfigGCore.logspamItemNbt(null, () -> "Item match ; firework effect meta");
		if (!MetaFireworkEffect.match(itemMeta, reference, check)) return false;

		ConfigGCore.logspamItemNbt(null, () -> "Item match ; firework meta");
		if (!MetaFirework.match(itemMeta, reference, check)) return false;

		ConfigGCore.logspamItemNbt(null, () -> "Item match ; leather armor meta");
		if (!MetaLeatherArmor.match(itemMeta, reference, check)) return false;

		ConfigGCore.logspamItemNbt(null, () -> "Item match ; potion meta");
		if (!MetaPotion.match(itemMeta, reference, check)) return false;

		ConfigGCore.logspamItemNbt(null, () -> "Item match ; skull meta");
		if (!MetaSkull.match(itemMeta, reference, check)) return false;

		ConfigGCore.logspamItemNbt(null, () -> "Item match ; banner meta");
		if (Version.ATLEAST_1_8 && !com.guillaumevdn.gcore.lib.item.meta.MetaBanner.match(itemMeta, reference, check)) return false;

		ConfigGCore.logspamItemNbt(null, () -> "Item match ; spawn egg meta");
		if (Version.ATLEAST_1_11 && !Version.ATLEAST_1_13 && !com.guillaumevdn.gcore.lib.item.meta.MetaSpawnEgg.match(itemMeta, reference, check)) return false;

		ConfigGCore.logspamItemNbt(null, () -> "Item match ; knowledge book meta");
		if (Version.ATLEAST_1_12 && !com.guillaumevdn.gcore.lib.item.meta.MetaKnowledgeBook.match(itemMeta, reference, check)) return false;

		ConfigGCore.logspamItemNbt(null, () -> "Item match ; tropical fish bucket meta");
		if (Version.ATLEAST_1_13 && !com.guillaumevdn.gcore.lib.item.meta.MetaTropicalFishBucket.match(itemMeta, reference, check)) return false;

		ConfigGCore.logspamItemNbt(null, () -> "Item match ; crossbow meta");
		if (Version.ATLEAST_1_14 && !com.guillaumevdn.gcore.lib.item.meta.MetaCrossbow.match(itemMeta, reference, check)) return false;

		ConfigGCore.logspamItemNbt(null, () -> "Item match ; suspicious stew meta");
		if (Version.ATLEAST_1_15 && !com.guillaumevdn.gcore.lib.item.meta.MetaSuspiciousStew.match(itemMeta, reference, check)) return false;

		// nbt
		try {
			ConfigGCore.logspamItemNbt(null, () -> "Item match ; nbt");
			NBTItem nbt = new NBTItem(item);
			NBTItem nbtRef = reference.getNBTItem();
			if (!nbt.match(nbtRef, check.isExact())) return false;
		} catch (Throwable exception) {
			exception.printStackTrace();
			return false;
		}
		ConfigGCore.logspamItemNbt(null, () -> "Item match ; success");

		// seems good
		return true;
	}

	public static boolean haveSameEnchants(ItemStack a, ItemStack b) {
		ItemMeta ma = a.getItemMeta();
		ItemMeta mb = b.getItemMeta();
		Map<Enchantment, Integer> ea = ma == null ? EMPTY_ENCHANTS : ma.getEnchants();
		Map<Enchantment, Integer> eb = mb == null ? EMPTY_ENCHANTS : mb.getEnchants();
		return CollectionUtils.contentEquals(ea, eb);
	}

	// ----- creation
	public static ItemStack createItem(Mat type, String name, List<String> lore) {
		return createItem(type, 1, name, lore);
	}

	public static ItemStack createItem(Mat type, int amount, String name, List<String> lore) {
		ItemStack item = type == null ? null : type.newStack();
		if (Mat.isVoid(item)) {
			return null;
		}
		item.setAmount(amount);
		if (name != null || lore != null) {
			ItemMeta meta = item.getItemMeta();
			if (name != null) meta.setDisplayName(StringUtils.format(name));
			if (lore != null) meta.setLore(StringUtils.formatCopy(lore));
			item.setItemMeta(meta);
		}
		return item;
	}

	// ----- modification
	/** @return the same item */
	public static ItemStack addAllFlags(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		Compat.addItemFlags(meta, CollectionUtils.asList(ItemFlag.values()));
		item.setItemMeta(meta);
		return item;
	}

	/** @return the same item */
	public static ItemStack addToLore(ItemStack item, List<String> toAdd) {
		return addToLore(item, toAdd, false);
	}

	public static ItemStack addToLore(ItemStack item, List<String> toAdd, boolean clone) {
		if (clone) {
			item = item.clone();
		}
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore() != null ? meta.getLore() : new ArrayList<>();
		lore.addAll(toAdd);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	/** @return either a copy OR the same item if already is glowing */
	public static ItemStack maybeAddGlow(ItemStack item) {
		return maybeAddGlow(item, true);
	}

	/** @return either a copy OR the same item */
	public static ItemStack maybeAddGlow(ItemStack item, boolean addGlow) {
		if (addGlow && item.getEnchantments().isEmpty()) {
			item = item.clone();
			item.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, 1);
			ItemMeta meta = item.getItemMeta();
			Compat.addItemFlags(meta, ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(meta);
		}
		return item;
	}

}
