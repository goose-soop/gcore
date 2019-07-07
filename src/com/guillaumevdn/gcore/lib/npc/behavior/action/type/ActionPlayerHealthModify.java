package com.guillaumevdn.gcore.lib.npc.behavior.action.type;

import java.util.List;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.npc.Npc;
import com.guillaumevdn.gcore.lib.npc.behavior.action.BAction;
import com.guillaumevdn.gcore.lib.npc.behavior.action.BActionType;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPDouble;

public class ActionPlayerHealthModify extends BAction {

	// base
	private PPDouble modifier = addComponent(new PPDouble("modifier", this, "1.0", -Double.MAX_VALUE, Double.MAX_VALUE, true, 0, EditorGUI.ICON_NUMBER, GLocale.GUI_GCORE_EDITOR_NPC_BEHAVIORHEALTHMODIFIERLORE.getLines()));

	public ActionPlayerHealthModify(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, BActionType.PLAYER_HEALTH_MODIFY, parent, mandatory, editorSlot, editorIcon, editorDescription);
	}

	// get
	public PPDouble getModifier() {
		return modifier;
	}

	public Double getModifier(Player parser) {
		return modifier.getParsedValue(parser);
	}

	// methods
	@Override
	public void run(Player player, Npc npc) {
		// get modifier
		Double modifier = getModifier(player);
		if (modifier == null) {
			throw new IllegalArgumentException("invalid parameters ; modifier = " + modifier + " ; for npc behavior action " + getId() + " (player " + player.getName() + ", npc " + npc.getId() + ")");
		}
		// heal the player ; force parsing to damageable here, otherwise it can bug in other spigot versions for some reason
		// RIP "this method is ambiguous", forever in our hearts
		if (modifier > 0d) {
			double health = ((Damageable) player).getHealth(), max = ((Damageable) player).getMaxHealth();
			if (health < max) {
				health += modifier;
				if (health > max) health = max;
				((Damageable) player).setHealth(health);
			}
		}
		// damage the player
		else if (modifier != 0d) {
			((Damageable) player).damage(-modifier);
		}
	}

}
