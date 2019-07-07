package com.guillaumevdn.gcore.lib.npc.behavior;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.data.GUser;
import com.guillaumevdn.gcore.data.GUserNpcData;
import com.guillaumevdn.gcore.lib.npc.Npc;
import com.guillaumevdn.gcore.lib.npc.behavior.RunningBehavior.ProgressResult;
import com.guillaumevdn.gcore.lib.util.Utils;

// :gun: :cowboy: in the désert, there is personne, never a coup de téléphone
public class BehaviorProgressTask extends BukkitRunnable {

	@Override
	public void run() {
		// check users
		for (GUser user : GCore.inst().getData().getUsers().getCache().values()) {
			Player player = user.getInfo().toPlayer();
			if (player == null) continue;
			boolean push = false;
			// check spawned npcs
			for (GUserNpcData userNpc : user.getUserNpcData().values()) {
				Npc npc = GCore.inst().getNpcManager().getNpc(player, userNpc.getId());
				if (npc == null || !npc.isSpawned()) continue;
				// check behaviors
				for (RunningBehavior behavior : Utils.asList(userNpc.getRunningBehaviors())) {
					ProgressResult result = behavior.progress(player, npc);
					if (result.getMustRemove()) {// remove
						userNpc.getRunningBehaviors().remove(behavior);
						user.updateNpc(userNpc.getId(), userNpc, false);
					}
					if (result.getMustSave()) {// save
						push = true;
					}
				}
			}
			// set must push
			if (push) {
				GCore.inst().getNpcManager().getBehaviorMustPushUsers().add(user);
			}
		}
	}

}
