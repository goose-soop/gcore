package com.guillaumevdn.gcore.lib.npc.behavior.action.type;

import java.util.List;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.npc.Npc;
import com.guillaumevdn.gcore.lib.npc.NpcAnimation;
import com.guillaumevdn.gcore.lib.npc.behavior.action.BAction;
import com.guillaumevdn.gcore.lib.npc.behavior.action.BActionType;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPEnum;

public class ActionNpcAnimate extends BAction {

	// base
	private PPEnum<NpcAnimation> animation = new PPEnum<>("animation", this, NpcAnimation.TAKE_DAMAGE.name(), NpcAnimation.class, "npc animation", true, 0, EditorGUI.ICON_ENUM, GLocale.GUI_GCORE_EDITOR_NPC_BEHAVIORANIMATIONLORE.getLines());

	public ActionNpcAnimate(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, BActionType.NPC_ANIMATE, parent, mandatory, editorSlot, editorIcon, editorDescription);
	}

	// get
	public PPEnum<NpcAnimation> getAnimation() {
		return animation;
	}

	public NpcAnimation getAnimation(Player parser) {
		return animation.getParsedValue(parser);
	}

	// methods
	@Override
	public void run(final Player player, final Npc npc) {
		// get settings
		NpcAnimation animation = getAnimation(player);
		if (animation == null) {
			throw new IllegalArgumentException("invalid parameters ; animation = " + animation + " ; for npc behavior action " + getId() + " (player " + player.getName() + ", npc " + npc.getId() + ")");
		}
		// animate npc
		npc.animate(animation);
	}

}
