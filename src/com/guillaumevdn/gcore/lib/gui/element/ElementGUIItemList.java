package com.guillaumevdn.gcore.lib.gui.element;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.list.ListElement;
import com.guillaumevdn.gcore.lib.gui.element.item.ElementGUIItem;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementGUIItemList extends ListElement<ElementGUIItem> {

	public ElementGUIItemList(Element parent, String id, Need need, Text editorDescription) {
		super("GUI item", parent, id, need, editorDescription);
	}

	// element
	@Override
	protected ElementGUIItem createElement(String elementId) {
		return new ElementGUIItem(this, elementId, Need.optional(), null);
	}

	// editor
	@Override
	public Mat editorIconType() {
		return CommonMats.CHEST;
	}

}
