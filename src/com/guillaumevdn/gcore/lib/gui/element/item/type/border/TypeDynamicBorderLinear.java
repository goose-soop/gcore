package com.guillaumevdn.gcore.lib.gui.element.item.type.border;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.gui.element.item.ElementGUIItem;
import com.guillaumevdn.gcore.lib.gui.element.item.type.GUIItemType;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveGUI;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveItemHolder;
import com.guillaumevdn.gcore.lib.gui.struct.active.ItemHolder;

/**
 * @author GuillaumeVDN
 */
public class TypeDynamicBorderLinear extends GUIItemType {

	public TypeDynamicBorderLinear(String id) {
		super(id, false, CommonMats.GRAY_STAINED_GLASS_PANE);
	}

	@Override
	protected void doFillTypeSpecificElements(ElementGUIItem item) {
		super.doFillTypeSpecificElements(item);
		item.addInteger("on_count", Need.optional(2), TextEditorGeneric.descriptionGuiItemDynamicBorderLinearOnCount);
		item.addItem("icon_on", Need.optional(), TextEditorGeneric.descriptionGuiItemDynamicBorderLinearIconOn);
		item.addItem("icon_off", Need.optional(), TextEditorGeneric.descriptionGuiItemDynamicBorderLinearIconOff);
	}

	@Override
	public ActiveItemHolder newActive(ActiveGUI instance, ItemHolder holder, ElementGUIItem element) {
		return new ActiveItemHolderBorderLinear(instance, holder, element);
	}

}
