package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementLocationList extends ElementValueList<Location> {

	public ElementLocationList(Element parent, String id, Need need, Text editorDescription) {
		super(Location.class, parent, id, need, editorDescription);
	}

	// ----- editor
	@Override
	public Mat editorIconType() {
		return CommonMats.RAIL;
	}

	@Override
	protected List<String> editorLineIconLore(int lineIndex) {
		List<String> lore = super.editorLineIconLore(lineIndex);
		lore.add("§r");
		lore.addAll(TextEditorGeneric.controlImport.parseLines());
		return lore;
	}

	@Override
	public void onEditorOtherClickEdit(int lineIndex, ClickCall call) {
		// right-click : import location
		if (call.getType().equals(ClickType.RIGHT)) {
			WorkerGCore.inst().awaitLocation(call.getClicker(), TextEditorGeneric.messageElementBasicImportLocation, value -> {
				List<String> newValue = getRawValueCopy();
				if (newValue == null) {
					setValue(CollectionUtils.asList(getSerializer().serialize(value)));
				} else {
					if (lineIndex >= getRawValue().size()) {
						newValue.add(getSerializer().serialize(value));
					} else {
						newValue.set(lineIndex, getSerializer().serialize(value));
					}
					setValue(newValue);
				}
				// reopen GUI (that refreshes it since it's an editor GUI)
				call.reopenGUI();
				getSuperElement().onEditorChange(this);
			}, () -> call.reopenGUI());
		}
	}

	@Override
	protected String editorNewLine() {
		World world = CollectionUtils.random(Bukkit.getWorlds());
		double x = NumberUtils.random(-1000d, 1000d);
		double z = NumberUtils.random(-1000d, 1000d);
		return getSerializer().serialize(new Location(world, x, world.getHighestBlockYAt((int) x, (int) z) + 1d, z));
	}

}
