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
public class ElementLong extends ElementValue<Long> {

	private final long min, max;

	public ElementLong(Element parent, String id, Need need, Text editorDescription) {
		this(parent, id, need, Long.MIN_VALUE, editorDescription);
	}

	public ElementLong(Element parent, String id, Need need, long min, Text editorDescription) {
		this(parent, id, need, min, Long.MAX_VALUE, editorDescription);
	}

	public ElementLong(Element parent, String id, Need need, long min, long max, Text editorDescription) {
		super("long" + (min == Long.MIN_VALUE && max == Long.MAX_VALUE ? "" : (min == Long.MIN_VALUE ? " (max. " + max + ")" : (max == Long.MAX_VALUE ? " (min. " + min + ")" : " (" + min + " - " + max + ")"))),
				Long.class, parent, id, need, editorDescription);
		this.min = min;
		this.max = max;
	}

	// ----- get
	public final long getMin() {
		return min;
	}

	public final long getMax() {
		return max;
	}

	// ----- parse
	@Override
	protected void validate(Long value) throws ParsingError {
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
