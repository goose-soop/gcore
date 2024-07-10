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
import com.guillaumevdn.gcore.lib.element.type.basic.ElementInteger;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementStringIntegerMap extends MapElement<String, ElementInteger> implements ParseableMapElement<String, Integer, ElementInteger> {

	public ElementStringIntegerMap(Element parent, String id, Need need, Text editorDescription) {
		super(String.class, parent, id, need, editorDescription);
	}

	// ----- add

	@Override
	public ElementInteger createElement(String elementId) {
		ElementInteger element = new ElementInteger(this, elementId, Need.optional(), null);
		element.setValue(CollectionUtils.asList("1")); // don't use the default value otherwise it won't be saved
		return element;
	}

	// ----- parsing

	private ParsedCache<Map<String, Integer>> cache = new ParsedCache<>();

	@Override
	public ParsedCache<Map<String, Integer>> getCache() {
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
	protected void editorAskKeyAndCreateAndAddElement(ClickCall call, BiConsumer<String, ElementInteger> onCreate, Runnable onCancel) {
		WorkerGCore.inst().awaitChat(call.getClicker(), TextEditorGeneric.messageElementCreateEnterId, raw -> {
			// invalid id, or already exists
			final String id = raw.toLowerCase().trim();
			if (!StringUtils.isAlphanumeric(id)) {
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
