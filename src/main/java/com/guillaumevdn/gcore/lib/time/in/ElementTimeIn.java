package com.guillaumevdn.gcore.lib.time.in;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.basic.BasicElement;
import com.guillaumevdn.gcore.lib.element.struct.container.ParseableContainerElement;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementInteger;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public abstract class ElementTimeIn<T extends TimeIn> extends ParseableContainerElement<T> {

	protected final ElementInteger hour = addInteger("hour", Need.optional(0), 0, 23, TextEditorGeneric.descriptionElementTimeHour);
	protected final ElementInteger minute = addInteger("minute", Need.optional(0), 0, 59, TextEditorGeneric.descriptionElementTimeMinute);

	public ElementTimeIn(Element parent, String id, Need need, Text editorDescription) {
		super(parent, id, need, editorDescription);
	}

	// ----- get
	public final ElementInteger getHour() {
		return hour;
	}

	public final ElementInteger getMinute() {
		return minute;
	}

	// ----- editor
	@Override
	public Mat editorIconType() {
		return CommonMats.CLOCK;
	}

	@Override
	public List<String> editorCurrentValue() {
		Stream<String> stream = values().stream().flatMap(elem -> ((BasicElement) elem).getRawValueOrDefault() == null ? Stream.empty() : ((BasicElement) elem).getRawValueOrDefault().stream());
		List<String> all = stream.filter(value -> value != null).collect(Collectors.toList());
		return all.isEmpty() ? null : all;
	}

}
