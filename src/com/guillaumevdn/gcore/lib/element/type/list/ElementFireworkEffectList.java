package com.guillaumevdn.gcore.lib.element.type.list;

import java.util.List;

import org.bukkit.FireworkEffect;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.list.ListElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParseableListElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsedCache;
import com.guillaumevdn.gcore.lib.element.type.container.ElementFireworkEffect;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementFireworkEffectList extends ListElement<ElementFireworkEffect> implements ParseableListElement<FireworkEffect, ElementFireworkEffect> {

	public ElementFireworkEffectList(Element parent, String id, Need need, Text editorDescription) {
		super("firework effect", parent, id, need, editorDescription);
	}

	// element
	@Override
	public ElementFireworkEffect createElement(String elementId) {
		return new ElementFireworkEffect(this, elementId, Need.optional(), null);
	}

	// cache
	private ParsedCache<List<FireworkEffect>> cache = new ParsedCache<>();

	@Override
	public ParsedCache<List<FireworkEffect>> getCache() {
		return hasParseableLocations() ? null : cache;
	}

	@Override
	public void resetCache() {
		cache.clear();
	}

	// editor
	@Override
	public Mat editorIconType() {
		return CommonMats.NETHER_STAR;
	}

}
