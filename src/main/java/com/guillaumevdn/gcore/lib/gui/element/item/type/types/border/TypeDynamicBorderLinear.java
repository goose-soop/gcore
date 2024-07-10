package com.guillaumevdn.gcore.lib.gui.element.item.type.types.border;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.type.container.ElementItemMode;
import com.guillaumevdn.gcore.lib.gui.element.item.ElementGUIItem;
import com.guillaumevdn.gcore.lib.gui.element.item.type.GUIItemType;
import com.guillaumevdn.gcore.lib.gui.element.item.type.IconNeed;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveGUI;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveItemHolder;
import com.guillaumevdn.gcore.lib.gui.struct.active.ItemHolder;

/**
 * @author GuillaumeVDN
 */
public class TypeDynamicBorderLinear extends GUIItemType {

	public TypeDynamicBorderLinear(String id) {
		super(id, IconNeed.USELESS, CommonMats.GRAY_STAINED_GLASS_PANE);
	}

	@Override
	protected void doFillTypeSpecificElements(ElementGUIItem item) {
		super.doFillTypeSpecificElements(item);
		item.addInteger("on_count", Need.optional(2), TextEditorGeneric.descriptionGuiItemDynamicBorderLinearOnCount);
		item.addItem("icon_on", Need.optional(), ElementItemMode.BUILDABLE, TextEditorGeneric.descriptionGuiItemDynamicBorderLinearIconOn);
		item.addItem("icon_off", Need.optional(), ElementItemMode.BUILDABLE, TextEditorGeneric.descriptionGuiItemDynamicBorderLinearIconOff);
		item.addInteger("refresh_ticks", Need.optional(ConfigGCore.dynamicBorderRefreshTicks), 1,
				TextEditorGeneric.descriptionGuiItemDynamicBorderLinearRefreshTicks);
	}

	@Override
	public ActiveItemHolder newActive(ActiveGUI instance, ItemHolder holder, ElementGUIItem element) {
		return new ActiveItemHolderBorderLinear(instance, holder, element, element.directParseOrNull("refresh_ticks", instance.getReplacer()));
	}

}
