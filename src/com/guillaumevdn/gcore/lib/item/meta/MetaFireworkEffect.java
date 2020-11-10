package com.guillaumevdn.gcore.lib.item.meta;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.type.container.ElementFireworkEffect;
import com.guillaumevdn.gcore.lib.element.type.container.ElementItem;
import com.guillaumevdn.gcore.lib.item.ItemCheck;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public final class MetaFireworkEffect {

	public static boolean match(ItemMeta itemMeta, ItemMeta referenceMeta, ItemCheck check) {
		FireworkEffectMeta meta = ObjectUtils.castOrNull(itemMeta, FireworkEffectMeta.class); // might be null if exact match is false
		FireworkEffectMeta ref = ObjectUtils.castOrNull(referenceMeta, FireworkEffectMeta.class);
		if (ref == null) return true;
		// effect
		if (check.isExact() && (meta.hasEffect() != ref.hasEffect() || !meta.getEffect().equals(ref.getEffect()))) return false;
		else if (!check.isExact() && ref.hasEffect() && (meta == null || !meta.hasEffect() || !meta.getEffect().equals(ref.getEffect()))) return false;
		// seems good
		return true;
	}

	public static void write(ItemMeta itemMeta, DataIO writer) throws Throwable {
		FireworkEffectMeta meta = ObjectUtils.castOrNull(itemMeta, FireworkEffectMeta.class);
		if (meta != null) {
			if (meta.hasEffect()) {
				writer.writeObject("effect", d -> writeEffect(meta.getEffect(), d));
			}
		}
	}

	public static void writeEffect(FireworkEffect effect, DataIO writer) {
		writer.write("type", effect.getType());
		writer.writeSerializedList("colors", effect.getColors());
		writer.writeSerializedList("fadeColors", effect.getFadeColors());
		if (effect.hasFlicker()) writer.write("flicker", true);
		if (effect.hasTrail()) writer.write("trail", true);
	}

	public static void read(ItemMeta itemMeta, DataIO reader) throws Throwable {
		FireworkEffectMeta meta = ObjectUtils.castOrNull(itemMeta, FireworkEffectMeta.class);
		if (meta != null) {
			DataIO effect = reader.readObject("effect");
			if (effect != null) {
				FireworkEffect eff = readEffect(effect);
				if (eff != null) {
					meta.setEffect(eff);
				}
			}
		}
	}

	public static FireworkEffect readEffect(DataIO reader) {
		FireworkEffect.Type type = reader.readEnum("type", FireworkEffect.Type.class);
		List<Color> colors = reader.readSerializedList("colors", Color.class);
		List<Color> fadeColors = reader.readSerializedList("fadeColors", Color.class);
		Boolean flicker = reader.readBoolean("flicker");
		Boolean trail = reader.readBoolean("trail");
		return type == null ? null : FireworkEffect.builder().with(type).withColor(colors).withFade(fadeColors).flicker(flicker != null && flicker).trail(trail != null && trail).build();
	}

	public static void fillElements(ItemMeta sampleMeta, ElementItem item) {
		if (ObjectUtils.instanceOf(sampleMeta, FireworkEffectMeta.class)) {
			item.addFireworkEffect("effect", Need.optional(), TextEditorGeneric.descriptionItemFireworkEffect);
		}
	}

	public static void clearElements(ElementItem item) {
		item.remove("effect");
	}

	public static void writeElements(ElementItem item, DataIO writer, Replacer replacer) {
		item.parseElementAs("effect", FireworkEffect.class, replacer).ifPresentDo(effect -> {
			writer.writeObject("effect", w -> writeEffect(effect, w));
		});
	}

	public static void importElements(ElementItem item, ItemMeta itemMeta) {
		FireworkEffectMeta meta = ObjectUtils.castOrNull(itemMeta, FireworkEffectMeta.class);
		if (meta != null) {
			item.getElementAs("effect", ElementFireworkEffect.class).importValue(meta.getEffect());
		}
	}

}
