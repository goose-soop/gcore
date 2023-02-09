package com.guillaumevdn.gcore.lib.element.type.container;

import java.util.List;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.PermissionGCore;
import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.container.ContainerElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementStringList;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class ElementCommandRestriction extends ContainerElement {

	private ElementStringList whitelist = addStringList("whitelist", Need.optional(), TextEditorGeneric.descriptionCommandRestrictionWhitelist);
	private ElementStringList blacklist = addStringList("blacklist", Need.optional(), TextEditorGeneric.descriptionCommandRestrictionBlacklist);

	public ElementCommandRestriction(Element parent, String id, Need need, Text editorDescription) {
		super(parent, id, need, editorDescription);
	}

	// ----- get
	public ElementStringList getWhitelist() {
		return whitelist;
	}

	public ElementStringList getBlacklist() {
		return blacklist;
	}

	public boolean isAllowed(String command, Player player, Replacer replacer) {
		// bypass
		if (PermissionGCore.inst().gcoreBypassCommandRestrictions.has(player)) {
			return true;
		}
		// parse
		List<String> whitelist = null, blacklist = null;
		try {
			whitelist = this.whitelist.parseNoCatch(replacer).orEmptyList();
			blacklist = this.blacklist.parseNoCatch(replacer).orEmptyList();
		} catch (ParsingError error) {
			ParsingError.print(error, whitelist == null ? this.whitelist : this.blacklist);
			return false;
		}
		// check
		String cmd = parseCommand(command);
		if (!whitelist.isEmpty()) {
			return whitelist.stream().anyMatch(w -> cmd.startsWith(parseCommand(w)));
		}
		if (!blacklist.isEmpty() && blacklist.stream().anyMatch(b -> cmd.startsWith(parseCommand(b)))) {
			return false;
		}
		return true;
	}

	private static String parseCommand(String string) {
		if (!string.isEmpty() /* might happen if we just type / for instance */ && string.charAt(0) == '/') {
			string = string.substring(1);
		}
		return string.toLowerCase();
	}

	// ----- editor
	@Override
	public Mat editorIconType() {
		return CommonMats.COMMAND_BLOCK;
	}

	@Override
	public List<String> editorCurrentValue() {
		List<String> whitelist = this.whitelist.parseGeneric().orNull();
		if (whitelist != null) {
			return CollectionUtils.asList("whitelist : " + StringUtils.toTextString(", ", whitelist));
		}
		List<String> blacklist = this.blacklist.parseGeneric().orNull();
		if (blacklist != null) {
			return CollectionUtils.asList("blacklist : " + StringUtils.toTextString(", ", blacklist));
		}
		return null;
	}

}
