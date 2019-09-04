package com.guillaumevdn.gcore.lib.parseable.primitive;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.gui.GUI;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.ParseResult;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.PrimitiveParseable;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorItem;
import com.guillaumevdn.gcore.lib.parseable.editor.ModifCallback;
import com.guillaumevdn.gcore.lib.parseable.placeholder.PlaceholderParser;
import com.guillaumevdn.gcore.lib.util.StateSetting;
import com.guillaumevdn.gcore.lib.util.Utils;
import com.guillaumevdn.gcore.lib.util.input.ChatInput;

public class PPStateSetting extends PrimitiveParseable<StateSetting> {

	// base
	public PPStateSetting(String id, Parseable parent, String defaultValue, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, defaultValue == null ? null : Utils.asList(defaultValue), "cooldown", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// parse
	@Override
	public ParseResult<StateSetting> parseValue(List<String> value, Player parsing) throws Throwable {
		if (value.isEmpty()) {
			return new ParseResult<StateSetting>(null);
		}
		List<String> split = Utils.split(" ", value.get(0), false);
		StateSetting.Type type = Utils.valueOfOrNull(StateSetting.Type.class, split.get(0));
		return type == null ? null : new ParseResult<StateSetting>(new StateSetting(type, split.size() == 2 ? split.get(1) : null));
	}

	// editor
	@Override
	protected void fillEditor(final EditorGUI gui, Player player, final ModifCallback onModif) {
		// current, raw and delete
		EditorGUI.fillItemCurrent(gui, player, this, 0, onModif);
		EditorGUI.fillItemRaw(gui, player, this, 4, getValue() == null || getValue().isEmpty() ? null : getValue().get(0), onModif);
		EditorGUI.fillItemDelete(gui, player, this, 6, onModif);
		// select
		gui.setRegularItem(new EditorItem("control_item_select", 2, Mat.ENDER_CHEST, GLocale.GUI_GENERIC_EDITORENUMTYPESELECT.getLine(), GLocale.GUI_GENERIC_EDITORENUMSELECTLORE.getLines()) {
			@Override
			protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
				// selection gui
				EditorGUI sub = new EditorGUI(getLastData().getPlugin(), gui, Utils.getNewInventoryName(gui.getName(), "Select"), 54, GUI.SLOTS_0_TO_44) {
					@Override
					protected void fill() {
						// add values
						for (final StateSetting.Type val : StateSetting.Type.values()) {
							final String valName = val.name();
							setRegularItem(new EditorItem("value_" + valName, -1, Mat.ENDER_CHEST, "§6" + valName, null) {
								@Override
								protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
									// replace value
									if (getValue() != null) {
										String curr = !getValue().isEmpty() ? getValue().get(0) : null;
										String setting = curr != null && curr.contains(" ") ? curr.split(" ")[1] : null;
										getValue().set(0, valName + (setting != null ? " " + setting : ""));
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
		// setting
		gui.setRegularItem(new EditorItem("control_item_setting", 3, Mat.COMMAND_BLOCK, GLocale.GUI_GENERIC_EDITOR_GENERIC_SETTINGLORE.getLine(), GLocale.GUI_GENERIC_EDITORRAWLORE.getLines("{placeholders}", PlaceholderParser.describeAll())) {
			@Override
			protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
				// chat
				player.closeInventory();
				GLocale.MSG_GENERIC_CHATINPUT.send(player);
				GCore.inst().getChatInputs().put(player, new ChatInput() {
					@Override
					public void onChat(Player player, String value) {
						// replace value
						if (getValue() != null) {
							String curr = !getValue().isEmpty() ? getValue().get(0) : null;
							if (curr != null && curr.contains(" ")) {
								getValue().set(0, curr.split(" ")[0] + " " + value);
							}
						}
						onModif.callback(gui, player);
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
	public List<Integer> getEditorRegularSlots() {
		return GUI.SLOTS_0_TO_7;
	}

	@Override
	public int getEditorBackSlot() {
		return 8;
	}

	// clone
	protected PPStateSetting() {
		super();
	}

	@Override
	public PPStateSetting clone() {
		return (PPStateSetting) super.clone();
	}

}
