package com.guillaumevdn.gcore.lib.element.type.container;

import java.util.List;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.container.ContainerElement;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementMatList;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class ElementMatRestriction extends ContainerElement {

	private ElementMatList whitelist = addMatList("whitelist", Need.optional(), TextEditorGeneric.descriptionMatRestrictionWhitelist);
	private ElementMatList blacklist = addMatList("blacklist", Need.optional(), TextEditorGeneric.descriptionMatRestrictionBlacklist);

	public ElementMatRestriction(Element parent, String id, Need need, Text editorDescription) {
		super(parent, id, need, editorDescription);
	}

	// ----- get
	public ElementMatList getWhitelist() {
		return whitelist;
	}

	public ElementMatList getBlacklist() {
		return blacklist;
	}

	public boolean isAllowed(Mat mat, Replacer replacer) {
		if (!readContains()) {
			return true;
		}
		List<Mat> whitelist = this.whitelist.parse(replacer).orNull();
		if (whitelist != null && (ConfigGCore.ignoreInvalidElementValues ? this.whitelist.getRawValueSize() != 0 : !whitelist.isEmpty())) {
			return whitelist.contains(mat);
		}
		List<Mat> blacklist = this.blacklist.parse(replacer).orNull();
		if (blacklist != null && (ConfigGCore.ignoreInvalidElementValues ? this.blacklist.getRawValueSize() != 0 : !blacklist.isEmpty())) {
			return !blacklist.contains(mat);
		}
		return true;
	}

	// ----- editor
	@Override
	public Mat editorIconType() {
		return CommonMats.COMMAND_BLOCK;
	}

	@Override
	public List<String> editorCurrentValue() {
		List<Mat> whitelist = this.whitelist.parseGeneric().orNull();
		if (whitelist != null) {
			return CollectionUtils.asList("whitelist : " + StringUtils.toTextString(", ", whitelist.stream().map(Mat::getId)));
		}
		List<Mat> blacklist = this.blacklist.parseGeneric().orNull();
		if (blacklist != null) {
			return CollectionUtils.asList("blacklist : " + StringUtils.toTextString(", ", blacklist.stream().map(Mat::getId)));
		}
		return null;
	}

}
