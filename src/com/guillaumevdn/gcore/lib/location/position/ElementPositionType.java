package com.guillaumevdn.gcore.lib.location.position;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.container.typable.ElementTypableElementType;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public final class ElementPositionType extends ElementTypableElementType<PositionType> {

	public ElementPositionType(Element parent, String id, Text editorDescription) {
		super(PositionTypes.inst(), parent, id, editorDescription);
	}

	@Override
	public Mat editorIconType() {
		return parseGeneric().ifPresentMap(PositionType::getIcon).orElse(CommonMats.REDSTONE);
	}

}
