package com.guillaumevdn.gcore.lib.npc.navigation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.data.GUser;
import com.guillaumevdn.gcore.data.GUserNpcData;
import com.guillaumevdn.gcore.data.UserInfo;
import com.guillaumevdn.gcore.lib.npc.Npc;
import com.guillaumevdn.gcore.lib.util.GUserOperator;
import com.guillaumevdn.gcore.lib.util.Point;

public class GroupNavigator extends PathfindingNavigator {

	// base
	private int npcId;
	private Collection<UserInfo> allUsers;

	// constructor
	public GroupNavigator(int npcId, Collection<UserInfo> allUsers, World world, Point start, Point target, int pathfindingStep, int pathfindingSpeed, int yToleranceUp, int yToleranceDown, double targetDistanceTolerance, long ticksPerStep) {
		super(world, start, target, pathfindingStep, pathfindingSpeed, yToleranceUp, yToleranceDown, targetDistanceTolerance, ticksPerStep);
		this.npcId = npcId;
		this.allUsers = allUsers;
	}

	// get
	public int getNpcId() {
		return npcId;
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
				} else if (GCore.inst().getNpcManager().spawnNpc(player, npcId, null)) {
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
					GUserNpcData userNpc = user.getUserNpcData(npcId);
					userNpc.setLocation(location);
					user.updateNpc(npcId, userNpc);
				}
			}.operate();
		}
	}

}
