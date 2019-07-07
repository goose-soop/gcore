package com.guillaumevdn.gcore.lib.npc.behavior.condition.type;

import java.util.List;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.data.GUser;
import com.guillaumevdn.gcore.data.GUserNpcData;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.npc.Npc;
import com.guillaumevdn.gcore.lib.npc.behavior.condition.BCondition;
import com.guillaumevdn.gcore.lib.npc.behavior.condition.BConditionType;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPEnum;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPString;
import com.guillaumevdn.gcore.lib.util.Utils;

public class ConditionNpcVariableCheck extends BCondition {

	// base
	private PPString variable = addComponent(new PPString("variable", this, "health", true, 0, EditorGUI.ICON_STRING, GLocale.GUI_GCORE_EDITOR_NPC_BEHAVIORVARIABLELORE.getLines()));
	private PPEnum<Check> check = addComponent(new PPEnum<Check>("check", this, Check.EQUALS.name(), Check.class, "check type", true, 1, EditorGUI.ICON_ENUM, GLocale.GUI_GCORE_EDITOR_NPC_BEHAVIORCHECKLORE.getLines()));
	private PPString value = addComponent(new PPString("value", this, "20.0", true, 2, EditorGUI.ICON_STRING, GLocale.GUI_GCORE_EDITOR_NPC_BEHAVIORVARIABLEVALUELORE.getLines()));

	public ConditionNpcVariableCheck(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, BConditionType.NPC_VARIABLE_CHECK, parent, mandatory, editorSlot, editorIcon, editorDescription);
	}

	// get
	public PPString getVariable() {
		return variable;
	}

	public String getVariable(Player parser) {
		return variable.getParsedValue(parser);
	}

	public PPEnum<Check> getCheck() {
		return check;
	}

	public Check getCheck(Player parser) {
		return check.getParsedValue(parser);
	}

	public PPString getValue() {
		return value;
	}

	public String getValue(Player parser) {
		return value.getParsedValue(parser);
	}

	// methods
	@Override
	public boolean check(Player player, Npc npc) {
		// get settings
		String variable = getVariable(player);
		Check check = getCheck(player);
		String value = getValue(player);
		if (variable == null || check == null || value == null) {
			throw new IllegalArgumentException("invalid parameters ; variable = " + variable + ", check = " + check + ", value = " + value + " ; for npc behavior action " + getId() + " (player " + player.getName() + ", npc " + npc.getId() + ")");
		}
		// get user npc data
		GUser user = GUser.get(player);
		GUserNpcData userNpc = user.getUserNpcData(npc.getId());
		String currentValue = userNpc.getVariableValue(variable);
		// check : equals
		if (check.equals(Check.EQUALS)) {
			return currentValue.equals(value);
		}
		// not a number
		Double currentValueNumber = Utils.doubleOrNull(currentValue);
		if (currentValueNumber == null) {
			throw new IllegalStateException("tried to perform number checks on '" + value + "' of variable " + variable + " but it's not a number ; for npc behavior action " + getId() + " (player " + player.getName() + ", npc " + npc.getId() + ")");
		}
		Double valueNumber = Utils.doubleOrNull(value);
		if (valueNumber == null) {
			throw new IllegalArgumentException("invalid parameters ; value = " + value + " but it must be a number ; for npc behavior action " + getId() + " (player " + player.getName() + ", npc " + npc.getId() + ")");
		}
		// checks : numbers
		if (check.equals(Check.ABOVE_NUMBER)) {
			return currentValueNumber > valueNumber;
		} else if (check.equals(Check.ABOVE_OR_EQUALS_NUMBER)) {
			return currentValueNumber >= valueNumber;
		} else if (check.equals(Check.BELOW_OR_EQUALS_NUMBER)) {
			return currentValueNumber <= valueNumber;
		} else if (check.equals(Check.BELOW_NUMBER)) {
			return currentValueNumber < valueNumber;
		} else if (check.equals(Check.EQUALS_NUMBER)) {
			return currentValueNumber == valueNumber;
		}
		// wot ze fok
		throw new IllegalStateException("b*tch i can't even ??? wot ???");
	}

	// check
	public static enum Check {

		ABOVE_NUMBER,
		ABOVE_OR_EQUALS_NUMBER,
		EQUALS_NUMBER,
		BELOW_OR_EQUALS_NUMBER,
		BELOW_NUMBER,
		EQUALS

	}

}
