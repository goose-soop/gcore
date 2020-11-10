package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.List;

import org.bukkit.Location;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.WorkerGCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementLocation extends ElementValue<Location> {

	public ElementLocation(Element parent, String id, Need need, Text editorDescription) {
		super(Location.class, parent, id, need, editorDescription);
	}

	// editor
	@Override
	public Mat editorIconType() {
		return CommonMats.RAIL;
	}

	@Override
	public List<String> editorIconLore() {
		List<String> lore = super.editorIconLore();
		lore.addAll(TextEditorGeneric.controlImport.parseLines());
		return lore;
	}

	@Override
	public void onEditorClick(ClickCall call) {
		// left-click : enter value
		if (call.getType().equals(ClickType.LEFT)) {
			call.getClicker().closeInventory();
			WorkerGCore.inst().awaitChatWithSuggestedValue(call.getClicker(), TextEditorGeneric.messageElementBasicEditSuggestCurrent, getValueLineOrDefault(0), value -> {
				if (StringUtils.hasPlaceholders(value)) {
					setValue(CollectionUtils.asList(value));
				} else {
					Location loc = null;
					try {
						loc = getSerializer().deserialize(value);
					} catch (Throwable ignored) {}
					setValue(loc == null ? null : CollectionUtils.asList(value));
				}
				call.getGUI().setRegularItem(buildEditorItem(call.getSlot()));
				call.getGUI().openFor(call.getClicker(), call.getPageIndex());
				getSuperElement().onEditorChange(this);
			}, () -> call.getGUI().openFor(call.getClicker(), call.getPageIndex()));
		}
		// shift + right-click : import
		else if (call.getType().equals(ClickType.SHIFT_RIGHT)) {
			call.getClicker().closeInventory();
			WorkerGCore.inst().awaitLocation(call.getClicker(), TextEditorGeneric.messageElementBasicImportLocation, value -> {
				setValue(CollectionUtils.asList(getSerializer().serialize(value)));
				call.getGUI().setRegularItem(buildEditorItem(call.getSlot()));
				call.getGUI().openFor(call.getClicker(), call.getPageIndex());
				getSuperElement().onEditorChange(this);
			}, () -> call.getGUI().openFor(call.getClicker(), call.getPageIndex()));
		}
		// other
		else {
			super.onEditorClick(call);
		}
	}

}
