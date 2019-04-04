package com.guillaumevdn.gcore.lib.npc.navigation;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.data.ModifiedNpcData;
import com.guillaumevdn.gcore.data.PCUser;
import com.guillaumevdn.gcore.data.UserInfo;
import com.guillaumevdn.gcore.lib.npc.Npc;
import com.guillaumevdn.gcore.lib.npc.NpcData;
import com.guillaumevdn.gcore.lib.util.PCUserOperator;

public class GroupNavigator extends PathfindingNavigator {

	// base
	private int npcId;
	private NpcData npcData;
	private UserInfo mainUser;
	private Collection<UserInfo> allUsers;

	// constructor
	public GroupNavigator(int npcId, NpcData npcData, UserInfo mainUser, Collection<UserInfo> allUsers, Point target, int pathfindingStep, int pathfindingSpeed, int yToleranceUp, int yToleranceDown, double targetDistanceTolerance, long ticksPerStep) {
		super(null, null, target, pathfindingStep, pathfindingSpeed, yToleranceUp, yToleranceDown, targetDistanceTolerance, ticksPerStep);
		this.npcId = npcId;
		this.npcData = npcData;
		this.mainUser = mainUser;
		this.allUsers = allUsers;
	}

	// get
	public int getNpcId() {
		return npcId;
	}

	public UserInfo getMainUser() {
		return mainUser;
	}

	public Collection<UserInfo> getAllUsers() {
		return allUsers;
	}

	// methods
	@Override
	public void start() {
		// invalid state
		if (!getState().equals(State.WAITING)) {
			return;
		}
		// get main user
		Player main = mainUser.toPlayer();
		if (main == null) {
			updateNpcDataLocation(getTarget().toLocation(getWorld()));
			cancel();
			return;
		}
		// ensure main npc is spawned
		GCore.inst().getNpcManager().spawnNpc(main, npcId, npcData, null);
		Npc mainNpc = GCore.inst().getNpcManager().getNpc(main, npcId);
		if (mainNpc == null) {
			updateNpcDataLocation(getTarget().toLocation(getWorld()));
			cancel();
			return;
		}
		// get world and starting point
		setWorld(mainNpc.getLocation().getWorld());
		setStart(new Point(mainNpc.getLocation()));
		// start pathfinding and navigation
		super.start();
	}

	@Override
	protected void onFail() {
		Location location = getTarget().toLocation(getWorld());
		move(location);
		updateNpcDataLocation(location);
	}

	@Override
	protected void onStep(Point step) {
		move(step.toLocation(getWorld()));
	}

	@Override
	protected void onSuccess() {
		updateNpcDataLocation(getTarget().toLocation(getWorld()));
	}

	// utils
	private void move(Location location) {
		for (UserInfo info : allUsers) {
			// attempt to spawn and move/teleport
			Player player = info.toPlayer();
			if (player != null) {
				GCore.inst().getNpcManager().spawnNpc(player, npcId, npcData, null);
				Npc npc = GCore.inst().getNpcManager().getNpc(player, npcId);
				if (npc != null) {
					npc.move(location);
				}
			}
		}
	}

	private void updateNpcDataLocation(final Location location) {
		for (UserInfo info : allUsers) {
			new PCUserOperator(info) {
				@Override
				protected void process(PCUser user) {
					ModifiedNpcData modif = user.getNpc(npcId);
					if (modif == null) modif = new ModifiedNpcData(npcId, true);
					modif.setLocation(location);
					user.updateNpc(npcId, modif);
				}
			}.operate();
		}
	}

}
