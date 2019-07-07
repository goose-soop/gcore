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

public class ActionNpcVariableMultiplyNumber extends BAction {

	// base
	private PPString variable = addComponent(new PPString("variable", this, "health", true, 0, EditorGUI.ICON_STRING, GLocale.GUI_GCORE_EDITOR_NPC_BEHAVIORVARIABLELORE.getLines()));
	private PPDouble multiplier = addComponent(new PPDouble("multiplier", this, "2.0", -Double.MAX_VALUE, Double.MAX_VALUE, true, 1, EditorGUI.ICON_NUMBER, GLocale.GUI_GCORE_EDITOR_NPC_BEHAVIORVARIABLEMULTIPLIERLORE.getLines()));

	public ActionNpcVariableMultiplyNumber(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, BActionType.NPC_VARIABLE_MULTIPLY_NUMBER, parent, mandatory, editorSlot, editorIcon, editorDescription);
	}

	// get
	public PPString getVariable() {
		return variable;
	}

	public String getVariable(Player parser) {
		return variable.getParsedValue(parser);
	}

	public PPDouble getMultiplier() {
		return multiplier;
	}

	public Double getMultiplier(Player parser) {
		return multiplier.getParsedValue(parser);
	}

	// methods
	@Override
	public void run(Player player, Npc npc) {
		// get settings
		String variable = getVariable(player);
		Double multiplier = getMultiplier(player);
		if (variable == null || multiplier == null) {
			throw new IllegalArgumentException("invalid parameters ; variable = " + variable + ", multiplier = " + multiplier + " ; for npc behavior action " + getId() + " (player " + player.getName() + ", npc " + npc.getId() + ")");
		}
		// get user npc data
		GUser user = GUser.get(player);
		GUserNpcData userNpc = user.getUserNpcData(npc.getId());
		String value = userNpc.getVariableValue(variable);
		Double number = Utils.doubleOrNull(value);
		// not a number
		if (number == null) {
			throw new IllegalStateException("tried to multiply value '" + value + "' of variable " + variable + " but it's not a number ; for npc behavior action " + getId() + " (player " + player.getName() + ", npc " + npc.getId() + ")");
		}
		// change user npc data and mark as pushable
		userNpc.setVariableValue(variable, String.valueOf(number *= multiplier));
		GCore.inst().getNpcManager().getBehaviorMustPushUsers().add(user);
	}

}
