package com.guillaumevdn.gcore.lib.legacy_npc.navigation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.data.usernpcs.UserNPC;
import com.guillaumevdn.gcore.data.usernpcs.UserNPCs;
import com.guillaumevdn.gcore.lib.legacy_npc.ElementNPC;
import com.guillaumevdn.gcore.lib.legacy_npc.NPC;
import com.guillaumevdn.gcore.lib.legacy_npc.NPCManager;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

public class GroupNavigator extends PathfindingNavigator {

	// base
	private ElementNPC config;
	private int npcId;
	private Collection<Player> allUsers;

	// constructor
	public GroupNavigator(int npcId, ElementNPC config, Collection<Player> allUsers, World world, Point start, Point target, int pathfindingStep, int pathfindingSpeed, int yToleranceUp, int yToleranceDown, double targetDistanceTolerance, long ticksPerStep) {
		super(world, start, target, pathfindingStep, pathfindingSpeed, yToleranceUp, yToleranceDown, targetDistanceTolerance, ticksPerStep);
		this.npcId = npcId;
		this.config = config;
		this.allUsers = allUsers;
	}

	// get
	public int getNpcId() {
		return npcId;
	}

	public Collection<Player> getAllUsers() {
		return allUsers;
	}

	@Override
	public List<NPC> getAffected() {
		List<NPC> npcs = new ArrayList<NPC>();
		for (Player player : allUsers) {
			NPC npc = NPCManager.inst().getNpc(player, npcId);
			if (npc != null) {
				npcs.add(npc);
			} else if (NPCManager.inst().spawnNpc(player, npcId, null)) {
				npc = NPCManager.inst().getNpc(player, npcId);
				if (npc != null) {
					npcs.add(npc);
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
		for (NPC npc : getAffected()) {
			npc.move(location, npc.getLocation().getY() == location.getY());
		}
	}

	private void updateNpcDataLocation(Location location) {
		for (Player player : allUsers) {
			UserNPCs user = UserNPCs.get(player);
			if (user != null) {
				UserNPC userNpc = user.getNPC(npcId);
				userNpc.saveNonDefault(config, Replacer.of(player), null, null, null, null, location, null, null, null, null, null, null, null, null);
				user.updateNpc(npcId, userNpc);
			}
		}
	}

}
