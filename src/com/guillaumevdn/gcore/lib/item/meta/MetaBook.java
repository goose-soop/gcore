package com.guillaumevdn.gcore.lib.item.meta;

import java.util.List;
import java.util.Objects;

import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.element.editor.SlotPlacement;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementString;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementStringList;
import com.guillaumevdn.gcore.lib.element.type.container.ElementItem;
import com.guillaumevdn.gcore.lib.item.ItemCheck;
import com.guillaumevdn.gcore.lib.item.ItemReference;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public final class MetaBook {

	public static boolean match(ItemMeta itemMeta, ItemReference reference, ItemCheck check) {
		if (!reference.hasMeta(BookMeta.class)) return true;
		BookMeta meta = ObjectUtils.castOrNull(itemMeta, BookMeta.class);  // might be null if exact match is false

		// author
		if (check.isExact() && (meta == null || !Objects.deepEquals(meta.getAuthor(), reference.getAuthor()))) return false;
		else if (!check.isExact() && reference.hasAuthor() && (meta == null || !Objects.deepEquals(meta.getAuthor(), reference.getAuthor()))) return false;

		// title
		if (check.isExact() && (meta == null || !Objects.deepEquals(meta.getTitle(), reference.getTitle()))) return false;
		else if (!check.isExact() && reference.hasTitle() && (meta == null || !Objects.deepEquals(meta.getTitle(), reference.getTitle()))) return false;

		// author
		if (Version.ATLEAST_1_9) {
			if (check.isExact() && (meta == null || !Objects.deepEquals(meta.getGeneration(), reference.getGeneration()))) return false;
			else if (!check.isExact() && reference.hasGeneration() && (meta == null || !Objects.deepEquals(meta.getGeneration(), reference.getGeneration()))) return false;
		}

		// pages
		if (check.isExact() && (meta == null || meta.hasPages() != reference.hasPages() || !CollectionUtils.contentEquals(meta.getPages(), reference.getPages()))) return false;
		else if (!check.isExact() && reference.hasPages() && (meta == null || !meta.hasPages() || !CollectionUtils.contentEquals(meta.getPages(), reference.getPages()))) return false;

		// seems good
		return true;
	}

	public static void write(ItemMeta itemMeta, DataIO writer) {
		BookMeta meta = ObjectUtils.castOrNull(itemMeta, BookMeta.class);
		if (meta != null) {
			// author
			if (meta.hasAuthor()) {
				writer.write("author", meta.getAuthor());
			}
			// title
			if (meta.hasTitle()) {
				writer.write("title", meta.getTitle());
			}
			// generation
			if (Version.ATLEAST_1_9 && meta.hasGeneration()) {
				writer.write("generation", meta.getGeneration());
			}
			// pages
			if (meta.hasPages()) {
				writer.writeSerializedList("pages", meta.getPages());
			}
		}
	}

	public static void read(ItemMeta itemMeta, DataIO reader) {
		BookMeta meta = ObjectUtils.castOrNull(itemMeta, BookMeta.class);
		if (meta != null) {
			// author
			String author = reader.readString("author");
			if (author != null) {
				meta.setAuthor(author);
			}
			// title
			String title = reader.readString("title");
			if (title != null) {
				meta.setTitle(title);
			}
			// generation
			String generation = !Version.ATLEAST_1_9 ? null : reader.readString("generation");
			if (generation != null) {
				meta.setGeneration(ObjectUtils.safeValueOf(generation, org.bukkit.inventory.meta.BookMeta.Generation.class));
			}
			// pages
			List<String> pages = reader.readDirectList("pages");
			if (pages != null) {
				meta.setPages(pages);
			}
		}
	}

	public static void fillElements(ItemMeta sampleMeta, ElementItem item) {
		if (ObjectUtils.instanceOf(sampleMeta, BookMeta.class)) {
			item.addString("author", Need.optional(), SlotPlacement.START_ROW, TextEditorGeneric.descriptionItemBookAuthor);
			item.addString("title", Need.optional(), TextEditorGeneric.descriptionItemBookTitle);
			if (Version.ATLEAST_1_9) {
				item.add(new com.guillaumevdn.gcore.lib.element.type.basic.ElementBookGeneration(item, "generation", Need.optional(), TextEditorGeneric.descriptionItemBookGeneration));
			}
			item.addStringList("pages", Need.optional(), TextEditorGeneric.descriptionItemBookPages);
		}
	}

	public static void clearElements(ElementItem item) {
		item.remove("author");
		item.remove("title");
		item.remove("generation");
		item.remove("pages");
	}

	public static void writeElements(ElementItem item, DataIO writer, Replacer replacer) {
		item.parseElementAs("author", replacer).ifPresentDo(v -> writer.write("author", v));
		item.parseElementAs("title", replacer).ifPresentDo(v -> writer.write("title", v));
		if (Version.ATLEAST_1_9) {
			item.parseElementAs("generation", replacer).ifPresentDo(v -> writer.write("generation", v));  // if not null, this will generate a new serializer for book generation
		}
		item.parseElementAs("pages", replacer).ifPresentDo(v -> writer.writeSerializedList("pages", (List<String>) v));
	}

	public static void importElements(ElementItem item, ItemMeta itemMeta) {
		BookMeta meta = ObjectUtils.castOrNull(itemMeta, BookMeta.class);
		if (meta != null) {
			item.getElementAs("author", ElementString.class).setValue(meta.hasAuthor() ? CollectionUtils.asList(meta.getAuthor()) : null);
			item.getElementAs("title", ElementString.class).setValue(meta.hasTitle() ? CollectionUtils.asList(meta.getTitle()) : null);
			if (Version.ATLEAST_1_9) {
				item.getElementAs("generation", com.guillaumevdn.gcore.lib.element.type.basic.ElementBookGeneration.class).setValue(meta.hasGeneration() ? CollectionUtils.asList(meta.getGeneration().name()) : null);
			}
			item.getElementAs("pages", ElementStringList.class).setValue(meta.hasPages() ? meta.getPages() : null);
		}
	}

}
