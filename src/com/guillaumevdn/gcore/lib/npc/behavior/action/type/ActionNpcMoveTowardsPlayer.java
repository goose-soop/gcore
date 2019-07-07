package com.guillaumevdn.gcore.lib.npc.behavior.action.type;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.data.GUser;
import com.guillaumevdn.gcore.data.GUserNpcData;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.npc.Npc;
import com.guillaumevdn.gcore.lib.npc.behavior.action.BAction;
import com.guillaumevdn.gcore.lib.npc.behavior.action.BActionType;
import com.guillaumevdn.gcore.lib.npc.navigation.Pathfinding;
import com.guillaumevdn.gcore.lib.npc.navigation.Point;
import com.guillaumevdn.gcore.lib.parseable.Parseable;

public class ActionNpcMoveTowardsPlayer extends BAction {

	// base
	public ActionNpcMoveTowardsPlayer(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, BActionType.NPC_MOVE_TOWARDS_PLAYER, parent, mandatory, editorSlot, editorIcon, editorDescription);
	}

	// methods
	@Override
	public void run(final Player player, final Npc npc) {
		// not the same world
		if (!player.getWorld().equals(npc.getLocation().getWorld())) {
			throw new IllegalArgumentException("action " + getId() + " is trying to move towards npc but it's in another world ; for npc behavior action " + getId() + " (player " + player.getName() + ", npc " + npc.getId() + ")");
		}
		// calculate pathfinding and follow the first step
		new Pathfinding(player.getWorld(), new Point(npc.getLocation()), new Point(player.getLocation()), 1, 50, 1, 1, 1) {
			@Override
			protected void onSuccess(List<Point> blocksPath, List<Location> smoothPath) {
				// get point
				Location location = smoothPath.get(0);
				if (location == null) return;
				// move npc
				npc.move(location, true);
				// change user npc data and mark as pushable
				GUser user = GUser.get(player);
				GUserNpcData userNpc = user.getUserNpcData(npc.getId());
				userNpc.setLocation(location);
				GCore.inst().getNpcManager().getBehaviorMustPushUsers().add(user);
			}
			@Override
			protected void onFail() {
				// do nothing on fail
			}
		}.start();
	}

}
