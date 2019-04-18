package com.guillaumevdn.gcore.lib.npc.navigation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.data.GUser;
import com.guillaumevdn.gcore.data.ModifiedNpcData;
import com.guillaumevdn.gcore.data.UserInfo;
import com.guillaumevdn.gcore.lib.npc.Npc;
import com.guillaumevdn.gcore.lib.npc.NpcData;
import com.guillaumevdn.gcore.lib.util.GUserOperator;

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

	@Override
	public List<Npc> getAffected() {
		List<Npc> npcs = new ArrayList<Npc>();
		for (UserInfo info : allUsers) {
			Player player = info.toPlayer();
			if (player != null) {
				Npc npc = GCore.inst().getNpcManager().getNpc(player, npcId);
				if (npc != null) {
					npcs.add(npc);
				} else if (GCore.inst().getNpcManager().spawnNpc(player, npcId, npcData, null)) {
					npc = GCore.inst().getNpcManager().getNpc(player, npcId);
					if (npc != null) {
						npcs.add(npc);
					}
				}
			}
		}
		return npcs;
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
	protected void onStep(Location step) {
		move(step);
	}

	@Override
	protected void onSuccess() {
		updateNpcDataLocation(getTarget().toLocation(getWorld()));
	}

	// utils
	private void move(Location location) {
		for (Npc npc : getAffected()) {
			npc.move(location, npc.getLocation().getY() == location.getY());
		}
	}

	private void updateNpcDataLocation(final Location location) {
		for (UserInfo info : allUsers) {
			new GUserOperator(info) {
				@Override
				protected void process(GUser user) {
					ModifiedNpcData modif = user.getNpc(npcId);
					if (modif == null) modif = new ModifiedNpcData(npcId, true);
					modif.setLocation(location);
					user.updateNpc(npcId, modif);
				}
			}.operate();
		}
	}

}
