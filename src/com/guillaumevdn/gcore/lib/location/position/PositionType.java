package com.guillaumevdn.gcore.lib.location.position;

import javax.annotation.Nonnull;

import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.container.typable.TypableElementType;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public abstract class PositionType extends TypableElementType<ElementPosition> {

	public PositionType(String id, Mat icon) {
		super(id, icon);
	}

	public abstract Position doParse(ElementPosition position, @Nonnull Replacer replacer) throws ParsingError;

}
