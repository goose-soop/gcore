package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.List;

import org.bukkit.World;

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
public class ElementWorld extends ElementValue<World> {

	public ElementWorld(Element parent, String id, Need need, Text editorDescription) {
		super(World.class, parent, id, need, editorDescription);
	}

	// ----- editor
	@Override
	public Mat editorIconType() {
		return CommonMats.FURNACE_MINECART;
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
			WorkerGCore.inst().awaitChatWithSuggestedValue(call.getClicker(), TextEditorGeneric.messageElementBasicEditSuggestCurrent, getRawValueLineOrDefault(0), value -> {
				if (StringUtils.hasPlaceholders(value)) {
					setValue(CollectionUtils.asList(value));
				} else {
					World world = null;
					try {
						world = getSerializer().deserialize(value);
					} catch (Throwable ignored) {}
					setValue(world == null ? null : CollectionUtils.asList(value));
				}
				call.getGUI().setRegularItem(buildEditorItem(call.getPageIndex(), call.getSlot()));
				call.reopenGUI();
				getSuperElement().onEditorChange(this);
			}, () -> call.reopenGUI());
		}
		// right-click : import
		else if (call.getType().equals(ClickType.RIGHT)) {
			setValue(CollectionUtils.asList(getSerializer().serialize(call.getClicker().getWorld())));
			call.getGUI().setRegularItem(buildEditorItem(call.getPageIndex(), call.getSlot()));
			call.reopenGUI();
			getSuperElement().onEditorChange(this);
		}
		// other
		else {
			super.onEditorClick(call);
		}
	}

}
