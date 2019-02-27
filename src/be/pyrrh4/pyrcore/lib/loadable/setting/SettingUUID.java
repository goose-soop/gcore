package be.pyrrh4.pyrcore.lib.loadable.setting;

import java.util.List;
import java.util.UUID;

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

public class SettingUUID extends AbstractUniqueSetting<UUID> {

	// base
	public SettingUUID(String id, String def, boolean mandatory, List<String> description) {
		super(id, def, mandatory, "unique id", description);
	}

	// get
	@Override
	public UUID parse(String raw) {
		return UUID.fromString(raw);
	}

	@Override
	public void initializeEditorItem(final EditorGUI parent, final int slot, final EditorCallback onModif) {
		// raw
		List<String> desc = Utils.asListMultiple(getDescription(), "", PCLocale.GUI_GENERIC_EDITORRAWLORE.getLines("{placeholders}", PlaceholderParser.describeAll()));
		parent.setRegularItem(new EditorItem(getId(), slot, Mat.PAPER, getId(), fillEditorItemLore(desc)) {
			@Override
			protected void onClick(Player player, ClickType clickType, int pageIndex) {
				// chat
				player.closeInventory();
				PCLocale.MSG_GENERIC_CHATINPUT.send(player);
				PyrCore.inst().getChatInputs().put(player, new ChatInput() {
					@Override
					public void onChat(Player player, String value) {
						if (!value.replace(" ", "").equalsIgnoreCase("cancel")) {
							// TODO : detect if it's a variable or a correct UUID
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
