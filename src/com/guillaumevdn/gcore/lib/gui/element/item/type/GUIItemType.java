package com.guillaumevdn.gcore.lib.gui.element.item.type;

import javax.annotation.Nonnull;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.container.typable.TypableElementType;
import com.guillaumevdn.gcore.lib.gui.element.item.ElementGUIItem;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveGUI;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveItemHolder;
import com.guillaumevdn.gcore.lib.gui.struct.active.ItemHolder;

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
	@Nonnull
	public abstract ActiveItemHolder newActive(ActiveGUI instance, ItemHolder holder, ElementGUIItem element);

}
