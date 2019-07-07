package com.guillaumevdn.gcore.lib.npc.behavior;

import org.bukkit.scheduler.BukkitRunnable;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.util.Utils;

// :gun: :cowboy: he can shoot a fly, d'un seul geste
public class BehaviorSaveUserTask extends BukkitRunnable {

	@Override
	public void run() {
		GCore.inst().getData().getUsers().pushAsync(Utils.asList(GCore.inst().getNpcManager().getBehaviorMustPushUsers()));
		GCore.inst().getNpcManager().getBehaviorMustPushUsers().clear();
	}

}
