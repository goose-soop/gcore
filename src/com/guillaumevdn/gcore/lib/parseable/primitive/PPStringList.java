package com.guillaumevdn.gcore.lib.parseable.primitive;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.gui.GUI;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.messenger.JsonMessage;
import com.guillaumevdn.gcore.lib.parseable.ParseResult;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.PrimitiveParseable;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorItem;
import com.guillaumevdn.gcore.lib.parseable.editor.ModifCallback;
import com.guillaumevdn.gcore.lib.parseable.placeholder.PlaceholderParser;
import com.guillaumevdn.gcore.lib.util.Utils;
import com.guillaumevdn.gcore.lib.util.input.ChatInput;

public class PPStringList extends PrimitiveParseable<List<String>> {

	// base
	public PPStringList(String id, Parseable parent, List<String> defaultValue, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, defaultValue, "list of strings", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// parse
	@Override
	public ParseResult<List<String>> parseValue(List<String> value, Player parsing) throws Throwable {
		return new ParseResult<List<String>>(value);
	}

	// editor
	@Override
	protected void fillEditor(final EditorGUI gui, Player player, final ModifCallback onModif) {
		// current and delete
		EditorGUI.fillItemCurrent(gui, player, 20, this, onModif);
		EditorGUI.fillItemDelete(gui, player, 24, this, onModif);
		// set line icons
		if (getValue() != null) {
			for (int i = 0; i < getValue().size(); ++i) {
				final int index = i;
				gui.setRegularItem(new EditorItem("line_" + index, -1, Mat.PAPER, "§6" + (index + 1), GLocale.GUI_GENERIC_EDITORTEXTLINELORE.getLines("{current}", getValue().get(index), "{placeholders}", PlaceholderParser.describeAll())) {
					@Override
					protected void onClick(Player player, ClickType clickType, int pageIndex) {
						// edit
						if (clickType.isLeftClick()) {
							player.closeInventory();
							JsonMessage json = new JsonMessage();
							for (String str : GLocale.MSG_GENERIC_CHATINPUTMODIFY.getLines()) {
								json.append(str).setClickAsSuggestCmd(getValue().get(index).replace("§", "&")).save();
							}
							json.send(player);
							GCore.inst().getChatInputs().put(player, new ChatInput() {
								@Override
								public void onChat(Player player, String value) {
									// replace value
									if (!value.replace(" ", "").equalsIgnoreCase("cancel")) {
										getValue().set(index, value);
										onModif.callback(null, gui, player);
									}
									// re-fill and open
									gui.open(player);
								}
							});
						}
						// delete
						else if (clickType.isRightClick()) {
							// delete value
							getValue().remove(index);
							onModif.callback(null, gui, player);
							// re-fill and open
							gui.open(player);
						}
					}
				});
			}
		}
		// new line
		gui.setPersistentItem(new EditorItem("control_item_add", 22, Mat.BLAZE_ROD, GLocale.GUI_GENERIC_EDITORITEMADD.getLine(), null) {
			@Override
			protected void onClick(Player player, ClickType clickType, int pageIndex) {
				// add value
				if (getValue() == null) {
					setValue(Utils.asList("new line"));
				} else {
					getValue().add("new line");
				}
				onModif.callback(null, gui, player);
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
	protected PPStringList() {
		super();
	}

	@Override
	public PPStringList clone() {
		return (PPStringList) super.clone();
	}

}
