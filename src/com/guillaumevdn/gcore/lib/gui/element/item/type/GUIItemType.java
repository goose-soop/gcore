package com.guillaumevdn.gcore.lib.gui.element.item.type;

import javax.annotation.Nullable;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.container.typable.TypableElementType;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.gui.element.item.element.ElementGUIItem;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveGUI;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveHolderItem;
import com.guillaumevdn.gcore.lib.gui.struct.active.ItemHolder;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public abstract class GUIItemType extends TypableElementType<ElementGUIItem> {

	private boolean needIcon;

	public GUIItemType(String id, boolean needIcon, Mat icon) {
		super(id, icon);
		this.needIcon = needIcon;
	}

	// elements
	@Override
	protected void doFillTypeSpecificElements(ElementGUIItem item) {
		super.doFillTypeSpecificElements(item);
		if (needIcon) {
			item.addItem("icon", Need.optional(), TextEditorGeneric.descriptionGuiItemIcon);
		}
	}

	// build
	@Nullable
	public abstract ActiveHolderItem newActive(ActiveGUI gui, ItemHolder holder, ElementGUIItem item, Replacer replacer) throws ParsingError;

}
