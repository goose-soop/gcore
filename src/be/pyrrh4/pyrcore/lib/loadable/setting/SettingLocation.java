package be.pyrrh4.pyrcore.lib.loadable.setting;

import java.util.List;

import org.bukkit.Location;
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
import be.pyrrh4.pyrcore.lib.util.Utils;
import be.pyrrh4.pyrcore.lib.util.input.ChatInput;
import be.pyrrh4.pyrcore.lib.util.input.LocationInput;

public class SettingLocation extends AbstractUniqueSetting<Location> {

	// base
	public SettingLocation(String id, String def, boolean mandatory, List<String> description) {
		super(id, def, mandatory, "location", description);
	}

	// methods
	@Override
	public Location parse(String raw) {
		return Utils.unserializeWXYZLocation(raw);
	}

	@Override
	public void initializeEditorItem(final EditorGUI parent, final int slot, final EditorCallback onModif) {
		// sub
		parent.setRegularItem(new EditorItem(getId(), slot, Mat.MINECART, getId(), fillEditorItemLore()) {
			@Override
			public void onClick(Player player, ClickType clickType, int pageIndex) {
				// init
				EditorGUI sub = new EditorGUI(parent, getId(), 9, 8) {
					@Override
					protected void fill() {
						// import
						setRegularItem(new EditorItem("import", 2, Mat.MINECART, PCLocale.GUI_GENERIC_EDITORLOCATIONIMPORT.getLine(), fillEditorItemLore(PCLocale.GUI_GENERIC_EDITORLOCATIONIMPORTLORE.getLines())) {
							@Override
							protected void onClick(Player player, ClickType clickType, int pageIndex) {
								// location
								player.closeInventory();
								PCLocale.MSG_GENERIC_LOCATIONINPUT.send(player);
								PyrCore.inst().getLocationInputs().put(player, new LocationInput() {
									@Override
									public void onChoose(Player player, Location value) {
										if (value != null) {
											setValue(Utils.serializeWXYZLocation(value));
											onModif.callback();
										}
										open(player);
									}
								});
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
										// TODO : detect if it's a valid format ({text or placeholder for world} then 3x or 5x {decimal number or placeholder})
										if (!value.replace(" ", "").equalsIgnoreCase("cancel")) {
											setValue(value);
											onModif.callback();
										}
										open(player);
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
