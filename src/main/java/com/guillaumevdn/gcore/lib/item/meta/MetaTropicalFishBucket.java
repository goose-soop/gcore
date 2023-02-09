package com.guillaumevdn.gcore.lib.item.meta;

import java.util.Objects;

import org.bukkit.DyeColor;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.element.editor.SlotPlacement;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementDyeColor;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementTropicalFishPattern;
import com.guillaumevdn.gcore.lib.element.type.container.ElementItem;
import com.guillaumevdn.gcore.lib.item.ItemCheck;
import com.guillaumevdn.gcore.lib.item.ItemReference;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public final class MetaTropicalFishBucket {

	public static boolean match(ItemMeta itemMeta, ItemReference reference, ItemCheck check) {
		if (!reference.hasMeta(TropicalFishBucketMeta.class)) return true;
		TropicalFishBucketMeta meta = ObjectUtils.castOrNull(itemMeta, TropicalFishBucketMeta.class);  // might be null if exact match is false

		// variant
		if (check.isExact() && meta.hasVariant() != reference.hasVariant()) {
			ConfigGCore.logspamItemNbt(null, () -> "Item match ; -> one has variant and one doesn't, " + meta.hasVariant() + "/" + reference.hasVariant());
			return false;
		}
		if (!reference.hasVariant()) {
			return true;
		}

		// body color
		if (check.isExact() && (meta == null || !Objects.deepEquals(meta.getBodyColor(), reference.getBodyColor()))) {
			ConfigGCore.logspamItemNbt(null, () -> "Item match ; -> body color don't match, " + (meta == null ? "null meta" : meta.getBodyColor()) + "/" + reference.getBodyColor());
			return false;
		} else if (!check.isExact() && reference.getBodyColor() != null && (meta == null || !reference.getBodyColor().equals(meta.getBodyColor()))) {
			ConfigGCore.logspamItemNbt(null, () -> "Item match ; -> body color don't match, " + (meta == null ? "null meta" : meta.getBodyColor()) + "/" + reference.getBodyColor());
			return false;
		}

		// pattern color
		if (check.isExact() && (meta == null || !Objects.deepEquals(meta.getPatternColor(), reference.getPatternColor()))) {
			ConfigGCore.logspamItemNbt(null, () -> "Item match ; -> pattern color don't match, " + (meta == null ? "null meta" : meta.getPatternColor()) + "/" + reference.getPatternColor());
			return false;
		} else if (!check.isExact() && reference.getPatternColor() != null && (meta == null || !reference.getPatternColor().equals(meta.getPatternColor()))) {
			ConfigGCore.logspamItemNbt(null, () -> "Item match ; -> pattern color don't match, " + (meta == null ? "null meta" : meta.getPatternColor()) + "/" + reference.getPatternColor());
			return false;
		}

		// pattern
		if (check.isExact() && (meta == null || !Objects.deepEquals(meta.getPattern(), reference.getPattern()))) {
			ConfigGCore.logspamItemNbt(null, () -> "Item match ; -> pattern don't match, " + (meta == null ? "null meta" : meta.getPattern()) + "/" + reference.getPattern());
			return false;
		} else if (!check.isExact() && reference.getPattern() != null && (meta == null || !reference.getPattern().equals(meta.getPattern()))) {
			ConfigGCore.logspamItemNbt(null, () -> "Item match ; -> pattern don't match, " + (meta == null ? "null meta" : meta.getPattern()) + "/" + reference.getPattern());
			return false;
		}

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
