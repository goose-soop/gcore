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
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementIntegerIntegerMap extends MapElement<Integer, ElementInteger> implements ParseableMapElement<Integer, Integer, ElementInteger> {

	public ElementIntegerIntegerMap(Element parent, String id, Need need, Text editorDescription) {
		super(Integer.class, "integer/integer map", parent, id, need, editorDescription);
	}

	// ----- add
	@Override
	public ElementInteger createElement(String elementId) {
		ElementInteger element = new ElementInteger(this, elementId, Need.optional(), null);
		element.setValue(CollectionUtils.asList("1"));  // don't use the default value otherwise it won't be saved
		return element;
	}

	// ----- parsing
	private ParsedCache<Map<Integer, Integer>> cache = new ParsedCache<>();

	@Override
	public ParsedCache<Map<Integer, Integer>> getCache() {
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
	protected void editorAskKeyAndCreateAndAddElement(ClickCall call, BiConsumer<Integer, ElementInteger> onCreate, Runnable onCancel) {
		WorkerGCore.inst().awaitChat(call.getClicker(), TextEditorGeneric.messageElementCreateEnterId, raw -> {
			// invalid id, or already exists
			final String id = raw.toLowerCase().trim();
			final Integer intId = NumberUtils.integerOrNull(id);
			if (intId == null) {
				TextEditorGeneric.messageElementCreateInvalidId.replace("{value}", () -> id).send(call.getClicker());
				call.reopenGUI();
			} else if (getElement(intId).isPresent()) {
				TextEditorGeneric.messageElementCreateAlreadyExists.replace("{value}", () -> id).send(call.getClicker());
				call.reopenGUI();
			}
			// create element
			else {
				add(intId, createElement(id));
				// reopen GUI (that refreshes it since it's an editor GUI)
				call.reopenGUI();
			}
		}, () -> call.reopenGUI());
	}

}
