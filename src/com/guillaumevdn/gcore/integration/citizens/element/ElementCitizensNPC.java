package com.guillaumevdn.gcore.integration.citizens.element;

import java.util.List;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.integration.citizens.IntegrationInstanceCitizens;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.editor.EnumSelectorGUI;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementAbstractEnum;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.string.Text;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

/**
 * @author GuillaumeVDN
 */
public class ElementCitizensNPC extends ElementAbstractEnum<NPC> {

	public ElementCitizensNPC(Element parent, String id, Need need, Text editorDescription) {
		super(NPC.class, false /* ignored anyway since overriden here */, parent, id, need, editorDescription);
	}

	@Override
	public List<NPC> getValues() {
		return CollectionUtils.asList(CitizensAPI.getNPCRegistry().sorted());
	}

	// editor
	@Override
	public Mat editorIconType() {
		return CommonMats.PLAYER_HEAD;
	}

	@Override
	public List<String> editorIconLore() {
		List<String> lore = super.editorIconLore();
		lore.addAll(TextEditorGeneric.controlImport.parseLines());
		return lore;
	}

	@Override
	public void onEditorClick(ClickCall call) {
		// right-click : select
		if (call.getType().equals(ClickType.RIGHT)) {
			EnumSelectorGUI.openSelector(call.getClicker(), false, getSerializer(), () -> getValues(), editorIconType(), value -> value.getId() + " (" + value.getName() + ")",
					// on select
					value -> {
						setValue(CollectionUtils.asList(getSerializer().serialize(value)));
						call.getGUI().setRegularItem(buildEditorItem(call.getSlot()));
						call.getGUI().openFor(call.getClicker(), call.getPageIndex());
						getSuperElement().onEditorChange(this);
					},
					// on cancel
					() -> {
						call.getGUI().openFor(call.getClicker(), call.getPageIndex());
					});
		}
		// shift + right-click : import
		else if (call.getType().equals(ClickType.SHIFT_RIGHT)) {
			IntegrationInstanceCitizens.awaitNPC(call.getClicker(), TextEditorGeneric.messageElementBasicImportNPC, value -> {
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
