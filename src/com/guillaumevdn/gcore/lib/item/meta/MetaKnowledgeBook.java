package com.guillaumevdn.gcore.lib.item.meta;

import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.KnowledgeBookMeta;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementNamespacedKeyList;
import com.guillaumevdn.gcore.lib.element.type.container.ElementItem;
import com.guillaumevdn.gcore.lib.item.ItemCheck;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public final class MetaKnowledgeBook {

	public static boolean match(ItemMeta itemMeta, ItemMeta referenceMeta, ItemCheck check) {
		KnowledgeBookMeta meta = ObjectUtils.castOrNull(itemMeta, KnowledgeBookMeta.class); // might be null if exact match is false
		KnowledgeBookMeta ref = ObjectUtils.castOrNull(referenceMeta, KnowledgeBookMeta.class);
		if (ref == null) return true;
		// pages
		if (check.isExact() && (meta.hasRecipes() != ref.hasRecipes() || !CollectionUtils.contentEquals(meta.getRecipes(), ref.getRecipes()))) return false;
		else if (!check.isExact() && ref.hasRecipes() && (meta == null || !meta.hasRecipes() || !CollectionUtils.contentEquals(meta.getRecipes(), ref.getRecipes()))) return false;
		// seems good
		return true;
	}

	public static void write(ItemMeta itemMeta, DataIO writer) throws Throwable {
		KnowledgeBookMeta meta = ObjectUtils.castOrNull(itemMeta, KnowledgeBookMeta.class);
		if (meta != null) {
			if (meta.hasRecipes()) {
				writer.writeSerializedList("recipes", meta.getRecipes());
			}
		}
	}

	public static void read(ItemMeta itemMeta, DataIO reader) throws Throwable {
		KnowledgeBookMeta meta = ObjectUtils.castOrNull(itemMeta, KnowledgeBookMeta.class);
		if (meta != null) {
			List<NamespacedKey> recipes = reader.readSerializedList("recipes", NamespacedKey.class);
			if (recipes != null) {
				meta.setRecipes(recipes);
			}
		}
	}

	public static void fillElements(ItemMeta sampleMeta, ElementItem item) {
		if (ObjectUtils.instanceOf(sampleMeta, KnowledgeBookMeta.class)) {
			item.add(new ElementNamespacedKeyList(item, "recipes", Need.optional(), TextEditorGeneric.descriptionItemKnowledgeBookRecipes));
		}
	}

	public static void clearElements(ElementItem item) {
		item.remove("recipes");
	}

	public static void writeElements(ElementItem item, DataIO writer, Replacer replacer) {
		item.parseElementAsList("recipes", NamespacedKey.class, replacer).ifPresentDo(recipes -> writer.writeSerializedList("recipes", recipes));
	}

	public static void importElements(ElementItem item, ItemMeta itemMeta) {
		KnowledgeBookMeta meta = ObjectUtils.castOrNull(itemMeta, KnowledgeBookMeta.class);
		if (meta != null) {
			item.getElementAs("recipes", ElementNamespacedKeyList.class).setValue(Serializer.NAMESPACED_KEY.serialize(meta.getRecipes()));
		}
	}

}
