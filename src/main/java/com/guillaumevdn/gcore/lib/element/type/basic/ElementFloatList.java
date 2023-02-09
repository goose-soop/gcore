package com.guillaumevdn.gcore.lib.element.type.basic;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementFloatList extends ElementValueList<Float> {

	
	private final float min, max;

	public ElementFloatList(Element parent, String id, Need need, Text editorDescription) {
		this(parent, id, need, -Float.MAX_VALUE, editorDescription);
	}

	public ElementFloatList(Element parent, String id, Need need, float min, Text editorDescription) {
		this(parent, id, need, min, Float.MAX_VALUE, editorDescription);
	}

	public ElementFloatList(Element parent, String id, Need need, float min, float max, Text editorDescription) {
		super(Float.class, parent, id, need, editorDescription);
		this.min = min;
		this.max = max;
	}

	// ----- get
	public final float getMin() {
		return min;
	}

	public final float getMax() {
		return max;
	}

	// ----- parse
	@Override
	protected void validate(Float value) throws ParsingError {
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

	@Override
	protected String editorNewLine() {
		return StringUtils.toTextString(Math.pow(10d, (double) NumberUtils.random(0, 3)), 2);
	}

}
