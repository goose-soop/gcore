package com.guillaumevdn.gcore.lib.item.meta;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.type.container.ElementItem;
import com.guillaumevdn.gcore.lib.element.type.list.ElementFireworkEffectList;
import com.guillaumevdn.gcore.lib.item.ItemCheck;
import com.guillaumevdn.gcore.lib.item.ItemReference;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public final class MetaFirework {

	public static boolean match(ItemMeta itemMeta, ItemReference reference, ItemCheck check) {
		if (!reference.hasMeta(FireworkMeta.class))
			return true;
		FireworkMeta meta = ObjectUtils.castOrNull(itemMeta, FireworkMeta.class); // might be null if exact match is false

		// effects
		if (check.isExact()) {
			if (meta.hasEffects() != reference.hasEffects() || meta.getEffects().size() != reference.getEffects().size())
				return false;
		} else {
			if (reference.hasEffects() && (meta == null || meta.hasEffects()))
				return false;
		}
		main: for (FireworkEffect refEffect : reference.getEffects()) {
			for (FireworkEffect effect : meta.getEffects()) {
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
		FireworkMeta meta = ObjectUtils.castOrNull(itemMeta, FireworkMeta.class);
		if (meta != null) {
			if (meta.hasEffects()) {
				List<DataIO> list = new ArrayList<>();
				for (FireworkEffect effect : meta.getEffects()) {
					DataIO d = new DataIO();
					MetaFireworkEffect.writeEffect(effect, d);
					list.add(d);
				}
				writer.writeDirectList("effects", list);
			}
		}
	}

	public static void read(ItemMeta itemMeta, DataIO reader) throws Throwable {
		FireworkMeta meta = ObjectUtils.castOrNull(itemMeta, FireworkMeta.class);
		if (meta != null) {
			List<DataIO> effects = reader.readDirectList("effects");
			if (effects != null) {
				for (DataIO d : effects) {
					FireworkEffect effect = MetaFireworkEffect.readEffect(d);
					if (effect != null) {
						meta.addEffect(effect);
					}
				}
			}
		}
	}

	public static void fillElements(ItemMeta sampleMeta, ElementItem item) {
		if (ObjectUtils.instanceOf(sampleMeta, FireworkMeta.class)) {
			item.addFireworkEffectList("effects", Need.optional(), TextEditorGeneric.descriptionItemFireworkEffects);
		}
	}

	public static void clearElements(ElementItem item) {
		item.remove("effects");
	}

	public static void writeElements(ElementItem item, DataIO writer, Replacer replacer) {
		item.parseElementAsList("effects", FireworkEffect.class, replacer).ifPresentDo(effects -> {
			List<DataIO> list = new ArrayList<>();
			for (FireworkEffect effect : effects) {
				DataIO d = new DataIO();
				MetaFireworkEffect.writeEffect(effect, d);
				list.add(d);
			}
			writer.writeDirectList("effects", list);
		});
	}

	public static void importElements(ElementItem item, ItemMeta itemMeta) {
		FireworkMeta meta = ObjectUtils.castOrNull(itemMeta, FireworkMeta.class);
		if (meta != null) {
			ElementFireworkEffectList list = item.getElementAs("effects");
			list.clear();
			for (FireworkEffect effect : meta.getEffects()) {
				list.createAndAddElement().importValue(effect);
			}
		}
	}

}
