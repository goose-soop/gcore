package com.guillaumevdn.gcore.lib.item.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.Color;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.element.editor.SlotPlacement;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementColor;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementPotionExtra;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementPotionType;
import com.guillaumevdn.gcore.lib.element.type.container.ElementItem;
import com.guillaumevdn.gcore.lib.element.type.list.ElementPotionEffectList;
import com.guillaumevdn.gcore.lib.item.ItemCheck;
import com.guillaumevdn.gcore.lib.item.PotionExtra;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.serialization.adapter.type.AdapterPotionEffect;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public final class MetaPotion {

	public static boolean match(ItemMeta itemMeta, ItemMeta referenceMeta, ItemCheck check) {
		PotionMeta meta = ObjectUtils.castOrNull(itemMeta, PotionMeta.class); // might be null if exact match is false
		PotionMeta ref = ObjectUtils.castOrNull(referenceMeta, PotionMeta.class);
		if (ref == null) return true;
		// base
		if (Version.ATLEAST_1_9) {
			org.bukkit.potion.PotionData baseMeta = meta == null ? null : meta.getBasePotionData();
			org.bukkit.potion.PotionData baseRef = ref.getBasePotionData();
			if (check.isExact()) {
				if (!baseMeta.getType().equals(baseRef.getType())) return false;
				if (baseMeta.isExtended() != baseRef.isExtended()) return false;
				if (baseMeta.isUpgraded() != baseRef.isUpgraded()) return false;
			}
		}
		// color
		if (Version.ATLEAST_1_12) {
			if (check.isExact() && !Objects.deepEquals(meta.getColor(), ref.getColor())) return false;
			else if (!check.isExact() && ref.getColor() != null && (meta == null || ref.getColor().equals(meta.getColor()))) return false;
		}
		// effects
		if (check.isExact()) {
			if (meta.hasCustomEffects() != ref.hasCustomEffects() || meta.getCustomEffects().size() != ref.getCustomEffects().size()) return false;
		} else {
			if (ref.hasCustomEffects() && (meta == null || meta.hasCustomEffects())) return false;
		}
		main: for (PotionEffect refEffect : ref.getCustomEffects()) {
			for (PotionEffect effect : meta.getCustomEffects()) {
				if (effect.equals(refEffect)) {
					continue main;
				}
			}
			return false;
		}
		// seems good
		return true;
	}

	public static void write(ItemMeta itemMeta, DataIO writer) throws Throwable {
		PotionMeta meta = ObjectUtils.castOrNull(itemMeta, PotionMeta.class);
		if (meta != null) {
			// base
			if (Version.ATLEAST_1_9) {
				org.bukkit.potion.PotionData base = meta.getBasePotionData();
				writer.write("potionType", base.getType());
				if (base.isExtended()) writer.write("extended", base.isExtended());
				if (base.isUpgraded()) writer.write("upgraded", base.isUpgraded());
			}
			// color
			if (Version.ATLEAST_1_12) {
				if (meta.hasColor()) {
					writer.write("color", meta.getColor());
				}
			}
			// custom effects
			if (meta.hasCustomEffects()) {
				List<DataIO> list = new ArrayList<>();
				for (PotionEffect effect : meta.getCustomEffects()) {
					DataIO d = new DataIO();
					AdapterPotionEffect.INSTANCE.write(effect, d);
					list.add(d);
				}
				writer.writeDirectList("customEffects", list);
			}
		}
	}

	public static void read(ItemMeta itemMeta, DataIO reader) throws Throwable {
		PotionMeta meta = ObjectUtils.castOrNull(itemMeta, PotionMeta.class);
		if (meta != null) {
			// base
			if (Version.ATLEAST_1_9) {
				PotionType type = reader.readEnum("potionType", PotionType.class);
				if (type != null) {
					Boolean extended = reader.readBoolean("extended");
					Boolean upgraded = reader.readBoolean("upgraded");
					meta.setBasePotionData(new org.bukkit.potion.PotionData(type, extended != null && extended, upgraded != null && upgraded));
				}
			}
			// color
			if (Version.ATLEAST_1_12) {
				Color color = reader.readSerialized("color", Color.class);
				if (color != null) {
					meta.setColor(color);
				}
			}
			// custom effects
			List<DataIO> customEffects = reader.readDirectList("customEffects");
			if (customEffects != null) for (DataIO d : customEffects) {
				PotionEffect effect = AdapterPotionEffect.INSTANCE.read(d);
				if (effect != null) {
					meta.addCustomEffect(effect, true); // true to overwrite existing effect with the same type
				}
			}
		}
	}

	public static void fillElements(ItemMeta sampleMeta, ElementItem item) {
		if (ObjectUtils.instanceOf(sampleMeta, PotionMeta.class)) {
			boolean hasNextRow = false;
			if (Version.ATLEAST_1_9) {
				item.addPotionType("potion_type", Need.optional(), SlotPlacement.START_ROW, TextEditorGeneric.descriptionItemPotionType);
				item.addPotionExtra("potion_extra", Need.optional(PotionExtra.NONE), TextEditorGeneric.descriptionItemPotionExtra);
				hasNextRow = true;
			}
			if (Version.ATLEAST_1_12) {
				item.addColor("color", Need.optional(), hasNextRow ? SlotPlacement.ANY : SlotPlacement.START_ROW, TextEditorGeneric.descriptionItemLeatherArmorColor);
				hasNextRow = true;
			}
			item.addPotionEffectList("custom_effects", Need.optional(), hasNextRow ? SlotPlacement.ANY : SlotPlacement.START_ROW, TextEditorGeneric.descriptionItemPotionCustomEffects);
		}
	}

	public static void clearElements(ElementItem item) {
		item.remove("potion_type");
		item.remove("potion_extra");
		item.remove("color");
		item.remove("custom_effects");
	}

	public static void writeElements(ElementItem item, DataIO writer, Replacer replacer) throws Throwable {
		if (Version.ATLEAST_1_9) {
			item.parseElementAs("potion_type", replacer).ifPresentDo(v -> writer.write("potionType", v));
			item.parseElementAs("potion_extra", replacer).ifPresentDo(extra -> {
				writer.write("extended", PotionExtra.EXTENDED.equals(extra));
				writer.write("upgraded", PotionExtra.UPGRADED.equals(extra));
			});
		}
		if (Version.ATLEAST_1_12) {
			item.parseElementAs("color", replacer).ifPresentDo(v -> writer.write("color", v));
		}
		item.parseElementAsList("custom_effects", PotionEffect.class, replacer).ifPresentDoThrowable(effects -> {
			List<DataIO> list = new ArrayList<>();
			for (PotionEffect effect : effects) {
				DataIO d = new DataIO();
				AdapterPotionEffect.INSTANCE.write(effect, d);
				list.add(d);
			}
			writer.writeDirectList("customEffects", list);
		});
	}

	public static void importElements(ElementItem item, ItemMeta itemMeta) {
		PotionMeta meta = ObjectUtils.castOrNull(itemMeta, PotionMeta.class);
		if (meta != null) {
			if (Version.ATLEAST_1_9) {
				org.bukkit.potion.PotionData base = meta.getBasePotionData();
				item.getElementAs("potion_type", ElementPotionType.class).setValue(CollectionUtils.asList(base.getType().name()));
				item.getElementAs("potion_extra", ElementPotionExtra.class).setValue(base.isExtended() ? CollectionUtils.asList(PotionExtra.EXTENDED.name()) : (base.isUpgraded() ? CollectionUtils.asList(PotionExtra.UPGRADED.name()) : null));
			}
			if (Version.ATLEAST_1_12) {
				item.getElementAs("color", ElementColor.class).setValue(meta.hasColor() ? CollectionUtils.asList(Serializer.COLOR.serialize(meta.getColor())) : null);
			}
			ElementPotionEffectList list = item.getElementAs("custom_effects");
			list.clear();
			for (PotionEffect effect : meta.getCustomEffects()) {
				list.createAndAddElement().importValue(effect);
			}
		}
	}

}
