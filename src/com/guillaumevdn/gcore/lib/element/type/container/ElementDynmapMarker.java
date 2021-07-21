package com.guillaumevdn.gcore.lib.element.type.container;

import java.util.List;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.container.ContainerElement;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementLocation;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementText;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementDynmapMarker extends ContainerElement {

	private ElementLocation location = addLocation("location", Need.required(), TextEditorGeneric.descriptionDynmapMarkerLocation);
	private ElementText text = addText("text", Need.required(), TextEditorGeneric.descriptionDynmapMarkerText);

	public ElementDynmapMarker(Element parent, String id, Need need, Text editorDescription) {
		super("dynmap marker", parent, id, need, editorDescription);
	}

	// ----- get
	public ElementLocation getLocation() {
		return location;
	}

	public ElementText getText() {
		return text;
	}

	// ----- editor
	@Override
	public Mat editorIconType() {
		return CommonMats.CLOCK;
	}

	@Override
	public List<String> editorCurrentValue() {
		if (!location.parseGeneric().isPresent()) {
			return null;
		}
		return text.parseGeneric().ifPresentMap(Text::parseLines).orNull();
	}

}
