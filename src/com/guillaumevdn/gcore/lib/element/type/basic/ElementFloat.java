package com.guillaumevdn.gcore.lib.element.type.basic;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementFloat extends ElementValue<Float> {

	private final float min, max;

	public ElementFloat(Element parent, String id, Need need, Text editorDescription) {
		this(parent, id, need, -Float.MAX_VALUE, editorDescription);
	}

	public ElementFloat(Element parent, String id, Need need, float min, Text editorDescription) {
		this(parent, id, need, min, Float.MAX_VALUE, editorDescription);
	}

	public ElementFloat(Element parent, String id, Need need, float min, float max, Text editorDescription) {
		super("float" + (min == -Float.MAX_VALUE && max == Float.MAX_VALUE ? "" : (min == -Float.MAX_VALUE ? " (max. " + max + ")" : (max == Float.MAX_VALUE ? " (min. " + min + ")" : " (" + min + " - " + max + ")"))),
				Float.class, parent, id, need, editorDescription);
		this.min = min;
		this.max = max;
	}

	// get
	public final float getMin() {
		return min;
	}

	public final float getMax() {
		return max;
	}

	// parse
	@Override
	protected void validate(Float value) throws ParsingError {
		if (value < min) {
			throw new ParsingError(this, "Number should be at least " + min);
		} else if (value > max) {
			throw new ParsingError(this, "Number should be at most " + max);
		}
	}

	// editor
	@Override
	public Mat editorIconType() {
		return CommonMats.LIME_DYE;
	}

}
