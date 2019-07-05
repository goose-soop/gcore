package com.guillaumevdn.gcore.lib.npc.behavior.action.type;

import java.util.List;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.data.GUser;
import com.guillaumevdn.gcore.data.GUserNpcData;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.npc.Npc;
import com.guillaumevdn.gcore.lib.npc.behavior.action.BAction;
import com.guillaumevdn.gcore.lib.npc.behavior.action.BActionType;
import com.guillaumevdn.gcore.lib.parseable.Parseable;

public class ActionNpcHide extends BAction {

	// base
	public ActionNpcHide(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, BActionType.NPC_VARIABLE_MULTIPLY_NUMBER, parent, mandatory, editorSlot, editorIcon, editorDescription);
	}

	// methods
	@Override
	public void run(Player player, Npc npc) {
		// remove npc
		GCore.inst().getNpcManager().removeNpc(player, npc);
		// change user npc data and mark as pushable
		GUser user = GUser.get(player);
		GUserNpcData userNpc = user.getUserNpcData(npc.getId());
		userNpc.setShown(false);
		GCore.inst().getNpcManager().getBehaviorMustPushUsers().add(user);
	}

}
