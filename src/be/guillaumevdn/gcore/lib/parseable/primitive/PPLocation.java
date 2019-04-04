package be.guillaumevdn.gcore.lib.parseable.primitive;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import be.guillaumevdn.gcore.GLocale;
import be.guillaumevdn.gcore.GCore;
import be.guillaumevdn.gcore.lib.material.Mat;
import be.guillaumevdn.gcore.lib.parseable.Parseable;
import be.guillaumevdn.gcore.lib.parseable.PrimitiveParseable;
import be.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import be.guillaumevdn.gcore.lib.parseable.editor.EditorItem;
import be.guillaumevdn.gcore.lib.parseable.editor.ModifCallback;
import be.guillaumevdn.gcore.lib.util.Utils;
import be.guillaumevdn.gcore.lib.util.input.LocationInput;

public class PPLocation extends PrimitiveParseable<Location> {

	// base
	public PPLocation(String id, Parseable parent, String defaultValue, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, Utils.asList(defaultValue), "location", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// parse
	@Override
	public Location parseValue(List<String> value, Player parsing) throws Throwable {
		return !value.isEmpty() ? Utils.unserializeWXYZLocation(value.get(0)) : null;
	}

	// editor
	@Override
	protected void fillEditor(final EditorGUI gui, Player player, final ModifCallback onModif) {
		// current, raw and delete
		EditorGUI.fillItemCurrent(gui, player, this, 0, onModif);
		EditorGUI.fillItemRaw(gui, player, this, 3, onModif);
		EditorGUI.fillItemDelete(gui, player, this, 6, onModif);
		// select
		gui.setRegularItem(new EditorItem("control_item_import", 2, Mat.ENDER_CHEST, GLocale.GUI_GENERIC_EDITORLOCATIONIMPORT.getLine(), GLocale.GUI_GENERIC_EDITORLOCATIONIMPORTLORE.getLines()) {
			@Override
			protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
				// location
				player.closeInventory();
				GLocale.MSG_GENERIC_LOCATIONINPUT.send(player);
				GCore.inst().getLocationInputs().put(player, new LocationInput() {
					@Override
					public void onChoose(Player player, Location value) {
						// replace value
						if (value != null) {
							if (getValue() != null) {
								getValue().set(0, Utils.serializeWXYZLocation(value));
							} else {
								setValue(Utils.asList(Utils.serializeWXYZLocation(value)));
							}
							onModif.callback(gui, player);
						}
						// re-fill and open
						gui.open(player);
					}
				});
			}
		});
	}

	@Override
	public int getEditorSize() {
		return 9;
	}

	@Override
	public int getEditorMaxRegularSlot() {
		return 7;
	}

	@Override
	public int getEditorBackSlot() {
		return 8;
	}

	// clone
	protected PPLocation() {
		super();
	}

	@Override
	public PPLocation clone() {
		return (PPLocation) super.clone();
	}

}
