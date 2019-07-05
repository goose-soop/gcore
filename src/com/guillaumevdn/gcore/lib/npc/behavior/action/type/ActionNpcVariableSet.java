package com.guillaumevdn.gcore.lib.npc.behavior.action.type;

import java.util.List;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.data.GUser;
import com.guillaumevdn.gcore.data.GUserNpcData;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.npc.Npc;
import com.guillaumevdn.gcore.lib.npc.behavior.action.BAction;
import com.guillaumevdn.gcore.lib.npc.behavior.action.BActionType;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPString;

public class ActionNpcVariableSet extends BAction {

	// base
	private PPString variable = addComponent(new PPString("variable", this, "health", true, 0, EditorGUI.ICON_STRING, GLocale.GUI_GCORE_EDITOR_NPC_BEHAVIORVARIABLELORE.getLines()));
	private PPString value = addComponent(new PPString("value", this, "20.0", true, 1, EditorGUI.ICON_STRING, GLocale.GUI_GCORE_EDITOR_NPC_BEHAVIORVARIABLEVALUELORE.getLines()));

	public ActionNpcVariableSet(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, BActionType.NPC_VARIABLE_SET, parent, mandatory, editorSlot, editorIcon, editorDescription);
	}

	// get
	public PPString getVariable() {
		return variable;
	}

	public String getVariable(Player parser) {
		return variable.getParsedValue(parser);
	}

	public PPString getValue() {
		return value;
	}

	public String getValue(Player parser) {
		return value.getParsedValue(parser);
	}

	// methods
	@Override
	public void run(Player player, Npc npc) {
		// get settings
		String variable = getVariable(player);
		String value = getValue(player);
		if (variable == null || value == null) {
			throw new IllegalArgumentException("invalid parameters ; variable = " + variable + ", value = " + value + " ; for npc behavior action " + getId() + " (player " + player.getName() + ", npc " + npc.getId() + ")");
		}
		// get user npc data
		GUser user = GUser.get(player);
		GUserNpcData userNpc = user.getUserNpcData(npc.getId());
		// change user npc data and mark as pushable
		userNpc.setVariableValue(variable, value);
		GCore.inst().getNpcManager().getBehaviorMustPushUsers().add(user);
	}

}
