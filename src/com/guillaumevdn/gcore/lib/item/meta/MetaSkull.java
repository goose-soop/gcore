package com.guillaumevdn.gcore.lib.item.meta;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.element.editor.SlotPlacement;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementString;
import com.guillaumevdn.gcore.lib.element.type.container.ElementItem;
import com.guillaumevdn.gcore.lib.item.ItemCheck;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

/**
 * @author GuillaumeVDN
 */
public final class MetaSkull {

	public static boolean match(ItemMeta itemMeta, ItemMeta referenceMeta, ItemCheck check) {
		SkullMeta meta = ObjectUtils.castOrNull(itemMeta, SkullMeta.class); // might be null if exact match is false
		SkullMeta ref = ObjectUtils.castOrNull(referenceMeta, SkullMeta.class);
		if (ref == null) return true;
		// author
		if (check.isExact() && (meta.hasOwner() != ref.hasOwner() || !meta.getOwner().equals(ref.getOwner()))) return false;
		else if (!check.isExact() && ref.hasOwner() && (meta == null || !meta.hasOwner() || !meta.getOwner().equals(ref.getOwner()))) return false;
		// seems good
		return true;
	}

	public static void write(ItemMeta itemMeta, DataIO writer) throws Throwable {
		SkullMeta meta = ObjectUtils.castOrNull(itemMeta, SkullMeta.class);
		if (meta != null) {
			GameProfile profile = ReflectionObject.of(meta).getField("profile").get();
			if (profile != null) {
				writer.write("ownerId", profile.getId());
				writer.write("ownerName", profile.getName() != null && !profile.getName().isEmpty() ? profile.getName() : null);
				Collection<Property> textures = profile.getProperties().get("textures");
				if (textures != null && !textures.isEmpty()) {
					Property property = textures.iterator().next();
					writer.write("skinData", property.getValue());
					writer.write("skinSignature", property.getSignature());
				}
			}
		}
	}

	public static void read(ItemMeta itemMeta, DataIO reader) throws Throwable {
		SkullMeta meta = ObjectUtils.castOrNull(itemMeta, SkullMeta.class);
		if (meta != null) {
			UUID id = reader.readUUID("ownerId");
			String name = reader.readString("ownerName");
			String data = reader.readString("skinData");
			String signature = reader.readString("skinSignature");
			if (data != null || id != null || name != null) {  // either we build it from data or we rebuild it from a name
				GameProfile profile = new GameProfile(id == null && name == null ? UUID.randomUUID() : null, name);
				if (data != null) {
					profile.getProperties().put("textures", signature != null ? new Property("textures", data, signature) : new Property("textures", data));
				}
				ReflectionObject.of(itemMeta).setField("profile", profile);
			}
		}
	}

	public static void fillElements(ItemMeta sampleMeta, ElementItem item) {
		if (ObjectUtils.instanceOf(sampleMeta, SkullMeta.class)) {
			item.addUUID("owner_id", Need.optional(), SlotPlacement.START_ROW, TextEditorGeneric.descriptionItemSkullId);
			item.addString("owner_name", Need.optional(), TextEditorGeneric.descriptionItemSkullName);
			item.addString("skin_data", Need.optional(), TextEditorGeneric.descriptionItemSkullData);
			item.addString("skin_signature", Need.optional(), TextEditorGeneric.descriptionItemSkullSignature);
		}
	}

	public static void clearElements(ElementItem item) {
		item.remove("owner_id");
		item.remove("owner_name");
		item.remove("skin_data");
		item.remove("skin_signature");
	}

	public static void writeElements(ElementItem item, DataIO writer, Replacer replacer) {
		item.parseElementAs("owner_id", replacer).ifPresentDo(v -> writer.write("ownerId", v));
		item.parseElementAs("owner_name", replacer).ifPresentDo(v -> writer.write("ownerName", v));
		item.parseElementAs("skin_data", replacer).ifPresentDo(v -> writer.write("skinData", v));
		item.parseElementAs("skin_signature", replacer).ifPresentDo(v -> writer.write("skinSignature", v));
	}

	public static void importElements(ElementItem item, ItemMeta itemMeta) {
		SkullMeta meta = ObjectUtils.castOrNull(itemMeta, SkullMeta.class);
		if (meta != null) {
			String id = null;
			String name = null;
			String data = null;
			String signature = null;
			try {
				GameProfile profile = ReflectionObject.of(meta).getField("profile").get();
				if (profile != null) {
					id = profile.getId() != null ? profile.getId().toString() : null;
					name = profile.getName() != null && !profile.getName().isEmpty() ? profile.getName() : null;
					Collection<Property> textures = profile.getProperties().get("textures");
					if (textures != null && !textures.isEmpty()) {
						Property property = textures.iterator().next();
						data = property.getValue();
						data = property.getSignature();
					}
				}
			} catch (Throwable exeption) {
				exeption.printStackTrace();
			}
			item.getElementAs("owner_id", ElementString.class).setValue(id != null ? CollectionUtils.asList(id) : null);
			item.getElementAs("owner_name", ElementString.class).setValue(name != null ? CollectionUtils.asList(name) : null);
			item.getElementAs("skin_data", ElementString.class).setValue(data != null ? CollectionUtils.asList(data) : null);
			item.getElementAs("skin_signature", ElementString.class).setValue(signature != null ? CollectionUtils.asList(signature) : null);
		}
	}

}
