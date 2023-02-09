package com.guillaumevdn.gcore.lib.item.meta;

import java.util.Objects;

import org.bukkit.Color;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.element.editor.SlotPlacement;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementColor;
import com.guillaumevdn.gcore.lib.element.type.container.ElementItem;
import com.guillaumevdn.gcore.lib.item.ItemCheck;
import com.guillaumevdn.gcore.lib.item.ItemReference;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public final class MetaLeatherArmor {

	public static boolean match(ItemMeta itemMeta, ItemReference reference, ItemCheck check) {
		if (!reference.hasMeta(LeatherArmorMeta.class)) return true;
		LeatherArmorMeta meta = ObjectUtils.castOrNull(itemMeta, LeatherArmorMeta.class);  // might be null if exact match is false

		// color
		if (check.isExact() && (meta == null || !Objects.deepEquals(meta.getColor(), reference.getArmorColor()))) return false;
		else if (!check.isExact() && reference.getArmorColor() != null && (meta == null || reference.getArmorColor().equals(meta.getColor()))) return false;

		// seems good
		return true;
	}

	public static void write(ItemMeta itemMeta, DataIO writer) {
		LeatherArmorMeta meta = ObjectUtils.castOrNull(itemMeta, LeatherArmorMeta.class);
		if (meta != null) {
			if (meta.getColor() != null) {
				writer.write("color", meta.getColor());
			}
		}
	}

	public static void read(ItemMeta itemMeta, DataIO reader) {
		LeatherArmorMeta meta = ObjectUtils.castOrNull(itemMeta, LeatherArmorMeta.class);
		if (meta != null) {
			Color color = reader.readSerialized("color", Color.class);
			if (color != null) {
				meta.setColor(color);
			}
		}
	}

	public static void fillElements(ItemMeta sampleMeta, ElementItem item) {
		if (ObjectUtils.instanceOf(sampleMeta, LeatherArmorMeta.class)) {
			item.addColor("color", Need.optional(), SlotPlacement.START_ROW, TextEditorGeneric.descriptionItemLeatherArmorColor);
		}
	}

	public static void clearElements(ElementItem item) {
		item.remove("color");
	}

	public static void writeElements(ElementItem item, DataIO writer, Replacer replacer) {
		item.parseElementAs("color", replacer).ifPresentDo(v -> writer.write("color", v));
	}

	public static void importElements(ElementItem item, ItemMeta itemMeta) {
		LeatherArmorMeta meta = ObjectUtils.castOrNull(itemMeta, LeatherArmorMeta.class);
		if (meta != null) {
			item.getElementAs("color", ElementColor.class).setValue(meta.getColor() != null ? CollectionUtils.asList(Serializer.COLOR.serialize(meta.getColor())) : null);
		}
	}

}
