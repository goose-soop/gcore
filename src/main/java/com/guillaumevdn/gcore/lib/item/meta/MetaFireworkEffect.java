package com.guillaumevdn.gcore.lib.item.meta;

import java.util.List;
import java.util.Objects;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.type.container.ElementFireworkEffect;
import com.guillaumevdn.gcore.lib.element.type.container.ElementItem;
import com.guillaumevdn.gcore.lib.item.ItemCheck;
import com.guillaumevdn.gcore.lib.item.ItemReference;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public final class MetaFireworkEffect {

	public static boolean match(ItemMeta itemMeta, ItemReference reference, ItemCheck check) {
		if (!reference.hasMeta(FireworkEffectMeta.class))
			return true;
		FireworkEffectMeta meta = ObjectUtils.castOrNull(itemMeta, FireworkEffectMeta.class); // might be null if exact match is false

		// effect
		if (check.isExact() && (meta == null || !Objects.deepEquals(meta.getEffect(), reference.getEffect())))
			return false;
		else if (!check.isExact() && reference.hasEffect() && (meta == null || !Objects.deepEquals(meta.getEffect(), reference.getEffect())))
			return false;

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
		if (effect.hasFlicker())
			writer.write("flicker", true);
		if (effect.hasTrail())
			writer.write("trail", true);
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
		return type == null ? null
				: FireworkEffect.builder().with(type).withColor(colors).withFade(fadeColors).flicker(flicker != null && flicker).trail(trail != null && trail)
						.build();
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
		ElementFireworkEffect effect = item.getElementAs("effect");
		if (effect != null /* null on non-firework metas */ && effect.readContains()) {
			effect.parse(replacer).ifPresentDo(eff -> {
				writer.writeObject("effect", w -> writeEffect(eff, w));
			});
		}
	}

	public static void importElements(ElementItem item, ItemMeta itemMeta) {
		FireworkEffectMeta meta = ObjectUtils.castOrNull(itemMeta, FireworkEffectMeta.class);
		if (meta != null) {
			item.getElementAs("effect", ElementFireworkEffect.class).importValue(meta.getEffect());
		}
	}

}
