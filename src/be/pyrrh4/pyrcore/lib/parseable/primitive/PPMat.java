package be.pyrrh4.pyrcore.lib.parseable.primitive;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.parseable.editor.EditorItem;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.parseable.Parseable;
import be.pyrrh4.pyrcore.lib.parseable.PrimitiveParseable;
import be.pyrrh4.pyrcore.lib.parseable.editor.EditorGUI;
import be.pyrrh4.pyrcore.lib.parseable.editor.ModifCallback;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class PPMat extends PrimitiveParseable<Mat> {

	// base
	public PPMat(String id, Parseable parent, String defaultValue, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, Utils.asList(defaultValue), "PyrCore material", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// parse
	@Override
	public Mat parseValue(List<String> value, Player parsing) throws Throwable {
		return !value.isEmpty() ? Mat.from(value.get(0), 0) : null;
	}

	// editor
	@Override
	protected void fillEditor(final EditorGUI gui, Player player, final ModifCallback onModif) {
		// current, raw and delete
		EditorGUI.fillItemCurrent(gui, player, this, 0, onModif);
		EditorGUI.fillItemRaw(gui, player, this, 3, onModif);
		EditorGUI.fillItemDelete(gui, player, this, 6, onModif);
		// select
		gui.setRegularItem(new EditorItem("control_item_select", 2, Mat.ENDER_CHEST, PCLocale.GUI_GENERIC_EDITORENUMSELECT.getLine(), PCLocale.GUI_GENERIC_EDITORENUMSELECTLORE.getLines()) {
			@Override
			protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
				// selection gui
				EditorGUI sub = new EditorGUI(getLastData().getPlugin(), gui, Utils.getNewInventoryName(gui.getName(), "Select"), 54, 44) {
					@Override
					protected void fill() {
						// add values
						for (final Mat val : Mat.values()) {
							if (!val.exists()) continue;
							final String valName = val.getModernName();
							setRegularItem(new EditorItem("value_" + valName, -1, val, "§6" + valName, null) {
								@Override
								protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
									// replace value
									if (getValue() != null) {
										getValue().set(0, valName);
									} else {
										setValue(Utils.asList(valName));
									}
									onModif.callback(gui, player);
									// re-fill and open
									gui.open(player);
								}
							});
						}
					}
				};
				// open it
				sub.open(player);
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
	protected PPMat() {
		super();
	}

	@Override
	public PPMat clone() {
		return (PPMat) super.clone();
	}

}
