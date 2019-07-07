package com.guillaumevdn.gcore.lib.npc.behavior.condition.type;

import java.util.List;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.npc.Npc;
import com.guillaumevdn.gcore.lib.npc.behavior.condition.BCondition;
import com.guillaumevdn.gcore.lib.npc.behavior.condition.BConditionType;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPDouble;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPEnum;

public class ConditionNpcPlayerDistanceCheck extends BCondition {

	// base
	private PPEnum<Check> check = addComponent(new PPEnum<Check>("check", this, Check.EQUALS.name(), Check.class, "check type", true, 0, EditorGUI.ICON_ENUM, GLocale.GUI_GCORE_EDITOR_NPC_BEHAVIORCHECKLORE.getLines()));
	private PPDouble value = addComponent(new PPDouble("value", this, "20.0", -Double.MAX_VALUE, Double.MAX_VALUE, true, 1, EditorGUI.ICON_NUMBER, GLocale.GUI_GCORE_EDITOR_NPC_BEHAVIORPLAYERDISTANCEVALUELORE.getLines()));

	public ConditionNpcPlayerDistanceCheck(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, BConditionType.NPC_PLAYER_DISTANCE_CHECK, parent, mandatory, editorSlot, editorIcon, editorDescription);
	}

	// get
	public PPEnum<Check> getCheck() {
		return check;
	}

	public Check getCheck(Player parser) {
		return check.getParsedValue(parser);
	}

	public PPDouble getValue() {
		return value;
	}

	public Double getValue(Player parser) {
		return value.getParsedValue(parser);
	}

	// methods
	@Override
	public boolean check(Player player, Npc npc) {
		// get settings
		Check check = getCheck(player);
		Double value = getValue(player);
		if (check == null || value == null) {
			throw new IllegalArgumentException("invalid parameters ; check = " + check + ", value = " + value + " ; for npc behavior condition " + getId() + " (player " + player.getName() + ", npc " + npc.getId() + ")");
		}
		// not the same world
		if (!player.getWorld().equals(npc.getLocation().getWorld())) {
			throw new IllegalArgumentException("trying check distance from player to npc but it's in another world ; for npc behavior condition " + getId() + " (player " + player.getName() + ", npc " + npc.getId() + ")");
		}
		// checks
		double distance = player.getLocation().distance(npc.getLocation());
		if (check.equals(Check.ABOVE)) {
			return distance > value;
		} else if (check.equals(Check.ABOVE_OR_EQUALS)) {
			return distance >= value;
		} else if (check.equals(Check.BELOW_OR_EQUALS)) {
			return distance <= value;
		} else if (check.equals(Check.BELOW)) {
			return distance < value;
		} else if (check.equals(Check.EQUALS)) {
			return distance == value;
		}
		// wot ze fok
		throw new IllegalStateException("b*tch i can't even ??? wot ???");
	}

	// check
	public static enum Check {

		ABOVE,
		ABOVE_OR_EQUALS,
		EQUALS,
		BELOW_OR_EQUALS,
		BELOW

	}

}
