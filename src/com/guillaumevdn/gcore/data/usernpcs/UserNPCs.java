package com.guillaumevdn.gcore.data.usernpcs;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.collection.IteratorControls;
import com.guillaumevdn.gcore.lib.concurrency.RWHashMap;
import com.guillaumevdn.gcore.lib.function.ThrowableTriConsumer;
import com.guillaumevdn.gcore.lib.function.TriConsumer;
import com.guillaumevdn.gcore.lib.legacy_npc.NPCManager;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;

/**
 * @author GuillaumeVDN
 */
public final class UserNPCs {

	private final UUID uuid;
	private RWHashMap<Integer, UserNPC> npcs;

	public UserNPCs(UUID uuid) {
		this.uuid = uuid;
		this.npcs = new RWHashMap<>(NPCManager.inst().getNPCsConfig().size(), 1f);
	}

	public UserNPCs(UUID uuid, RWHashMap<Integer, UserNPC> npcs) {
		this.uuid = uuid;
		this.npcs = npcs;
	}

	// ----- get
	public UUID getUniqueId() {
		return uuid;
	}

	public final void iterateNPCs(TriConsumer<Integer, UserNPC, IteratorControls> consumer) { npcs.iterateAndModify(consumer); }
	public final void iterateNPCsOrThrow(ThrowableTriConsumer<Integer, UserNPC, IteratorControls> consumer) throws Throwable { npcs.iterateAndModifyOrThrow(consumer); }

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

	// ----- methods
	public void setToSave() {
		BoardUsersNPCs.inst().addCachedToSave(uuid);
	}

	// ----- serialization
	public void write(DataIO writer) throws Throwable {
		writer.write("uuid", uuid);
		writer.writeObjectOrThrow("npcs", npcsWriter -> {
			npcs.iterateAndModifyOrThrow((id, npc, iter) -> {
				npcsWriter.writeObjectOrThrow("" + id, w -> {
					w.write("id", npc.getId());  // write id so the object is still written even if there's no modified data
					w.write("shown", npc.getModifiedShown());
					w.write("name", npc.getModifiedName());
					w.write("skinData", npc.getModifiedSkinData());
					w.write("skinSignature", npc.getModifiedSkinSignature());
					w.write("location", npc.getModifiedLocation());
					w.write("targetDistance", npc.getModifiedTargetDistance());
					w.writeSerializedList("status", npc.getModifiedStatus());
					w.write("heldItem", npc.getModifiedHeldItem());
					w.write("heldItemOff", npc.getModifiedHeldItemOff());
					w.write("boots", npc.getModifiedBoots());
					w.write("leggings", npc.getModifiedLeggings());
					w.write("chestplate", npc.getModifiedChestplate());
					w.write("helmet", npc.getModifiedHelmet());
				});
			});
		});
	}

	// ----- static
	public static UserNPCs get(Player player) {
		return BoardUsersNPCs.inst().getCachedValue(player.getUniqueId());
	}

}
