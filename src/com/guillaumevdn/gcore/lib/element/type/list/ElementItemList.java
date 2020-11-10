package com.guillaumevdn.gcore.lib.element.type.list;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.list.ListElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParseableListElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsedCache;
import com.guillaumevdn.gcore.lib.element.type.container.ElementItem;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementItemList extends ListElement<ElementItem> implements ParseableListElement<ItemStack, ElementItem> {

	public ElementItemList(Element parent, String id, Need need, Text editorDescription) {
		super("item", parent, id, need, editorDescription);
	}

	// element
	@Override
	protected ElementItem createElement(String elementId) {
		return new ElementItem(this, elementId, Need.optional(), null);
	}

	// parsing
	private ParsedCache<List<ItemStack>> cache = new ParsedCache<>();

	@Override
	public ParsedCache<List<ItemStack>> getCache() {
		return hasParseableLocations() ? null : cache;
	}

	@Override
	public void resetCache() {
		cache.clear();
	}

	// editor
	@Override
	public Mat editorIconType() {
		return CommonMats.GOLDEN_APPLE;
	}

}
