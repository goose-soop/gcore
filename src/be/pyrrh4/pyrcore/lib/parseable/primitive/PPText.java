package be.pyrrh4.pyrcore.lib.parseable.primitive;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.messenger.Text;
import be.pyrrh4.pyrcore.lib.parseable.Parseable;
import be.pyrrh4.pyrcore.lib.parseable.PrimitiveParseable;
import be.pyrrh4.pyrcore.lib.parseable.editor.EditorGUI;
import be.pyrrh4.pyrcore.lib.parseable.editor.EditorItem;
import be.pyrrh4.pyrcore.lib.parseable.editor.ModifCallback;
import be.pyrrh4.pyrcore.lib.util.Utils;
import be.pyrrh4.pyrcore.lib.util.input.ChatInput;

public class PPText extends PrimitiveParseable<Text> {

	// base
	public PPText(String id, Parseable parent, List<String> defaultValue, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, defaultValue, "text", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// parse
	@Override
	public Text parseValue(List<String> value, Player parsing) throws Throwable {
		return new Text("en_US", value);
	}

	// editor
	@Override
	public void fillEditor(final EditorGUI gui, Player player, final ModifCallback onModif) {
		// current and delete
		EditorGUI.fillItemCurrent(gui, player, this, 20, onModif);
		EditorGUI.fillItemDelete(gui, player, this, 24, onModif);
		// set line icons
		if (getValue() != null) {
			for (int i = 0; i < getValue().size(); ++i) {
				final int index = i;
				gui.setRegularItem(new EditorItem("line_" + index, -1, Mat.PAPER, "§6" + (index + 1), PCLocale.GUI_GENERIC_EDITORTEXTLINELORE.getLines()) {
					@Override
					protected void onClick(Player player, ClickType clickType, int pageIndex) {
						// edit
						if (clickType.isLeftClick()) {
							player.closeInventory();
							PCLocale.MSG_GENERIC_CHATINPUT.send(player);
							PyrCore.inst().getChatInputs().put(player, new ChatInput() {
								@Override
								public void onChat(Player player, String value) {
									// replace value
									if (!value.replace(" ", "").equalsIgnoreCase("cancel")) {
										getValue().set(index, value);
										onModif.callback(gui, player);
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
							onModif.callback(gui, player);
							// re-fill and open
							gui.open(player);
						}
					}
				});
			}
		}
		// new line
		gui.setPersistentItem(new EditorItem("control_item_add", 22, Mat.BLAZE_ROD, PCLocale.GUI_GENERIC_EDITORITEMADD.getLine(), null) {
			@Override
			protected void onClick(Player player, ClickType clickType, int pageIndex) {
				// add value
				if (getValue() == null) {
					setValue(Utils.asList("new line"));
				} else {
					getValue().add("new line");
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
	protected PPText() {
		super();
	}

	@Override
	public PPText clone() {
		return (PPText) super.clone();
	}

}
