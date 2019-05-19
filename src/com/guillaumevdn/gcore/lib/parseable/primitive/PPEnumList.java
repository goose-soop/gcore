package com.guillaumevdn.gcore.lib.parseable.primitive;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.gui.GUI;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.ParseResult;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.PrimitiveParseable;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorItem;
import com.guillaumevdn.gcore.lib.parseable.editor.ModifCallback;
import com.guillaumevdn.gcore.lib.util.Utils;

public class PPEnumList<T extends Enum<T>> extends PrimitiveParseable<List<T>> {

	// base
	private Class<T> enumClass;

	public PPEnumList(String id, Parseable parent, List<String> defaultValue, Class<T> enumClass, String typeName, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, defaultValue, typeName, mandatory, editorSlot, editorIcon, editorDescription);
		this.enumClass = enumClass;
	}

	// get
	public Class<T> getEnumClass() {
		return enumClass;
	}

	// parse
	@Override
	public ParseResult<List<T>> parseValue(List<String> value, Player parsing) throws Throwable {
		if (value.isEmpty()) {
			return new ParseResult<List<T>>(new ArrayList<T>());
		}
		List<T> result = new ArrayList<T>();
		for (String val : value) {
			T parsed = Utils.valueOfOrNull(enumClass, val);
			if (parsed != null) {
				result.add(parsed);
			} else {
				getLastData().log("couldn't parse element " + val + " for " + parsing.getName());
				return null;
			}
		}
		return new ParseResult<List<T>>(result);
	}

	// editor
	@Override
	protected void fillEditor(final EditorGUI gui, Player player, final ModifCallback onModif) {
		// current and delete
		EditorGUI.fillItemCurrent(gui, player, this, 20, onModif);
		EditorGUI.fillItemDelete(gui, player, this, 24, onModif);
		// set line icons
		if (getValue() != null) {
			for (int i = 0; i < getValue().size(); ++i) {
				final String line = getValue().get(i);
				final int index = i;
				gui.setRegularItem(new EditorItem("line_" + index, -1, Mat.PAPER, "§6" + (index + 1), GLocale.GUI_GENERIC_EDITORLISTELEMENTLORE.getLines("{current}", line)) {
					@Override
					protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
						// edit
						if (clickType.isLeftClick()) {
							// create sub GUI
							String name = Utils.getNewInventoryName(gui.getName(), "" + (index + 1));
							EditorGUI sub = new EditorGUI(getLastData().getPlugin(), gui, name, 9, GUI.SLOTS_0_TO_7) {
								private EditorGUI subThis = this;
								@Override
								protected void fill() {
									// current, raw and delete
									EditorGUI.fillItemCurrent(subThis, player, "" + (index + 1), Utils.asList(line), null, getTypeName(), isMandatory(), getEditorIcon(), 0, onModif);
									EditorGUI.fillItemRaw(subThis, player, 3, onModif, new RawChangeCallback() {
										@Override
										public void callback(EditorGUI from, Player player, String value) {
											getValue().set(index, value);
										}
									});
									EditorGUI.fillItemDelete(subThis, player, 6, onModif, new ModifCallback() {
										@Override
										public void callback(EditorGUI from, Player player) {
											getValue().remove(index);
										}
									});
									// select
									setRegularItem(new EditorItem("control_item_select", 2, Mat.ENDER_CHEST, GLocale.GUI_GENERIC_EDITORENUMSELECT.getLine(), GLocale.GUI_GENERIC_EDITORENUMSELECTLORE.getLines()) {
										@Override
										protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
											// selection gui
											EditorGUI subSelection = new EditorGUI(getLastData().getPlugin(), gui, Utils.getNewInventoryName(gui.getName(), "Select"), 54, GUI.SLOTS_0_TO_44) {
												@Override
												protected void fill() {
													// add values
													for (final T val : enumClass.getEnumConstants()) {
														final String valName = val.name();
														setRegularItem(new EditorItem("value_" + valName, -1, Mat.ENDER_CHEST, "§6" + valName, null) {
															@Override
															protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
																// replace value
																getValue().set(index, valName);
																onModif.callback(gui, player);
																// re-fill and open
																subThis.open(player);
															}
														});
													}
													// back item
													setPersistentItem(new EditorItem("control_item_back", 52, Mat.ARROW, GLocale.GUI_GENERIC_EDITORITEMBACK.getLine(), null) {
														@Override
														protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
															gui.open(player);
														}
													});
												}
											};
											// open it
											subSelection.open(player);
										}
									});
									// back item
									setPersistentItem(new EditorItem("control_item_back", 8, Mat.ARROW, GLocale.GUI_GENERIC_EDITORITEMBACK.getLine(), null) {
										@Override
										protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
											gui.open(player);
										}
									});
								}
							};
							// open it
							sub.open(player);
						}
						// delete
						else if (clickType.isRightClick()) {
							// delete value
							getValue().remove(index);
							onModif.callback(gui, player);
							// re-fill and open
							gui.open(player);
						}
					}
				});
			}
		}
		// new value
		gui.setPersistentItem(new EditorItem("control_item_add", 22, Mat.BLAZE_ROD, GLocale.GUI_GENERIC_EDITORITEMADD.getLine(), null) {
			@Override
			protected void onClick(Player player, ClickType clickType, int pageIndex) {
				// add value
				T newValue = enumClass.getEnumConstants().length == 0 ? null : enumClass.getEnumConstants()[0];
				if (getValue() == null) {
					setValue(Utils.asList(newValue == null ? "NO_VALUE" : newValue.name()));
				} else {
					getValue().add(newValue == null ? "NO_VALUE" : newValue.name());
				}
				onModif.callback(gui, player);
				// re-fill and open
				gui.open(player);
			}
		});
	}

	@Override
	public int getEditorSize() {
		return 27;
	}

	@Override
	public List<Integer> getEditorRegularSlots() {
		return GUI.SLOTS_0_TO_17;
	}

	@Override
	public int getEditorBackSlot() {
		return 25;
	}

	// clone
	protected PPEnumList() {
		super();
	}

	@Override
	public PPEnumList<T> clone() {
		// clone
		PPEnumList<T> clone = (PPEnumList<T>) super.clone();
		// clone properties
		clone.enumClass = enumClass;
		// success
		return clone;
	}

}
