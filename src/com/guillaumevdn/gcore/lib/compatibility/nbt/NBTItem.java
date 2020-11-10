package com.guillaumevdn.gcore.lib.compatibility.nbt;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.reflection.Reflection;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;

/**
 * @author GuillaumeVDN
 */
public class NBTItem extends NBTCompound {

	private ItemStack initialItem;

	public NBTItem(ItemStack item) throws Throwable {
		super(null, "root", 0, ReflectionObject.of(Reflection.invokeCraftbukkitMethod("inventory.CraftItemStack", "asNMSCopy", null, item).invokeMethod("getTag").orElse(Reflection.newNmsInstance("NBTTagCompound"))).invokeMethod("clone"));
		this.initialItem = item;
	}

	public NBTItem(ItemStack item, ReflectionObject tag) throws Throwable {
		super(null, "root", 0, tag);
		this.initialItem = item;
	}

	// get
	public ItemStack getInitialItem() {
		return initialItem;
	}

	public ItemStack getModifiedItem() throws Throwable {
		ConfigGCore.logspamItemNbt(null, () -> "Applying tag to item");
		ConfigGCore.logspamItemNbt(null, () -> "Initial keys " + new NBTItem(initialItem).getKeys());
		ConfigGCore.logspamItemNbt(null, () -> "Modified keys " + getKeys());
		// save dura and data because apparently it bugs in legacy versions -> if this bug is confirmed, uncomment this part
		/*int durability = Compat.getDurability(initialItem);
		int data = Compat.getLegacyData(initialItem);*/
		// clone item and set tag
		ItemStack item = initialItem.clone();
		ReflectionObject nmsItem = Reflection.invokeCraftbukkitMethod("inventory.CraftItemStack", "asNMSCopy", null, item);
		nmsItem.invokeMethod("setTag", (Object) getTag().get());
		ItemStack modified = Reflection.invokeCraftbukkitMethod("inventory.CraftItemStack", "asBukkitCopy", null, (Object) nmsItem.get()).get();
		// reapply data and dura
		/*if (durability != 0) modified = Compat.setDurability(modified, durability);
		if (data != 0) modified = Compat.setLegacyData(modified, data);*/
		// done
		ConfigGCore.logspamItemNbt(null, () -> "Final new keys " + new NBTItem(modified).getKeys());
		return modified;
	}

	// static
	public static final List<String> IGNORE_TAGS = CollectionUtils.asUnmodifiableLowercaseList(
			// item
			"unbreakable",
			"durability",
			"damage",
			"data",
			"HideFlags",
			// display
			"display",
			// enchantments
			"enchantments",
			// potions
			"CustomPotionEffects",
			// written book
			"author",
			"title",
			"pages",
			// custom model data
			"CustomModelData",
			// skull profile
			"SkullProfile",
			"SkullOwner"
			);

}
