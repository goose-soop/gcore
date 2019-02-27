package be.pyrrh4.pyrcore.lib.loadable.setting;

import java.util.List;

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

public class SettingInteger extends AbstractUniqueSetting<Integer> {

	// base
	public SettingInteger(String id, String def, boolean mandatory, List<String> description) {
		super(id, def, mandatory, "number", description);
	}

	// methods
	@Override
	public Integer parse(String raw) {
		return new Integer((int) Math.floor(Utils.calculateExpression(raw)));
	}

	public static Integer getParsed(String raw) {
		try {
			return new Integer((int) Math.floor(Utils.calculateExpression(raw)));
		} catch (Throwable ignored) {
			return null;
		}
	}

	@Override
	public void initializeEditorItem(final EditorGUI parent, final int slot, final EditorCallback onModif) {
		// raw
		List<String> desc = Utils.asListMultiple(getDescription(), "", PCLocale.GUI_GENERIC_EDITORRAWLORE.getLines("{placeholders}", PlaceholderParser.describeAll()));
		parent.setRegularItem(new EditorItem(getId(), slot, Mat.GLOWSTONE_DUST, getId(), fillEditorItemLore(desc)) {
			@Override
			protected void onClick(Player player, ClickType clickType, int pageIndex) {
				// chat
				player.closeInventory();
				PCLocale.MSG_GENERIC_CHATINPUT.send(player);
				PyrCore.inst().getChatInputs().put(player, new ChatInput() {
					@Override
					public void onChat(Player player, String value) {
						// TODO : verify that value contains either a valid integer, either a PAPI variable, either a QC variable
						if (!value.replace(" ", "").equalsIgnoreCase("cancel")) {
							setValue(value);
							onModif.callback();
						}
						parent.open(player);
					}
				});
			}
		});
	}

}
