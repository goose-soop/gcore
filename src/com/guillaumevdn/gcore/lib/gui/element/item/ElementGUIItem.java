package com.guillaumevdn.gcore.lib.gui.element.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.container.typable.ElementTypableElementType;
import com.guillaumevdn.gcore.lib.element.struct.container.typable.TypableContainerElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementBoolean;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementSound;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementStringList;
import com.guillaumevdn.gcore.lib.gui.element.item.overrideclick.ElementClickTypeOverrideClickMap;
import com.guillaumevdn.gcore.lib.gui.element.item.overrideclick.ElementOverrideClick;
import com.guillaumevdn.gcore.lib.gui.element.item.overrideclick.OverrideClick;
import com.guillaumevdn.gcore.lib.gui.element.item.type.GUIItemType;
import com.guillaumevdn.gcore.lib.gui.element.item.type.GUIItemTypes;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveGUI;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveItemHolder;
import com.guillaumevdn.gcore.lib.gui.struct.active.ItemHolder;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;
import com.guillaumevdn.gcore.lib.tuple.IntegerPair;

/**
 * @author GuillaumeVDN
 */
public class ElementGUIItem extends TypableContainerElement<GUIItemType> {

	private ElementStringList locations = addStringList("locations", Need.optional(), null, TextEditorGeneric.descriptionGuiItemLocations);
	private ElementBoolean persistent = addBoolean("persistent", Need.optional(false), TextEditorGeneric.descriptionGuiItemPersistent);
	private ElementSound clickSound = addSound("click_sound", Need.optional(), TextEditorGeneric.descriptionGuiItemClickSound);
	private ElementClickTypeOverrideClickMap overrideClicks = add(new ElementClickTypeOverrideClickMap(this, "override_clicks", Need.optional(), TextEditorGeneric.descriptionGuiItemOverrideClicks));

	public ElementGUIItem(Element parent, String id, Need need, Text editorDescription) {
		super(GUIItemTypes.inst(), "GUI item", parent, id, need, editorDescription);
	}

	@Override
	protected final ElementTypableElementType<GUIItemType> addType() {
		return add(new ElementGUIItemType(this, "type", TextEditorGeneric.descriptionGuiItemType));
	}

	// get
	public ElementStringList getLocations() {
		return locations;
	}

	public ElementBoolean getPersistent() {
		return persistent;
	}

	public ElementSound getClickSound() {
		return clickSound;
	}

	public ElementClickTypeOverrideClickMap getOverrideClicks() {
		return overrideClicks;
	}

	// do
	private Map<ClickType, Consumer<ClickCall>> overrideClicksCache = null;

	public Map<ClickType, Consumer<ClickCall>> parseOverrideClicks(Replacer replacer) {
		// has cache
		if (overrideClicksCache != null) {
			return overrideClicksCache;
		}
		// add override clicks
		Map<ClickType, Consumer<ClickCall>> result = new HashMap<>();
		getOverrideClicks().parse(replacer).orEmptyMap().forEach((click, action) -> {  // don't ignore NONE type, even for the default element ; the dude choose to set it that way, maybe he wants to cancel the effects of the TYPE
			Consumer<ClickCall> handler = action.getType().buildHandler(action);
			if (handler != null) {
				result.put(click, handler);
			}
		});
		// add default override click
		ElementOverrideClick def = getOverrideClicks().getDefaultElement().orNull();
		OverrideClick parsedDef = def == null ? null : def.parse(replacer).orNull();
		Consumer<ClickCall> defHandler = parsedDef == null ? null : parsedDef.getType().buildHandler(parsedDef);
		if (defHandler != null) {  // don't ignore NONE type, even for the default element ; the dude choose to set it that way, maybe he wants to cancel the effects of the TYPE
			for (ClickType click : ClickType.values()) {
				result.computeIfAbsent(click, __ -> defHandler);
			}
		}
		// cache if no parseable locations
		if (!overrideClicks.hasParseableLocations()) {
			overrideClicksCache = result;
		}
		// done
		return result;
	}

	public List<IntegerPair> parseLocations(Replacer replacer) throws ParsingError {
		List<IntegerPair> result = new ArrayList<>();
		for (String location : locations.parse(replacer).orEmptyList()) {
			try {
				String[] split = location.split(",");
				if (split.length == 1) {
					result.add(IntegerPair.of(-1, Integer.parseInt(split[0])));
				} else if (split.length == 2) {
					result.add(IntegerPair.of(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
				}
			} catch (Throwable ignored) {}
		}
		return result;
	}

	private transient ItemHolder holderCache = null;  // cache holder since it's only calling methods and equals check is with ID
	public ItemHolder getHolder() {
		return holderCache != null ? holderCache : (holderCache = new ItemHolder(getId()) {
			@Override
			public boolean parsePersistent(ActiveGUI instance) {
				return ElementGUIItem.this.getPersistent().parse(instance.getReplacer()).orElse(false);
			}
			@Override
			public ActiveItemHolder newActive(ActiveGUI gui) {
				return ElementGUIItem.this.getType().newActive(gui, this, ElementGUIItem.this);
			}
		});
	}

}
