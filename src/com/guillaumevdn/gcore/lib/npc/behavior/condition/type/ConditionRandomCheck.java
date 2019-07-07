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
import com.guillaumevdn.gcore.lib.util.Utils;

public class ConditionRandomCheck extends BCondition {

	// base
	private PPEnum<Check> check = addComponent(new PPEnum<Check>("check", this, Check.EQUALS.name(), Check.class, "check type", true, 0, EditorGUI.ICON_ENUM, GLocale.GUI_GCORE_EDITOR_NPC_BEHAVIORCHECKLORE.getLines()));
	private PPDouble value = addComponent(new PPDouble("value", this, "20.0", -Double.MAX_VALUE, Double.MAX_VALUE, true, 1, EditorGUI.ICON_NUMBER, GLocale.GUI_GCORE_EDITOR_NPC_BEHAVIORRANDOMVALUELORE.getLines()));
	private PPDouble min = addComponent(new PPDouble("min", this, "0.0", -Double.MAX_VALUE, Double.MAX_VALUE, true, 2, EditorGUI.ICON_NUMBER, GLocale.GUI_GCORE_EDITOR_NPC_BEHAVIORRANDOMMINLORE.getLines()));
	private PPDouble max = addComponent(new PPDouble("max", this, "100.0", -Double.MAX_VALUE, Double.MAX_VALUE, true, 3, EditorGUI.ICON_NUMBER, GLocale.GUI_GCORE_EDITOR_NPC_BEHAVIORRANDOMMAXLORE.getLines()));

	public ConditionRandomCheck(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, BConditionType.RANDOM_CHECK, parent, mandatory, editorSlot, editorIcon, editorDescription);
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

	public PPDouble getMin() {
		return min;
	}

	public Double getMin(Player parser) {
		return min.getParsedValue(parser);
	}

	public PPDouble getMax() {
		return max;
	}

	public Double getMax(Player parser) {
		return max.getParsedValue(parser);
	}

	// methods
	@Override
	public boolean check(Player player, Npc npc) {
		// get settings
		Check check = getCheck(player);
		Double value = getValue(player);
		Double min = getMin(player);
		Double max = getMax(player);
		if (check == null || value == null || min == null || max == null) {
			throw new IllegalArgumentException("invalid parameters ; check = " + check + ", value = " + value + ", min = " + min + ", max = " + max + " ; for npc behavior condition " + getId() + " (player " + player.getName() + ", npc " + npc.getId() + ")");
		}
		// not the same world
		if (!player.getWorld().equals(npc.getLocation().getWorld())) {
			throw new IllegalArgumentException("trying check distance from player to npc but it's in another world ; for npc behavior condition " + getId() + " (player " + player.getName() + ", npc " + npc.getId() + ")");
		}
		// checks
		double generated = Utils.randomDouble(min, max);
		if (check.equals(Check.ABOVE)) {
			return generated > value;
		} else if (check.equals(Check.ABOVE_OR_EQUALS)) {
			return generated >= value;
		} else if (check.equals(Check.BELOW_OR_EQUALS)) {
			return generated <= value;
		} else if (check.equals(Check.BELOW)) {
			return generated < value;
		} else if (check.equals(Check.EQUALS)) {
			return generated == value;
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
