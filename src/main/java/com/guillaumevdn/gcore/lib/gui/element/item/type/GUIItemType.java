package com.guillaumevdn.gcore.lib.gui.element.item.type;

import javax.annotation.Nonnull;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.container.typable.TypableElementType;
import com.guillaumevdn.gcore.lib.element.type.container.ElementItemMode;
import com.guillaumevdn.gcore.lib.gui.element.item.ElementGUIItem;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveGUI;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveItemHolder;
import com.guillaumevdn.gcore.lib.gui.struct.active.ItemHolder;

/**
 * @author GuillaumeVDN
 */
public abstract class GUIItemType extends TypableElementType<ElementGUIItem> {

	private final IconNeed iconNeed;

	public GUIItemType(String id, IconNeed iconNeed, Mat icon) {
		super(id, icon);
		this.iconNeed = iconNeed;
	}

	public final IconNeed getIconNeed() {
		return iconNeed;
	}

	protected @Override void doFillTypeSpecificElements(ElementGUIItem item) {
		super.doFillTypeSpecificElements(item);
		if (!iconNeed.equals(IconNeed.USELESS)) {
			item.addItem("icon", iconNeed.equals(IconNeed.REQUIRED) ? Need.required() : Need.optional(), ElementItemMode.BUILDABLE,
					TextEditorGeneric.descriptionGuiItemIcon);
		}
	}

	public @Nonnull abstract ActiveItemHolder newActive(ActiveGUI instance, ItemHolder holder, ElementGUIItem element);

}
