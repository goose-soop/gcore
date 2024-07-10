package com.guillaumevdn.gcore.lib.item.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.Color;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import com.guillaumevdn.gcore.GCore;
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
import com.guillaumevdn.gcore.lib.item.ItemReference;
import com.guillaumevdn.gcore.lib.item.PotionExtra;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.reflection.Reflection;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.serialization.adapter.type.AdapterPotionEffect;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public final class MetaPotion {

	public static boolean match(ItemMeta itemMeta, ItemReference reference, ItemCheck check) {
		if (!reference.hasMeta(PotionMeta.class))
			return true;
		PotionMeta meta = ObjectUtils.castOrNull(itemMeta, PotionMeta.class); // might be null if exact match is false

		// base
		if (Version.ATLEAST_1_20_5) {
			final PotionType baseType = meta == null ? null : meta.getBasePotionType();
			final PotionType refType = reference.getBasePotionDataOrType();

			if (baseType == null)
				return false;

			final PotionType realBaseType = PotionType.valueOf(baseType.name().replace("LONG_", "").replace("STRONG_", ""));
			final PotionType realRefType = PotionType.valueOf(refType.name().replace("LONG_", "").replace("STRONG_", ""));

			if (realBaseType != realRefType)
				return false;
			if (check.isExact()) {
				if (baseType.name().startsWith("LONG_") != refType.name().startsWith("LONG_"))
					return false;
				if (baseType.name().startsWith("STRONG_") != refType.name().startsWith("STRONG_"))
					return false;
			} else {
				if (refType.name().startsWith("LONG_") && !baseType.name().startsWith("LONG_"))
					return false;
				if (refType.name().startsWith("STRONG_") && !baseType.name().startsWith("STRONG_"))
					return false;
			}
		} else if (Version.ATLEAST_1_9) {
			try {
				final ReflectionObject baseMeta = meta == null ? null : ReflectionObject.of(meta).invokeMethod("getBasePotionData");
				final ReflectionObject baseRef = ReflectionObject.of(reference.getBasePotionDataOrType());

				if (baseMeta == null)
					return false;
				if (!baseMeta.invokeMethod("getType").equals(baseRef.invokeMethod("getType")))
					return false;
				if (check.isExact()) {
					if (baseMeta.invokeMethod("isExtended").get(boolean.class) != baseRef.invokeMethod("isExtended").get(boolean.class))
						return false;
					if (baseMeta.invokeMethod("isUpgraded").get(boolean.class) != baseRef.invokeMethod("isUpgraded").get(boolean.class))
						return false;
				} else {
					if (baseRef.invokeMethod("isExtended").get(boolean.class) && (baseMeta == null || !baseMeta.invokeMethod("isExtended").get(boolean.class)))
						return false;
					if (baseRef.invokeMethod("isUpgraded").get(boolean.class) && (baseMeta == null || !baseMeta.invokeMethod("isUpgraded").get(boolean.class)))
						return false;
				}
			} catch (Throwable exception) {
				GCore.inst().getMainLogger().error("Could not check potion meta", exception);
				return false;
			}
		}

		// color
		if (Version.ATLEAST_1_12) {
			if (check.isExact() && (meta == null || !Objects.deepEquals(meta.getColor(), reference.getPotionColor())))
				return false;
			else if (!check.isExact() && reference.getPotionColor() != null && (meta == null || reference.getPotionColor().equals(meta.getColor())))
				return false;
		}

		// effects
		if (check.isExact()) {
			if (meta.hasCustomEffects() != reference.hasPotionCustomEffects() || meta.getCustomEffects().size() != reference.getPotionCustomEffects().size())
				return false;
		} else {
			if (reference.hasPotionCustomEffects() && (meta == null || !meta.hasCustomEffects()))
				return false;
		}
		for (PotionEffect refEffect : reference.getPotionCustomEffects()) {
			if (!meta.getCustomEffects().stream().anyMatch(metaEffect -> metaEffect.getType().equals(refEffect.getType()))) { // just check type ;
																																// extended/upgraded will have
																																// been checked above
				return false;
			}
		}

		// seems good
		return true;
	}

	public static void write(ItemMeta itemMeta, DataIO writer) throws Throwable {
		PotionMeta meta = ObjectUtils.castOrNull(itemMeta, PotionMeta.class);
		if (meta != null) {
			// base
			if (Version.ATLEAST_1_20_5) {
				writer.write("potionType", meta.getBasePotionType());
			} else if (Version.ATLEAST_1_9) {
				final ReflectionObject base = ReflectionObject.of(meta).invokeMethod("getBasePotionData");
				writer.write("potionType", base.invokeMethod("getType").get());
				if (base.invokeMethod("isExtended").get(boolean.class))
					writer.write("extended", true);
				if (base.invokeMethod("isUpgraded").get(boolean.class))
					writer.write("upgraded", true);
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
					d.write("version", AdapterPotionEffect.INSTANCE.getVersion());
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
				if (type != null && !Version.ATLEAST_1_20_5) {
					Boolean extended = reader.readBoolean("extended");
					Boolean upgraded = reader.readBoolean("upgraded");
					final Object potionData = Reflection
							.newInstance(Class.forName("org.bukkit.potion.PotionData"), type, extended != null && extended, upgraded != null && upgraded).get();
					ReflectionObject.of(meta).invokeMethod("setBasePotionData", potionData);
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
			if (customEffects != null) {
				for (DataIO d : customEffects) {
					PotionEffect effect = AdapterPotionEffect.INSTANCE.read(d);
					if (effect != null) {
						meta.addCustomEffect(effect, true /* overwrite existing effect with the same type */);
					}
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
				item.addColor("color", Need.optional(), hasNextRow ? SlotPlacement.ANY : SlotPlacement.START_ROW,
						TextEditorGeneric.descriptionItemLeatherArmorColor);
				hasNextRow = true;
			}
			item.addPotionEffectList("custom_effects", Need.optional(), hasNextRow ? SlotPlacement.ANY : SlotPlacement.START_ROW,
					TextEditorGeneric.descriptionItemPotionCustomEffects);
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
				d.write("version", AdapterPotionEffect.INSTANCE.getVersion());
				AdapterPotionEffect.INSTANCE.write(effect, d);
				list.add(d);
			}
			writer.writeDirectList("customEffects", list);
		});
	}

	public static void importElements(ElementItem item, ItemMeta itemMeta) {
		PotionMeta meta = ObjectUtils.castOrNull(itemMeta, PotionMeta.class);
		if (meta != null) {
			if (Version.ATLEAST_1_20_5) {
				item.getElementAs("potion_type", ElementPotionType.class).setValue(CollectionUtils.asList(meta.getBasePotionType().name()));
			} else if (Version.ATLEAST_1_9) {
				try {
					final ReflectionObject base = ReflectionObject.of(meta).invokeMethod("getBasePotionData");
					item.getElementAs("potion_type", ElementPotionType.class)
							.setValue(CollectionUtils.asList(base.invokeMethod("getType").invokeMethod("name").get(String.class)));
					item.getElementAs("potion_extra", ElementPotionExtra.class)
							.setValue(base.invokeMethod("isExtended").get(boolean.class) ? CollectionUtils.asList(PotionExtra.EXTENDED.name())
									: (base.invokeMethod("isUpgraded").get(boolean.class) ? CollectionUtils.asList(PotionExtra.UPGRADED.name()) : null));
				} catch (Throwable exception) {
					GCore.inst().getMainLogger().error("Could not import potion meta", exception);
					return;
				}
			}
			if (Version.ATLEAST_1_12) {
				item.getElementAs("color", ElementColor.class)
						.setValue(meta.hasColor() ? CollectionUtils.asList(Serializer.COLOR.serialize(meta.getColor())) : null);
			}
			ElementPotionEffectList list = item.getElementAs("custom_effects");
			list.clear();
			for (PotionEffect effect : meta.getCustomEffects()) {
				list.createAndAddElement().importValue(effect);
			}
		}
	}

}
