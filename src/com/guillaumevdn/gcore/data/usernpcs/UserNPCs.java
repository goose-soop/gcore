package com.guillaumevdn.gcore.data.usernpcs;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.data.board.keyed.KeyReference;

/**
 * @author GuillaumeVDN
 */
public final class UserNPCs {

	private final UUID uuid;
	private final KeyReference<UUID> ref;
	private Map<Integer, UserNPC> npcs = new HashMap<>();

	public UserNPCs(UUID uuid) {
		this.uuid = uuid;
		this.ref = new KeyReference<>(uuid);
	}

	public UserNPCs(UUID uuid, Map<Integer, UserNPC> npcs) {
		this.uuid = uuid;
		this.ref = new KeyReference<>(uuid);
		this.npcs = npcs;
	}

	// get
	public UUID getUniqueId() {
		return uuid;
	}

	public Map<Integer, UserNPC> getNPCs() {
		return Collections.unmodifiableMap(npcs);
	}

	public UserNPC getNPC(int id) {
		return npcs.get(id);
	}

	public void updateNpc(int id, UserNPC npc) {
		npcs.put(id, npc);
		setToSave();
	}

	public void removeNpc(int id) {
		if (npcs.remove(id) != null) {
			setToSave();
		}
	}

	// methods
	public void setToSave() {
		BoardUsersNPCs.inst().addCachedToSave(ref);
	}

	// static
	public static UserNPCs get(Player player) {
		return BoardUsersNPCs.inst().getCachedValue(player.getUniqueId());
	}

}
