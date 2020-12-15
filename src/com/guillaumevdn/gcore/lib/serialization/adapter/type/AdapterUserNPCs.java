package com.guillaumevdn.gcore.lib.serialization.adapter.type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.data.usernpcs.UserNPC;
import com.guillaumevdn.gcore.data.usernpcs.UserNPCs;
import com.guillaumevdn.gcore.lib.legacy_npc.NPCStatus;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.serialization.adapter.DataAdapter;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;

/**
 * @author GuillaumeVDN
 */
public final class AdapterUserNPCs extends DataAdapter<UserNPCs> {

	public static final AdapterUserNPCs INSTANCE = new AdapterUserNPCs();

	private AdapterUserNPCs() {
		super(UserNPCs.class, 1);
	}

	@Override
	public void write(UserNPCs user, DataIO writer) throws Throwable {
		writer.write("uuid", user.getUniqueId());
		writer.writeObjectOrThrow("npcs", npcsWriter -> {
			for (int id : user.getNPCs().keySet()) {
				UserNPC npc = user.getNPC(id);
				npcsWriter.writeObjectOrThrow("" + id, w -> {
					w.write("id", npc.getId()); // write id so the object is still written even if there's no modified data
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
			}
		});
	}

	@Override
	public UserNPCs read(int version, DataIO reader) throws Throwable {
		if (version == 1) {
			UUID uuid = reader.readSerialized("uuid", UUID.class);
			Map<Integer, UserNPC> npcs = reader.readSameMapOrThrow("npcs", Integer.class, (key, r) -> {
				Integer id = NumberUtils.integerOrNull(key);
				return id == null ? null : r.readObjectOrThrow(key, npcReader -> {
					Boolean shown = npcReader.readBoolean("shown");
					String name = npcReader.readString("name");
					String skinData = npcReader.readString("skinData");
					String skinSignature = npcReader.readString("skinSignature");
					Location location = npcReader.readSerialized("location", Location.class);
					Double targetDistance = npcReader.readDouble("targetDistance");
					List<NPCStatus> status = npcReader.readSerializedList("status", NPCStatus.class);
					ItemStack heldItem = npcReader.readItem("heldItem");
					ItemStack heldItemOff = npcReader.readItem("heldItemOff");
					ItemStack boots = npcReader.readItem("leggings");
					ItemStack leggings = npcReader.readItem("leggings");
					ItemStack chestplate = npcReader.readItem("chestplate");
					ItemStack helmet = npcReader.readItem("helmet");
					UserNPC npc = new UserNPC(id);
					npc.saveNonDefault(null, null, shown, name, skinData, skinSignature, location, targetDistance, status, heldItem, heldItemOff, boots, leggings, chestplate, helmet);
					return npc;
				});
			});
			if (npcs == null) npcs = new HashMap<>();
			return new UserNPCs(uuid, npcs);
		}
		throw new IllegalArgumentException("unknown adapter version " + version);
	}

}
