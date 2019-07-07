package com.guillaumevdn.gcore.lib.npc.behavior.action.type;

import java.util.List;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.npc.Npc;
import com.guillaumevdn.gcore.lib.npc.behavior.action.BAction;
import com.guillaumevdn.gcore.lib.npc.behavior.action.BActionType;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPStringList;
import com.guillaumevdn.gcore.lib.util.Utils;

public class ActionPlayerSendMessage extends BAction {

	// base
	private PPStringList messages = addComponent(new PPStringList("messages", this, Utils.emptyList(), true, 0, EditorGUI.ICON_STRING_LIST, GLocale.GUI_GCORE_EDITOR_NPC_BEHAVIORMESSAGESLORE.getLines()));

	public ActionPlayerSendMessage(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, BActionType.PLAYER_SEND_MESSAGE, parent, mandatory, editorSlot, editorIcon, editorDescription);
	}

	// get
	public PPStringList getMessages() {
		return messages;
	}

	public List<String> getMessages(Player parser) {
		return messages.getParsedValue(parser);
	}

	// methods
	@Override
	public void run(Player player, Npc npc) {
		// get settings
		List<String> messages = getMessages(player);
		if (messages == null) {
			throw new IllegalArgumentException("invalid parameters ; messages = " + messages + " ; for npc behavior action " + getId() + " (player " + player.getName() + ", npc " + npc.getId() + ")");
		}
		// send a random message in the list
		player.sendMessage(Utils.random(messages));
	}

}
