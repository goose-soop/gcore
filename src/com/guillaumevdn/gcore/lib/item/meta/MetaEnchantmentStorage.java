package com.guillaumevdn.gcore.lib.item.meta;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.type.container.ElementItem;
import com.guillaumevdn.gcore.lib.element.type.map.ElementEnchantmentLevelMap;
import com.guillaumevdn.gcore.lib.item.ItemCheck;
import com.guillaumevdn.gcore.lib.item.ItemReference;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public final class MetaEnchantmentStorage {

	public static boolean match(ItemMeta itemMeta, ItemReference reference, ItemCheck check) {
		if (!reference.hasMeta(EnchantmentStorageMeta.class)) return true;
		EnchantmentStorageMeta meta = ObjectUtils.castOrNull(itemMeta, EnchantmentStorageMeta.class);  // might be null if exact match is false

		// enchantments
		if (check.isExact()) {
			if ((meta != null && meta.hasStoredEnchants()) != reference.hasStoredEnchants()) return false;
			if (reference.hasStoredEnchants() && !CollectionUtils.contentEquals(meta.getStoredEnchants(), reference.getStoredEnchants())) return false;
		} else {
			if (reference.hasStoredEnchants() && (meta == null || !meta.hasStoredEnchants())) return false;
			for (Enchantment enchantment : reference.getStoredEnchants().keySet()) {
				if (meta.getStoredEnchants().get(enchantment) != reference.getStoredEnchants().get(enchantment)) {
					return false;
				}
			}
		}
		// seems good
		return true;
	}

	public static void write(ItemMeta itemMeta, DataIO writer) throws Throwable {
		EnchantmentStorageMeta meta = ObjectUtils.castOrNull(itemMeta, EnchantmentStorageMeta.class);
		if (meta != null) {
			if (meta.hasStoredEnchants()) {
				writer.writeObject("storedEnchantments", d -> {
					meta.getStoredEnchants().forEach((enchantment, level) -> d.write(enchantment.getName(), level));
				});
			}
		}
	}

	public static void read(ItemMeta itemMeta, DataIO reader) throws Throwable {
		EnchantmentStorageMeta meta = ObjectUtils.castOrNull(itemMeta, EnchantmentStorageMeta.class);
		if (meta != null) {
			DataIO storedEnchantments = reader.readObject("storedEnchantments");
			if (storedEnchantments != null) {
				for (String raw : storedEnchantments.getKeys()) {
					Enchantment enchantment = ObjectUtils.enchantmentOrNull(raw);
					Integer level = storedEnchantments.readInteger(raw);
					if (enchantment != null && level != null) {
						meta.addStoredEnchant(enchantment, level, true);
					}
				}
			}
		}
	}

	public static void fillElements(ItemMeta sampleMeta, ElementItem item) {
		if (ObjectUtils.instanceOf(sampleMeta, EnchantmentStorageMeta.class)) {
			item.addEnchantmentLevelMap("stored_enchantments", Need.optional(), TextEditorGeneric.descriptionItemStoredEnchantments);
		}
	}

	public static void clearElements(ElementItem item) {
		item.remove("stored_enchantments");
	}

	public static void writeElements(ElementItem item, DataIO writer, Replacer replacer) {
		item.parseElementAsMap("stored_enchantments", Enchantment.class, Integer.class, replacer).ifPresentDo(enchantments -> {
			writer.writeObject("storedEnchantments", w -> enchantments.forEach((enchantment, level) -> w.write(enchantment.getName(), level)));
		});
	}

	public static void importElements(ElementItem item, ItemMeta itemMeta) {
		EnchantmentStorageMeta meta = ObjectUtils.castOrNull(itemMeta, EnchantmentStorageMeta.class);
		if (meta != null) {
			ElementEnchantmentLevelMap map = item.getElementAs("stored_enchantments");
			map.clear();
			for (Enchantment enchantment : meta.getStoredEnchants().keySet()) {
				map.createAndAddElement(enchantment).setValue(CollectionUtils.asList("" + meta.getStoredEnchantLevel(enchantment)));
			}
		}
	}

}
