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
import com.guillaumevdn.gcore.lib.parseable.primitive.PPDouble;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPString;
import com.guillaumevdn.gcore.lib.util.Utils;

public class ActionNpcVariableModifyNumber extends BAction {

	// base
	private PPString variable = addComponent(new PPString("variable", this, "health", true, 0, EditorGUI.ICON_STRING, GLocale.GUI_GCORE_EDITOR_NPC_BEHAVIORVARIABLELORE.getLines()));
	private PPDouble modifier = addComponent(new PPDouble("modifier", this, "1.0", -Double.MAX_VALUE, Double.MAX_VALUE, true, 1, EditorGUI.ICON_NUMBER, GLocale.GUI_GCORE_EDITOR_NPC_BEHAVIORVARIABLEMODIFIERLORE.getLines()));

	public ActionNpcVariableModifyNumber(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, BActionType.NPC_VARIABLE_MODIFY_NUMBER, parent, mandatory, editorSlot, editorIcon, editorDescription);
	}

	// get
	public PPString getVariable() {
		return variable;
	}

	public String getVariable(Player parser) {
		return variable.getParsedValue(parser);
	}

	public PPDouble getModifier() {
		return modifier;
	}

	public Double getModifier(Player parser) {
		return modifier.getParsedValue(parser);
	}

	// methods
	@Override
	public void run(Player player, Npc npc) {
		// get settings
		String variable = getVariable(player);
		Double modifier = getModifier(player);
		if (variable == null || modifier == null) {
			throw new IllegalArgumentException("invalid parameters ; variable = " + variable + ", modifier = " + modifier + " ; for npc behavior action " + getId() + " (player " + player.getName() + ", npc " + npc.getId() + ")");
		}
		// get user npc data
		GUser user = GUser.get(player);
		GUserNpcData userNpc = user.getUserNpcData(npc.getId());
		String value = userNpc.getVariableValue(variable);
		Double number = Utils.doubleOrNull(value);
		// not a number
		if (number == null) {
			throw new IllegalStateException("tried to modify value '" + value + "' of variable " + variable + " but it's not a number ; for npc behavior action " + getId() + " (player " + player.getName() + ", npc " + npc.getId() + ")");
		}
		// change user npc data and mark as pushable
		userNpc.setVariableValue(variable, String.valueOf(number += modifier));
		GCore.inst().getNpcManager().getBehaviorMustPushUsers().add(user);
	}

}
