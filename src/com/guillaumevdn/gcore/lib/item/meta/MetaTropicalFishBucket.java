package com.guillaumevdn.gcore.lib.item.meta;

import java.util.Objects;

import org.bukkit.DyeColor;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.element.editor.SlotPlacement;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementDyeColor;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementTropicalFishPattern;
import com.guillaumevdn.gcore.lib.element.type.container.ElementItem;
import com.guillaumevdn.gcore.lib.item.ItemCheck;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public final class MetaTropicalFishBucket {

	public static boolean match(ItemMeta itemMeta, ItemMeta referenceMeta, ItemCheck check) {
		TropicalFishBucketMeta meta = ObjectUtils.castOrNull(itemMeta, TropicalFishBucketMeta.class); // might be null if exact match is false
		TropicalFishBucketMeta ref = ObjectUtils.castOrNull(referenceMeta, TropicalFishBucketMeta.class);
		if (ref == null) return true;
		// variant
		if (check.isExact() && meta.hasVariant() != ref.hasVariant()) {
			return false;
		}
		if (!ref.hasVariant()) {
			return true;
		}
		// body color
		if (check.isExact() && (meta == null || !Objects.deepEquals(meta.getBodyColor(), ref.getBodyColor()))) return false;
		else if (!check.isExact() && ref.getBodyColor() != null && (meta == null || !ref.getBodyColor().equals(meta.getBodyColor()))) return false;
		// pattern color
		if (check.isExact() && (meta == null || !Objects.deepEquals(meta.getPatternColor(), ref.getPatternColor()))) return false;
		else if (!check.isExact() && ref.getPatternColor() != null && (meta == null || !ref.getPatternColor().equals(meta.getPatternColor()))) return false;
		// pattern
		if (check.isExact() && (meta == null || !Objects.deepEquals(meta.getPattern(), ref.getPattern()))) return false;
		else if (!check.isExact() && ref.getPattern() != null && (meta == null || !ref.getPattern().equals(meta.getPattern()))) return false;
		// seems good
		return true;
	}

	public static void write(ItemMeta itemMeta, DataIO writer) {
		TropicalFishBucketMeta meta = ObjectUtils.castOrNull(itemMeta, TropicalFishBucketMeta.class);
		if (meta != null) {
			writer.write("bodyColor", meta.getBodyColor());
			writer.write("patternColor", meta.getPatternColor());
			writer.write("pattern", meta.getPattern());
		}
	}

	public static void read(ItemMeta itemMeta, DataIO reader) {
		TropicalFishBucketMeta meta = ObjectUtils.castOrNull(itemMeta, TropicalFishBucketMeta.class);
		if (meta != null) {
			DyeColor bodyColor = reader.readEnum("bodyColor", DyeColor.class);
			if (bodyColor != null) {
				meta.setBodyColor(bodyColor);
			}
			DyeColor patternColor = reader.readEnum("patternColor", DyeColor.class);
			if (patternColor != null) {
				meta.setPatternColor(patternColor);
			}
			TropicalFish.Pattern pattern = reader.readEnum("pattern", TropicalFish.Pattern.class);
			if (pattern != null) {
				meta.setPattern(pattern);
			}
		}
	}

	public static void fillElements(ItemMeta sampleMeta, ElementItem item) {
		if (ObjectUtils.instanceOf(sampleMeta, TropicalFishBucketMeta.class)) {
			item.addDyeColor("color", Need.optional(), SlotPlacement.START_ROW, TextEditorGeneric.descriptionItemTropicalFishBodyColor);
			item.addDyeColor("pattern_color", Need.optional(), TextEditorGeneric.descriptionItemTropicalFishPatternColor);
			item.add(new ElementTropicalFishPattern(item, "pattern", Need.optional(), TextEditorGeneric.descriptionItemTropicalFishPattern));
		}
	}

	public static void clearElements(ElementItem item) {
		item.remove("color");
		item.remove("pattern_color");
		item.remove("pattern");
	}

	public static void writeElements(ElementItem item, DataIO writer, Replacer replacer) {
		item.parseElementAs("color", replacer).ifPresentDo(v -> writer.write("color", v));
		item.parseElementAs("pattern_color", replacer).ifPresentDo(v -> writer.write("patternColor", v));
		item.parseElementAs("pattern", replacer).ifPresentDo(v -> writer.write("pattern", v));
	}

	public static void importElements(ElementItem item, ItemMeta itemMeta) {
		TropicalFishBucketMeta meta = ObjectUtils.castOrNull(itemMeta, TropicalFishBucketMeta.class);
		if (meta != null) {
			item.getElementAs("color", ElementDyeColor.class).setValue(CollectionUtils.asList(meta.getBodyColor().name()));
			item.getElementAs("pattern_color", ElementDyeColor.class).setValue(CollectionUtils.asList(meta.getPatternColor().name()));
			item.getElementAs("pattern", ElementDyeColor.class).setValue(CollectionUtils.asList(meta.getPattern().name()));
		}
	}

}
