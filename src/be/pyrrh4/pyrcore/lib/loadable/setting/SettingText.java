package be.pyrrh4.pyrcore.lib.loadable.setting;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.loadable.AbstractListSetting;
import be.pyrrh4.pyrcore.lib.loadable.editor.EditorCallback;
import be.pyrrh4.pyrcore.lib.loadable.editor.EditorGUI;
import be.pyrrh4.pyrcore.lib.loadable.editor.EditorItem;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.messenger.Text;
import be.pyrrh4.pyrcore.lib.util.Utils;
import be.pyrrh4.pyrcore.lib.util.input.ChatInput;

public class SettingText extends AbstractListSetting<Text> {

	// base
	public SettingText(String id, List<String> def, boolean mandatory, List<String> description) {
		super(id, def, mandatory, "text", description);
	}

	// get
	@Override
	public Text parse(List<String> raw) {
		return new Text("en_US", raw);
	}

	@Override
	public void initializeEditorItem(final EditorGUI parent, final int slot, final EditorCallback onModif) {
		// sub
		parent.setRegularItem(new EditorItem(getId(), slot, Mat.BOOK, getId(), fillEditorItemLore()) {
			@Override
			public void onClick(Player player, ClickType clickType, int pageIndex) {
				// init
				EditorGUI sub = new EditorGUI(parent, getId()) {
					@Override
					protected void fill() {
						// set line icons
						int slot = -1;
						if (getValue() != null) {
							for (String line : getValue()) {
								final int index = ++slot;
								setRegularItem(new EditorItem("line_" + index, index, Mat.PAPER, "§6" + (index + 1), PCLocale.GUI_GENERIC_EDITORTEXTLINELORE.getLines("{value_mandatory}", isMandatory() ? "§cyes" : "§ano", "{value_type}", "text line", "{value_current}", line)) {
									@Override
									protected void onClick(Player player, ClickType clickType, int pageIndex) {
										// edit
										if (clickType.isLeftClick()) {
											player.closeInventory();
											PCLocale.MSG_GENERIC_CHATINPUT.send(player);
											PyrCore.inst().getChatInputs().put(player, new ChatInput() {
												@Override
												public void onChat(Player player, String value) {
													if (!value.replace(" ", "").equalsIgnoreCase("cancel")) {
														getValue().set(index, value);
														onModif.callback();
													}
													open(player);
												}
											});
										}
										// delete
										else if (clickType.isRightClick()) {
											getValue().remove(index);
											onModif.callback();
											open(player);
										}
									}
								});
							}
						}
						// new line
						setPersistentItem(new EditorItem("add", 49, Mat.BLAZE_ROD, PCLocale.GUI_GENERIC_EDITORITEMADD.getLine(), null) {
							@Override
							protected void onClick(Player player, ClickType clickType, int pageIndex) {
								if (getValue() == null) {
									setValue(Utils.asList("new line"));
								} else {
									getValue().add("new line");
								}
								onModif.callback();
								open(player);
							}
						});
						// delete
						setPersistentItem(new EditorItem("delete", 46, Mat.TNT_MINECART, PCLocale.GUI_GENERIC_EDITORITEMDELETESELF.getLine(), PCLocale.GUI_GENERIC_EDITORITEMDELETESELFLORE.getLines()) {
							@Override
							protected void onClick(Player player, ClickType clickType, int pageIndex) {
								if (getValue() != null) {
									getValue().clear();
								}
								onModif.callback();
								open(player);
							}
						});
						// back
						setPersistentItem(new EditorItem("back", 52, Mat.ARROW, PCLocale.GUI_GENERIC_EDITORITEMBACK.getLine(), null) {
							@Override
							protected void onClick(Player player, ClickType clickType, int pageIndex) {
								parent.open(player);
							}
						});
					}
				};
				// open sub
				sub.open(player);
			}
		});
	}

}
