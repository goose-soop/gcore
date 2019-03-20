package be.pyrrh4.pyrcore.lib.parseable.primitive;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.parseable.Parseable;
import be.pyrrh4.pyrcore.lib.parseable.PrimitiveParseable;
import be.pyrrh4.pyrcore.lib.parseable.editor.EditorGUI;
import be.pyrrh4.pyrcore.lib.parseable.editor.EditorItem;
import be.pyrrh4.pyrcore.lib.parseable.editor.ModifCallback;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class PPMatList extends PrimitiveParseable<List<Mat>> {

	// base
	public PPMatList(String id, Parseable parent, List<String> defaultValue, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, defaultValue, "list of PyrCore materials", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// parse
	@Override
	public List<Mat> parseValue(List<String> value, Player parsing) throws Throwable {
		if (!value.isEmpty()) {
			List<Mat> result = new ArrayList<Mat>();
			for (String val : value) {
				Mat parsed = Mat.from(val, 0);
				if (parsed != null) {
					result.add(parsed);
				} else {
					getLastData().log("invalid primitive setting of type " + getTypeName() + " (couldn't parse element " + val + " for " + parsing.getName() + ")");
				}
			}
		}
		return null;
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
				final int index = ++i;
				gui.setRegularItem(new EditorItem("line_" + index, -1, Mat.PAPER, "§6" + (index + 1), PCLocale.GUI_GENERIC_EDITORLISTELEMENTLORE.getLines()) {
					@Override
					protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
						// edit
						if (clickType.isLeftClick()) {
							// create sub GUI
							String name = Utils.getNewInventoryName(gui.getName(), "" + index);
							EditorGUI sub = new EditorGUI(getLastData().getPlugin(), gui, name, 9, 7) {
								private EditorGUI subThis = this;
								@Override
								protected void fill() {
									// current, raw and delete
									EditorGUI.fillItemCurrent(subThis, player, "" + index, Utils.asList(line), null, "PyrCore material", isMandatory(), getEditorIcon(), 0, onModif);
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
									setRegularItem(new EditorItem("control_item_select", 2, Mat.ENDER_CHEST, PCLocale.GUI_GENERIC_EDITORENUMSELECT.getLine(), PCLocale.GUI_GENERIC_EDITORENUMSELECTLORE.getLines()) {
										@Override
										protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
											// selection gui
											EditorGUI subSelection = new EditorGUI(getLastData().getPlugin(), gui, Utils.getNewInventoryName(gui.getName(), "Select"), 54, 44) {
												@Override
												protected void fill() {
													// add values
													for (final Mat val : Mat.values()) {
														final String valName = val.getModernName();
														setRegularItem(new EditorItem("value_" + valName, -1, val, "§6" + valName, null) {
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
													setPersistentItem(new EditorItem("control_item_back", 52, Mat.ARROW, PCLocale.GUI_GENERIC_EDITORITEMBACK.getLine(), null) {
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
									setPersistentItem(new EditorItem("control_item_back", 8, Mat.ARROW, PCLocale.GUI_GENERIC_EDITORITEMBACK.getLine(), null) {
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
		gui.setPersistentItem(new EditorItem("control_item_add", 22, Mat.BLAZE_ROD, PCLocale.GUI_GENERIC_EDITORITEMADD.getLine(), null) {
			@Override
			protected void onClick(Player player, ClickType clickType, int pageIndex) {
				// add value
				if (getValue() == null) {
					setValue(Utils.asList(Mat.GRASS_BLOCK.getModernName()));
				} else {
					getValue().add(Mat.GRASS_BLOCK.getModernName());
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
	public int getEditorMaxRegularSlot() {
		return 17;
	}

	@Override
	public int getEditorBackSlot() {
		return 25;
	}

	// clone
	protected PPMatList() {
		super();
	}

	@Override
	public PPMatList clone() {
		return (PPMatList) super.clone();
	}

}
