package com.guillaumevdn.gcore.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GPerm;
import com.guillaumevdn.gcore.data.GUser;
import com.guillaumevdn.gcore.data.ModifiedNpcData;
import com.guillaumevdn.gcore.data.UserInfo;
import com.guillaumevdn.gcore.lib.command.CommandArgument;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Param;
import com.guillaumevdn.gcore.lib.gui.ItemData;
import com.guillaumevdn.gcore.lib.messenger.Messenger;
import com.guillaumevdn.gcore.lib.npc.Npc;
import com.guillaumevdn.gcore.lib.npc.NpcStatus;
import com.guillaumevdn.gcore.lib.util.GUserOperator;
import com.guillaumevdn.gcore.lib.util.Utils;

public class CommandNpcSetEquipment extends CommandArgument {

	private Param paramNpc = new Param(Utils.asList("npc"), "npc id", null, false, true);
	private Param paramPlayer = new Param(Utils.asList("owner", "o"), "NPC 'owner' name", null, false, false);
	private Param paramStatus = new Param(Utils.asList("status", "s"), "new status list", null, false, true);
	private String available = null;

	public CommandNpcSetEquipment() {
		super(GCore.inst(), Utils.asList("setstatus"), "change status of a spawned NPC", GPerm.GCORE_NPC_MANIPULATE, true);
		for (NpcStatus status : NpcStatus.values()) {
			if (available == null) {
				available = status.name();
			} else {
				available += ", " + status.name();
			}
		}
	}

	@Override
	public void perform(final CommandCall call) {
		// disabled command
		if (GCore.inst().getNpcManager() == null) {
			Messenger.send(call.getSender(), Messenger.Level.SEVERE_ERROR, GCore.inst().getName(), "This command is disabled (no NPC manager found).");
			return;
		}
		// get owner
		final Player owner = paramPlayer.getPlayer(call, true);
		if (owner != null) {
			// get npc
			final Integer npcId = paramNpc.getInt(call);
			if (npcId != null) {
				final Npc npc = GCore.inst().getNpcManager().getNpc(owner, npcId);
				if (npc == null) {
					Messenger.send(call.getSender(), Messenger.Level.SEVERE_ERROR, GCore.inst().getName(), "Couldn't find NPC with ID " + npcId + " for player " + owner.getName() + ".");
					return;
				}
				// get status
				String rawStatus = paramStatus.getString(call);
				if (rawStatus != null) {
					List<NpcStatus> status = new ArrayList<NpcStatus>();
					for (String raw : Utils.split(",", rawStatus, false)) {
						NpcStatus stat = Utils.valueOfOrNull(NpcStatus.class, raw);
						if (stat == null) {
							Messenger.send(call.getSender(), Messenger.Level.SEVERE_ERROR, GCore.inst().getName(), "Couldn't find status '" + raw + "' (available : " + available + ").");
						} else {
							status.add(stat);
						}
					}
					// modify user data
					new GUserOperator(new UserInfo(owner)) {
						@Override
						protected void process(GUser user) {
							// build stuff array
							PlayerInventory inv = call.getSenderAsPlayer().getInventory();
							ItemData[] items = new ItemData[] {
									new ItemData("mainhand", inv.getItemInMainHand()),
									new ItemData("offhand", inv.getItemInOffHand()),
									new ItemData("boots", inv.getBoots()),
									new ItemData("leggings", inv.getLeggings()),
									new ItemData("chestplate", inv.getChestplate()),
									new ItemData("helmet", inv.getHelmet())
							};
							// change data
							ModifiedNpcData modif = user.getNpc(npcId);
							if (modif == null) modif = new ModifiedNpcData(npcId, true);
							modif.setItems(items);
							user.updateNpc(npcId, modif);
							// teleport
							npc.setItems(items);
							Messenger.send(call.getSender(), Messenger.Level.NORMAL_SUCCESS, GCore.inst().getName(), "Set equipment of NPC with ID " + npcId + " for player " + owner.getName() + ".");
						}
					}.operate();
				}
			}
		}
	}

}
