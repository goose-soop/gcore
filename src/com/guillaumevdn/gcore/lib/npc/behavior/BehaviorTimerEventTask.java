package com.guillaumevdn.gcore.lib.npc.behavior;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.npc.behavior.event.BEvent.AttemptContext;

// :gun: :cowboy: in the poussière of ohio, wearing his beautiful chapeau, but who this cavaliero ? LUUUCKYYYY LUUUCK
public class BehaviorTimerEventTask extends BukkitRunnable {

	@Override
	public void run() {
		// check players
		for (Player player : GCore.inst().getNpcManager().getNpcs().keySet()) {
			GCore.inst().getNpcManager().runBehaviorsAttempt(AttemptContext.TIMER, player, null);
		}
	}

}
