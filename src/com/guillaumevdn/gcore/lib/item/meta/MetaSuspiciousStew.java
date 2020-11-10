package com.guillaumevdn.gcore.lib.item.meta;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffect;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.element.editor.SlotPlacement;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.type.container.ElementItem;
import com.guillaumevdn.gcore.lib.element.type.list.ElementPotionEffectList;
import com.guillaumevdn.gcore.lib.item.ItemCheck;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.serialization.adapter.type.AdapterPotionEffect;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public final class MetaSuspiciousStew {

	public static boolean match(ItemMeta itemMeta, ItemMeta referenceMeta, ItemCheck check) {
		SuspiciousStewMeta meta = ObjectUtils.castOrNull(itemMeta, SuspiciousStewMeta.class); // might be null if exact match is false
		SuspiciousStewMeta ref = ObjectUtils.castOrNull(referenceMeta, SuspiciousStewMeta.class);
		if (ref == null) return true;
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
		SuspiciousStewMeta meta = ObjectUtils.castOrNull(itemMeta, SuspiciousStewMeta.class);
		if (meta != null) {
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
		SuspiciousStewMeta meta = ObjectUtils.castOrNull(itemMeta, SuspiciousStewMeta.class);
		if (meta != null) {
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
		if (ObjectUtils.instanceOf(sampleMeta, SuspiciousStewMeta.class)) {
			item.addPotionEffectList("custom_effects", Need.optional(), SlotPlacement.START_ROW, TextEditorGeneric.descriptionItemSuspiciousStewCustomEffects);
		}
	}

	public static void clearElements(ElementItem item) {
		item.remove("custom_effects");
	}

	public static void writeElements(ElementItem item, DataIO writer, Replacer replacer) throws Throwable {
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
		SuspiciousStewMeta meta = ObjectUtils.castOrNull(itemMeta, SuspiciousStewMeta.class);
		if (meta != null) {
			ElementPotionEffectList list = item.getElementAs("custom_effects");
			list.clear();
			for (PotionEffect effect : meta.getCustomEffects()) {
				list.createAndAddElement().importValue(effect);
			}
		}
	}

}
