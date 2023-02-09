package com.guillaumevdn.gcore.lib.gui.element.item.type;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.element.struct.container.typable.TypableElementTypes;
import com.guillaumevdn.gcore.lib.gui.element.item.type.types.TypeBack;
import com.guillaumevdn.gcore.lib.gui.element.item.type.types.TypeClose;
import com.guillaumevdn.gcore.lib.gui.element.item.type.types.TypeNone;
import com.guillaumevdn.gcore.lib.gui.element.item.type.types.border.TypeDynamicBorderLinear;
import com.guillaumevdn.gcore.lib.gui.element.item.type.types.border.TypeStaticBorder;

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
	public final TypeClose 					CLOSE					= register(new TypeClose("CLOSE"));
	public final TypeStaticBorder 			STATIC_BORDER			= register(new TypeStaticBorder("STATIC_BORDER"));
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
