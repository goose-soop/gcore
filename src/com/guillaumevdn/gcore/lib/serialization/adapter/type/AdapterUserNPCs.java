package com.guillaumevdn.gcore.lib.serialization.adapter.type;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.data.usernpcs.UserNPC;
import com.guillaumevdn.gcore.data.usernpcs.UserNPCs;
import com.guillaumevdn.gcore.lib.concurrency.RWHashMap;
import com.guillaumevdn.gcore.lib.legacy_npc.NPCStatus;
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
		user.write(writer);
	}

	@Override
	public UserNPCs read(int version, DataIO reader) throws Throwable {
		if (version == 1) {
			UUID uuid = reader.readSerialized("uuid", UUID.class);
			RWHashMap<Integer, UserNPC> npcs = reader.readObjectMapOrThrow("npcs", Integer.class, new RWHashMap<>(), (__, npcId, npcReader) -> {
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
				UserNPC npc = new UserNPC(npcId);
				npc.saveNonDefault(null, null, shown, name, skinData, skinSignature, location, targetDistance, status, heldItem, heldItemOff, boots, leggings, chestplate, helmet);
				return npc;
			});
			return new UserNPCs(uuid, npcs);
		}
		throw new IllegalArgumentException("unknown adapter version " + version);
	}

}
