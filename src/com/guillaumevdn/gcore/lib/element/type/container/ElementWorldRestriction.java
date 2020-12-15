package com.guillaumevdn.gcore.lib.element.type.container;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.container.ParseableContainerElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementWorldList;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class ElementWorldRestriction extends ParseableContainerElement<List<World>> {

	private ElementWorldList whitelist = addWorldList("whitelist", Need.optional(), TextEditorGeneric.descriptionWorldRestrictionWhitelist);
	private ElementWorldList blacklist = addWorldList("blacklist", Need.optional(), TextEditorGeneric.descriptionWorldRestrictionBlacklist);

	public ElementWorldRestriction(Element parent, String id, Need need, Text editorDescription) {
		super("world restriction", parent, id, need, editorDescription);
	}

	// get
	public ElementWorldList getWhitelist() {
		return whitelist;
	}

	public ElementWorldList getBlacklist() {
		return blacklist;
	}

	// parse
	@Override
	public List<World> doParse(Replacer replacer) throws ParsingError {
		List<World> whitelist = this.whitelist.parseNoCatch(replacer).orNull();
		if (whitelist != null && !whitelist.isEmpty()) {
			return whitelist;
		}
		List<World> result = CollectionUtils.asList(Bukkit.getWorlds());
		this.blacklist.parseNoCatch(replacer).ifPresentDo(bl -> result.removeAll(bl));
		return result;
	}

	public boolean isAllowed(World world, Replacer replacer) {
		if (!readContains()) {
			return true;
		}
		List<World> whitelist = this.whitelist.parse(replacer).orNull();
		if (whitelist != null && !whitelist.isEmpty()) {
			return whitelist.contains(world);
		}
		List<World> blacklist = this.blacklist.parse(replacer).orNull();
		if (blacklist != null && !blacklist.isEmpty()) {
			return !blacklist.contains(world);
		}
		return true;
	}

	// editor
	@Override
	public Mat editorIconType() {
		if (!parseGeneric().isPresent()) return CommonMats.MINECART;
		return CommonMats.FURNACE_MINECART;
	}

	@Override
	public List<String> editorCurrentValue() {
		List<World> whitelist = this.whitelist.parseGeneric().orNull();
		if (whitelist != null) {
			return CollectionUtils.asList("whitelist : " + StringUtils.toTextString(", ", whitelist.stream().map(World::getName)));
		}
		List<World> blacklist = this.blacklist.parseGeneric().orNull();
		if (blacklist != null) {
			return CollectionUtils.asList("blacklist : " + StringUtils.toTextString(", ", blacklist.stream().map(World::getName)));
		}
		return null;
	}

}
