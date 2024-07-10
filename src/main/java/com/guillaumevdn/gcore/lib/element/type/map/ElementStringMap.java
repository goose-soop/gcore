package com.guillaumevdn.gcore.lib.element.type.map;

import java.util.Map;
import java.util.function.BiConsumer;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.WorkerGCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.map.MapElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParseableMapElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsedCache;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementString;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementStringMap extends MapElement<String, ElementString> implements ParseableMapElement<String, String, ElementString> {

	public ElementStringMap(Element parent, String id, Need need, Text editorDescription) {
		super(String.class, parent, id, need, editorDescription);
	}

	// ----- add
	@Override
	public ElementString createElement(String elementId) {
		ElementString elem = new ElementString(this, elementId, Need.optional(), null);
		elem.setValue(CollectionUtils.asList("value")); // set a value to force it to save
		return elem;
	}

	// ----- parsing
	private ParsedCache<Map<String, String>> cache = new ParsedCache<>();

	@Override
	public ParsedCache<Map<String, String>> getCache() {
		return hasParseableLocations() ? null : cache;
	}

	@Override
	public final void resetCache() {
		cache.clear();
	}

	// ----- editor
	@Override
	public Mat editorIconType() {
		return CommonMats.ENCHANTING_TABLE;
	}

	@Override
	protected void editorAskKeyAndCreateAndAddElement(ClickCall call, BiConsumer<String, ElementString> onCreate, Runnable onCancel) {
		WorkerGCore.inst().awaitChat(call.getClicker(), TextEditorGeneric.messageElementCreateEnterId, raw -> {
			// invalid id, or already exists
			final String id = raw.toLowerCase().trim();
			if (!StringUtils.isAlphanumeric(id.replace("_", ""))) {
				TextEditorGeneric.messageElementCreateInvalidId.replace("{value}", () -> id).send(call.getClicker());
				call.reopenGUI();
			} else if (getElement(id).isPresent()) {
				TextEditorGeneric.messageElementCreateAlreadyExists.replace("{value}", () -> id).send(call.getClicker());
				call.reopenGUI();
			}
			// create element
			else {
				add(id, createElement(id));
				// reopen GUI (that refreshes it since it's an editor GUI)
				call.reopenGUI();
			}
		}, () -> call.reopenGUI());
	}

}
