package com.guillaumevdn.gcore.lib.compatibility.nbt;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.reflection.Reflection;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;

/**
 * @author GuillaumeVDN
 */
public class NBTItem extends NBTCompound {

	private ItemStack initialItem;

	public NBTItem(ItemStack item) throws Throwable {
		this(item, true);
	}

	public NBTItem(ItemStack item, boolean clone) throws Throwable {
		super(null, "root", 0, getTag(item, clone));
		this.initialItem = item;
	}

	private static ReflectionObject getTag(ItemStack item, boolean clone) throws Throwable {
		ReflectionObject nmsCopy = Reflection.invokeCraftbukkitMethod("inventory.CraftItemStack", "asNMSCopy", null, item);
		if (Version.ATLEAST_1_20_5) {
			final Object registryAccess = Reflection.invokeCraftbukkitMethod("CraftRegistry", "getMinecraftRegistry", null).get();
			final ReflectionObject tag = nmsCopy.invokeMethod("a", registryAccess);
			if (tag.justGet() != null) {
				return tag;
			} else {
				return Reflection.newNmsInstance("nbt.NBTTagCompound");
			}
		} else if (Version.ATLEAST_1_18) {
			return nmsCopy.invokeMethod(clone ? "getTagClone" : (Version.ATLEAST_1_20 ? "v" : (Version.ATLEAST_1_19 ? "u" : "t")))
					.orElse(Reflection.newNmsInstance("nbt.NBTTagCompound"));
		} else {
			ReflectionObject tag = nmsCopy.invokeMethod("getTag");
			if (tag.justGet() != null) {
				return clone ? tag.invokeMethod("clone") : tag;
			} else {
				return Reflection.newNmsInstance((Version.REMAPPED ? "nbt." : "") + "NBTTagCompound");
			}
		}
	}

	public NBTItem(ItemStack item, ReflectionObject tag) throws Throwable {
		super(null, "root", 0, tag);
		this.initialItem = item;
	}

	// ----- get
	public ItemStack getInitialItem() {
		return initialItem;
	}

	public ItemStack getModifiedItem() throws Throwable {
		/*
		 * ConfigGCore.logspamItemNbt(null, () -> "Applying tag to item"); ConfigGCore.logspamItemNbt(null, () ->
		 * "Initial keys " + new NBTItem(initialItem).getKeys()); ConfigGCore.logspamItemNbt(null, () -> "Modified keys " +
		 * getKeys());
		 */

		// save dura and data because apparently it bugs in legacy versions -> if this bug is confirmed, uncomment this part
		/*
		 * int durability = Compat.getDurability(initialItem); int data = Compat.getLegacyData(initialItem);
		 */
		// clone item and set tag
		ItemStack item = initialItem.clone();
		ReflectionObject nmsItem = Reflection.invokeCraftbukkitMethod("inventory.CraftItemStack", "asNMSCopy", null, item);
		nmsItem.invokeMethod(Version.ATLEAST_1_18 ? "c" : "setTag", (Object) getTag().get());
		ItemStack modified = Reflection.invokeCraftbukkitMethod("inventory.CraftItemStack", "asBukkitCopy", null, (Object) nmsItem.get()).get();
		// reapply data and dura
		/*
		 * if (durability != 0) modified = Compat.setDurability(modified, durability); if (data != 0) modified =
		 * Compat.setLegacyData(modified, data);
		 */
		// done
		// ConfigGCore.logspamItemNbt(null, () -> "Final new keys " + new NBTItem(modified).getKeys());
		return modified;
	}

	// ----- static
	public static final List<String> IGNORE_TAGS = CollectionUtils.asUnmodifiableLowercaseList(
			// item
			"unbreakable", "durability", "damage", "data", "HideFlags",
			// display
			"display",
			// enchantments
			"enchantments", "StoredEnchantments",
			// potions
			"CustomPotionEffects", "Potion",
			// written book
			"author", "title", "pages",
			// custom model data
			"CustomModelData",
			// skull profile
			"SkullProfile", "SkullOwner",
			// firework rocket
			"Fireworks");

}
