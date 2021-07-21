package com.guillaumevdn.gcore.lib.element.type.container;

import java.util.List;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.container.ContainerElement;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.time.duration.ElementDuration;

/**
 * @author GuillaumeVDN
 */
public class ElementTimeLimit extends ContainerElement {

	protected ElementDuration duration = addDuration("duration", Need.required(), null, null, TextEditorGeneric.descriptionTimeLimitDuration);
	protected ElementNotify reminder = addNotify("reminder", Need.optional(), TextEditorGeneric.descriptionTimeLimitReminder);

	public ElementTimeLimit(Element parent, String id, Need need, Text editorDescription) {
		super("time limit", parent, id, need, editorDescription);
	}

	// ----- get
	public ElementDuration getDuration() {
		return duration;
	}

	public ElementNotify getReminder() {
		return reminder;
	}

	// ----- editor
	@Override
	public Mat editorIconType() {
		return CommonMats.REPEATER;
	}

	@Override
	public List<String> editorCurrentValue() {
		return duration.editorCurrentValue();
	}

}
