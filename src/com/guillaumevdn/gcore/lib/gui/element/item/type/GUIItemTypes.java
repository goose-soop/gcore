package com.guillaumevdn.gcore.lib.gui.element.item.type;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.element.struct.container.typable.TypableElementTypes;
import com.guillaumevdn.gcore.lib.gui.element.item.type.types.TypeBack;
import com.guillaumevdn.gcore.lib.gui.element.item.type.types.TypeNone;
import com.guillaumevdn.gcore.lib.gui.element.item.type.types.border.TypeDynamicBorderLinear;

/**
 * @author GuillaumeVDN
 */
public final class GUIItemTypes extends TypableElementTypes<GUIItemType> {

	public GUIItemTypes() {
		super(GUIItemType.class);
	}

	// ----- types
	public final TypeNone 					NONE 					= register(new TypeNone("NONE"));
	public final TypeBack 					BACK					= register(new TypeBack("BACK"));
	public final TypeDynamicBorderLinear 	DYNAMIC_BORDER_LINEAR	= register(new TypeDynamicBorderLinear("DYNAMIC_BORDER_LINEAR"));

	// ----- values
	public static GUIItemTypes inst() {
		return GCore.inst().getGUIItemTypes();
	}

	@Override
	public GUIItemType defaultValue() {
		return NONE;
	}

}
