package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.List;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.WorkerGCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.object.Optional;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementBoolean extends ElementValue<Boolean> {

	public ElementBoolean(Element parent, String id, Need need, Text editorDescription) {
		super(Boolean.class, parent, id, need, editorDescription);
	}

	// editor
	@Override
	public Mat editorIconType() {
		Optional<Boolean> parsed = parseGeneric();
		if (!parsed.isPresent()) return CommonMats.WHITE_WOOL;
		return parsed.orNull() ? CommonMats.GREEN_WOOL : CommonMats.RED_WOOL;
	}

	@Override
	public List<String> editorIconLore() {
		List<String> lore = super.editorIconLore();
		lore.addAll(TextEditorGeneric.controlToggle.parseLines());
		return lore;
	}

	private static final List<String> BOOLEAN_TRUE = CollectionUtils.asList("true", "t", "yes", "y");
	private static final List<String> BOOLEAN_FALSE = CollectionUtils.asList("false", "f", "no", "n");

	@Override
	public void onEditorClick(ClickCall call) {
		// left-click : enter value
		if (call.getType().equals(ClickType.LEFT)) {
			WorkerGCore.inst().awaitChatWithSuggestedValue(call.getClicker(), TextEditorGeneric.messageElementBasicEditSuggestCurrent, getValueLineOrDefault(0), value -> {
				String trim = StringUtils.unformat(value.toLowerCase()).trim();
				if (StringUtils.hasPlaceholders(value)) {
					setValue(CollectionUtils.asList(value));
				} else if (BOOLEAN_TRUE.contains(trim)) {
					setValue(CollectionUtils.asList("true"));
				} else if (BOOLEAN_FALSE.contains(trim)) {
					setValue(CollectionUtils.asList("false"));
				} else {
					setValue(null);
				}
				getSuperElement().onEditorChange(ElementBoolean.this);
				call.getGUI().openFor(call.getClicker(), call.getPageIndex());
				getSuperElement().onEditorChange(this);
			}, () -> call.getGUI().openFor(call.getClicker(), call.getPageIndex()));
		}
		// right-click : toggle
		else if (call.getType().equals(ClickType.RIGHT)) {
			Optional<Boolean> parsed = parseGeneric();
			setValue(CollectionUtils.asList(String.valueOf(!parsed.orElse(true))));
			call.getGUI().setRegularItem(buildEditorItem(call.getSlot()));
			getSuperElement().onEditorChange(ElementBoolean.this);
		}
		// other
		else {
			super.onEditorClick(call);
		}
	}

}
