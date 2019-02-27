package be.pyrrh4.pyrcore.lib.loadable.setting;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.loadable.AbstractUniqueSetting;
import be.pyrrh4.pyrcore.lib.loadable.editor.EditorCallback;
import be.pyrrh4.pyrcore.lib.loadable.editor.EditorGUI;
import be.pyrrh4.pyrcore.lib.loadable.editor.EditorItem;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.parseable.placeholder.PlaceholderParser;
import be.pyrrh4.pyrcore.lib.util.input.ChatInput;

public class SettingWorld extends AbstractUniqueSetting<World> {

	// base
	public SettingWorld(String id, String def, boolean mandatory, List<String> description) {
		super(id, def, mandatory, "world", description);
	}

	// get
	@Override
	public World parse(String raw) {
		return Bukkit.getWorld(raw);
	}

	@Override
	public void initializeEditorItem(final EditorGUI parent, final int slot, final EditorCallback onModif) {
		// sub
		parent.setRegularItem(new EditorItem(getId(), slot, Mat.ENDER_CHEST, getId(), fillEditorItemLore()) {
			@Override
			public void onClick(Player player, ClickType clickType, int pageIndex) {
				// init
				EditorGUI sub = new EditorGUI(parent, getId(), 9, 8) {
					private final EditorGUI subThis = this;
					@Override
					protected void fill() {
						// select
						setRegularItem(new EditorItem("select", 2, Mat.ENDER_CHEST, PCLocale.GUI_GENERIC_EDITORENUMSELECT.getLine(), fillEditorItemLore(PCLocale.GUI_GENERIC_EDITORENUMSELECTLORE.getLines())) {
							@Override
							protected void onClick(Player player, ClickType clickType, int pageIndex) {
								// init
								EditorGUI subEnum = new EditorGUI(subThis, "Select") {
									private EditorGUI subEnumThis = this;
									@Override
									protected void fill() {
										// values
										int valueSlot = -1;
										for (final World value : Bukkit.getWorlds()) {
											subEnumThis.setRegularItem(new EditorItem("value_" + value.getName(), ++valueSlot, Mat.ENDER_CHEST, value.getName(), null) {
												@Override
												protected void onClick(Player player, ClickType clickType, int pageIndex) {
													setValue(value.getName());
													onModif.callback();
													subThis.open(player);
												}
											});
										}
										// back
										subEnumThis.setPersistentItem(new EditorItem("back", 52, Mat.ARROW, PCLocale.GUI_GENERIC_EDITORITEMBACK.getLine(), null) {
											@Override
											protected void onClick(Player player, ClickType clickType, int pageIndex) {
												subThis.open(player);
											}
										});
									}
								};
								// open sub
								subEnum.open(player);
							}
						});
						// raw
						setRegularItem(new EditorItem("raw", 3, Mat.COMMAND_BLOCK, PCLocale.GUI_GENERIC_EDITORRAW.getLine(), fillEditorItemLore(PCLocale.GUI_GENERIC_EDITORRAWLORE.getLines("{placeholders}", PlaceholderParser.describeAll()))) {
							@Override
							protected void onClick(Player player, ClickType clickType, int pageIndex) {
								// chat
								player.closeInventory();
								PCLocale.MSG_GENERIC_CHATINPUT.send(player);
								PyrCore.inst().getChatInputs().put(player, new ChatInput() {
									@Override
									public void onChat(Player player, String value) {
										if (!value.replace(" ", "").equalsIgnoreCase("cancel")) {
											setValue(value);
											onModif.callback();
										}
										subThis.open(player);
									}
								});
							}
						});
						// delete
						setPersistentItem(new EditorItem("delete", 6, Mat.TNT_MINECART, PCLocale.GUI_GENERIC_EDITORITEMDELETESELF.getLine(), PCLocale.GUI_GENERIC_EDITORITEMDELETESELFLORE.getLines()) {
							@Override
							protected void onClick(Player player, ClickType clickType, int pageIndex) {
								setValue(null);
								onModif.callback();
								open(player);
							}
						});
						// back
						setPersistentItem(new EditorItem("back", 8, Mat.ARROW, PCLocale.GUI_GENERIC_EDITORITEMBACK.getLine(), null) {
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
