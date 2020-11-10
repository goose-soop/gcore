package com.guillaumevdn.gcore.lib.element.type.list;

import java.util.List;

import org.bukkit.potion.PotionEffect;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.list.ListElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParseableListElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsedCache;
import com.guillaumevdn.gcore.lib.element.type.container.ElementPotionEffect;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementPotionEffectList extends ListElement<ElementPotionEffect> implements ParseableListElement<PotionEffect, ElementPotionEffect> {

	public ElementPotionEffectList(Element parent, String id, Need need, Text editorDescription) {
		super("item", parent, id, need, editorDescription);
	}

	// element
	@Override
	protected ElementPotionEffect createElement(String elementId) {
		return new ElementPotionEffect(this, elementId, Need.optional(), null);
	}

	// cache
	private ParsedCache<List<PotionEffect>> cache = new ParsedCache<>();

	@Override
	public ParsedCache<List<PotionEffect>> getCache() {
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
