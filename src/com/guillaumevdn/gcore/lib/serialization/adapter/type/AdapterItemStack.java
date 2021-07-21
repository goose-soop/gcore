package com.guillaumevdn.gcore.lib.serialization.adapter.type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.guillaumevdn.gcore.lib.compatibility.Compat;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.compatibility.nbt.NBTCompound;
import com.guillaumevdn.gcore.lib.compatibility.nbt.NBTItem;
import com.guillaumevdn.gcore.lib.gui.ItemFlag;
import com.guillaumevdn.gcore.lib.item.meta.MetaBook;
import com.guillaumevdn.gcore.lib.item.meta.MetaEnchantmentStorage;
import com.guillaumevdn.gcore.lib.item.meta.MetaFirework;
import com.guillaumevdn.gcore.lib.item.meta.MetaFireworkEffect;
import com.guillaumevdn.gcore.lib.item.meta.MetaLeatherArmor;
import com.guillaumevdn.gcore.lib.item.meta.MetaPotion;
import com.guillaumevdn.gcore.lib.item.meta.MetaSkull;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.reflection.Reflection;
import com.guillaumevdn.gcore.lib.serialization.adapter.DataAdapter;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public final class AdapterItemStack extends DataAdapter<ItemStack> {

	public static final AdapterItemStack INSTANCE = new AdapterItemStack();

	private AdapterItemStack() {
		super(ItemStack.class, 1);
	}

	@Override
	public void write(ItemStack item, DataIO writer) throws Throwable {
		// type
		Mat type = Mat.fromItem(item).orElse(null);
		writer.write("type", type != null ? type.getId() : item.getType().name());
		// durability
		int durability = Compat.getDurability(item);
		if (durability != 0) {
			writer.write("durability", durability);
		}
		// amount
		if (item.getAmount() != 1) {
			writer.write("amount", item.getAmount());
		}
		// meta
		if (item.hasItemMeta()) {
			ItemMeta meta = item.getItemMeta();
			// unbreakable
			if (Compat.isUnbreakable(meta)) {
				writer.write("unbreakable", true);
			}
			// custom model data
			if (Version.ATLEAST_1_14 && meta.hasCustomModelData()) {
				writer.write("customModelData", meta.getCustomModelData());
			}
			// enchantments
			if (!meta.getEnchants().isEmpty()) {
				writer.writeObject("enchantments", w -> item.getEnchantments().forEach((enchantment, level) -> w.write(enchantment.getName(), level)));
			}
			// flags
			List<ItemFlag> flags = Compat.getItemFlags(meta);
			if (!flags.isEmpty()) {
				writer.writeSerializedList("flags", flags);
			}
			// name
			if (meta.hasDisplayName()) {
				writer.write("name", meta.getDisplayName());
			}
			// lore
			if (meta.hasLore()) {
				writer.writeSerializedList("lore", meta.getLore());
			}
			// specific meta
			MetaBook.write(meta, writer);
			MetaEnchantmentStorage.write(meta, writer);
			MetaFireworkEffect.write(meta, writer);
			MetaFirework.write(meta, writer);
			MetaLeatherArmor.write(meta, writer);
			MetaPotion.write(meta, writer);
			MetaSkull.write(meta, writer);
			if (Version.ATLEAST_1_8) com.guillaumevdn.gcore.lib.item.meta.MetaBanner.write(meta, writer);
			if (Version.ATLEAST_1_11 && !Version.ATLEAST_1_13) com.guillaumevdn.gcore.lib.item.meta.MetaSpawnEgg.write(meta, writer);
			if (Version.ATLEAST_1_12) com.guillaumevdn.gcore.lib.item.meta.MetaKnowledgeBook.write(meta, writer);
			if (Version.ATLEAST_1_13) com.guillaumevdn.gcore.lib.item.meta.MetaTropicalFishBucket.write(meta, writer);
			if (Version.ATLEAST_1_14) com.guillaumevdn.gcore.lib.item.meta.MetaCrossbow.write(meta, writer);
			if (Version.ATLEAST_1_15) com.guillaumevdn.gcore.lib.item.meta.MetaSuspiciousStew.write(meta, writer);
		}
		// nbt
		writer.writeObjectOrThrow("nbt", d -> AdapterNBTCompound.INSTANCE.write(new NBTItem(item), d));
	}

	@Override
	public ItemStack read(int version, DataIO reader) throws Throwable {
		if (version == 1) {
			// ---------- WARNING : the order in which the operations are made is very important here ----------
			// type : numeric
			String rawType = reader.readString("type");
			Integer numericType = NumberUtils.integerOrNull(rawType);
			Mat type = null;
			if (numericType != null) {
				if (Version.ATLEAST_1_13) {
					throw new IllegalStateException("numeric material types (" + numericType + ") are not allowed in 1.13+ versions");
				}
				try {
					Material material = Reflection.invokeMethod("org.bukkit.material.Material", "getMaterial", null, numericType).get();
					type = Mat.firstFromIdOrDataName(material.name()).orElse(null);
				} catch (Throwable ignored) {}
				if (type == null) {
					throw new IllegalStateException("no material found with numeric type " + numericType);
				}
			}
			// type : string
			else {
				type = Mat.firstFromIdOrDataName(reader.readString("type")).get(); // this throws an error
			}
			// build item from type
			if (!type.canHaveItem()) {
				throw new IllegalStateException("material type " + reader.readString("type") + " can't be represented as an item stack");
			}
			ItemStack item = type.newStack();
			// durability
			// only set that if it's not already specified in the type, and it's not an <1.13 item with legacy data
			// because in legacy versions, data and durability must be the same for the variant to appear correctly, otherwise it displays the weird blank item
			Integer durability = reader.readInteger("durability");
			if (durability == null) durability = 0;
			if (durability != Compat.getDurability(item) && (Version.ATLEAST_1_13 || !type.getData().hasLegacyData())) {
				item = Compat.setDurability(item, durability);
			}
			// amount
			Integer amount = reader.readInteger("amount");
			if (amount != null && amount > 0) {
				item.setAmount(amount);
			}
			// meta
			ItemMeta meta = null;
			// unbreakable
			Boolean unbreakable = reader.readBoolean("unbreakable");
			if (unbreakable != null && unbreakable) {
				if (meta == null) meta = item.getItemMeta();
				Compat.setUnbreakable(meta, true);
			}
			// custom model data
			if (Version.ATLEAST_1_14) {
				Integer customModelData = reader.readInteger("customModelData");
				if (customModelData != null) {
					if (meta == null) meta = item.getItemMeta();
					meta.setCustomModelData(customModelData);
				}
			}
			// enchantments
			Map<Enchantment, Integer> enchantments = reader.readNullableSimpleMap("enchantments", Enchantment.class, new HashMap<>(), (key, __, mapReader) -> mapReader.readInteger(key));
			if (enchantments != null) {
				if (meta == null) meta = item.getItemMeta();
				for (Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
					if (enchantment.getValue() != 0) {  // allow negative enchants if needed, but not zero
						meta.addEnchant(enchantment.getKey(), enchantment.getValue(), true);
					}
				}
			}
			// flags
			List<ItemFlag> flags = reader.readSerializedList("flags", ItemFlag.class);
			if (flags != null) {
				if (meta == null) meta = item.getItemMeta();
				Compat.addItemFlags(meta, flags);
			}
			// name
			String name = reader.readString("name");
			if (name != null) {
				if (meta == null) meta = item.getItemMeta();
				meta.setDisplayName(StringUtils.format(name));
			}
			// lore
			List<String> lore = reader.readDirectList("lore");
			if (lore != null) {
				if (meta == null) meta = item.getItemMeta();
				meta.setLore(StringUtils.formatCopy(lore));
			}
			// specific meta
			if (meta == null) meta = item.getItemMeta();
			MetaBook.read(meta, reader);
			MetaEnchantmentStorage.read(meta, reader);
			MetaFireworkEffect.read(meta, reader);
			MetaFirework.read(meta, reader);
			MetaLeatherArmor.read(meta, reader);
			MetaPotion.read(meta, reader);
			MetaSkull.read(meta, reader);
			if (Version.ATLEAST_1_8) com.guillaumevdn.gcore.lib.item.meta.MetaBanner.read(meta, reader);
			if (Version.ATLEAST_1_11 && !Version.ATLEAST_1_13) com.guillaumevdn.gcore.lib.item.meta.MetaSpawnEgg.read(meta, reader);
			if (Version.ATLEAST_1_12) com.guillaumevdn.gcore.lib.item.meta.MetaKnowledgeBook.read(meta, reader);
			if (Version.ATLEAST_1_13) com.guillaumevdn.gcore.lib.item.meta.MetaTropicalFishBucket.read(meta, reader);
			if (Version.ATLEAST_1_14) com.guillaumevdn.gcore.lib.item.meta.MetaCrossbow.read(meta, reader);
			if (Version.ATLEAST_1_15) com.guillaumevdn.gcore.lib.item.meta.MetaSuspiciousStew.read(meta, reader);
			// apply meta to item
			if (meta != null) {
				item.setItemMeta(meta);
			}
			// set nbt
			DataIO nbt = reader.readObject("nbt");
			if (nbt != null) {
				NBTCompound compound = AdapterNBTCompound.INSTANCE.readCurrent(nbt);
				if (compound != null) {
					NBTItem nbtItem = new NBTItem(item);
					nbtItem.addNonClonedElementsFrom(compound);
					item = nbtItem.getModifiedItem();
				}
			}
			// done
			return item;
		}
		throw new IllegalArgumentException("unknown adapter version " + version);
	}

}
