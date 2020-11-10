package com.guillaumevdn.gcore.lib.item.meta;

import java.util.Objects;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.element.editor.SlotPlacement;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementEntityType;
import com.guillaumevdn.gcore.lib.element.type.container.ElementItem;
import com.guillaumevdn.gcore.lib.item.ItemCheck;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public final class MetaSpawnEgg {

	public static boolean match(ItemMeta itemMeta, ItemMeta referenceMeta, ItemCheck check) {
		SpawnEggMeta meta = ObjectUtils.castOrNull(itemMeta, SpawnEggMeta.class); // might be null if exact match is false
		SpawnEggMeta ref = ObjectUtils.castOrNull(referenceMeta, SpawnEggMeta.class);
		if (ref == null) return true;
		// author
		if (check.isExact() && !Objects.deepEquals(meta.getSpawnedType(), ref.getSpawnedType())) return false;
		else if (!check.isExact() && ref.getSpawnedType() != null && (meta == null || !ref.getSpawnedType().equals(meta.getSpawnedType()))) return false;
		// seems good
		return true;
	}

	public static void write(ItemMeta itemMeta, DataIO writer) {
		SpawnEggMeta meta = ObjectUtils.castOrNull(itemMeta, SpawnEggMeta.class);
		if (meta != null) {
			writer.write("spawnedType", meta.getSpawnedType());
		}
	}

	public static void read(ItemMeta itemMeta, DataIO reader) {
		SpawnEggMeta meta = ObjectUtils.castOrNull(itemMeta, SpawnEggMeta.class);
		if (meta != null) {
			EntityType spawnedType = reader.readEnum("spawnedType", EntityType.class);
			if (spawnedType != null) {
				meta.setSpawnedType(spawnedType);
			}
		}
	}

	public static void fillElements(ItemMeta sampleMeta, ElementItem item) {
		if (ObjectUtils.instanceOf(sampleMeta, SpawnEggMeta.class)) {
			item.addEntityType("spawned_type", Need.optional(), SlotPlacement.START_ROW, TextEditorGeneric.descriptionItemSpawnEggType);
		}
	}

	public static void clearElements(ElementItem item) {
		item.remove("spawned_type");
	}

	public static void writeElements(ElementItem item, DataIO writer, Replacer replacer) {
		item.parseElementAs("spawned_type", replacer).ifPresentDo(v -> writer.write("spawnedType", v));
	}

	public static void importElements(ElementItem item, ItemMeta itemMeta) {
		SpawnEggMeta meta = ObjectUtils.castOrNull(itemMeta, SpawnEggMeta.class);
		if (meta != null) {
			item.getElementAs("spawned_type", ElementEntityType.class).setValue(meta.getSpawnedType() != null ? CollectionUtils.asList(meta.getSpawnedType().name()) : null);
		}
	}

}
