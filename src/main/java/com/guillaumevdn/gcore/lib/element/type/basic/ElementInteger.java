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
public class ElementInteger extends ElementValue<Integer> {

	private final int min, max;

	public ElementInteger(Element parent, String id, Need need, Text editorDescription) {
		this(parent, id, need, Integer.MIN_VALUE, editorDescription);
	}

	public ElementInteger(Element parent, String id, Need need, int min, Text editorDescription) {
		this(parent, id, need, min, Integer.MAX_VALUE, editorDescription);
	}

	public ElementInteger(Element parent, String id, Need need, int min, int max, Text editorDescription) {
		super(Integer.class, parent, id, need, editorDescription);
		this.min = min;
		this.max = max;
	}

	// ----- get
	public final int getMin() {
		return min;
	}

	public final int getMax() {
		return max;
	}

	// ----- parse
	@Override
	protected void validate(Integer value) throws ParsingError {
		if (value < min) {
			throw new ParsingError(this, "Number should be at least " + min);
		} else if (value > max) {
			throw new ParsingError(this, "Number should be at most " + max);
		}
	}

	// ----- editor
	@Override
	public Mat editorIconType() {
		return CommonMats.LIME_DYE;
	}

}
