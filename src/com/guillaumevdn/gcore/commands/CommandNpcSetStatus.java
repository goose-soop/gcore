package com.guillaumevdn.gcore.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GPerm;
import com.guillaumevdn.gcore.data.GUser;
import com.guillaumevdn.gcore.data.GUserNpcData;
import com.guillaumevdn.gcore.data.UserInfo;
import com.guillaumevdn.gcore.lib.command.CommandArgument;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Param;
import com.guillaumevdn.gcore.lib.messenger.Messenger;
import com.guillaumevdn.gcore.lib.npc.Npc;
import com.guillaumevdn.gcore.lib.npc.NpcStatus;
import com.guillaumevdn.gcore.lib.util.GUserOperator;
import com.guillaumevdn.gcore.lib.util.Utils;

public class CommandNpcSetStatus extends CommandArgument {

	private Param paramNpc = new Param(Utils.asList("npc"), "npc id", null, false, true);
	private Param paramPlayer = new Param(Utils.asList("owner", "o"), "NPC 'owner' name", null, false, false);
	private Param paramStatus = new Param(Utils.asList("status", "s"), "new status list", null, false, true);
	private String available = null;

	public CommandNpcSetStatus() {
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
					final List<NpcStatus> status = new ArrayList<NpcStatus>();
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
							// change data
							GUserNpcData userNpc = user.getUserNpcData(npcId);
							userNpc.setStatus(Utils.asSet(status));
							user.updateNpc(npcId, userNpc);
							// update status
							npc.setStatus(status);
							Messenger.send(call.getSender(), Messenger.Level.NORMAL_SUCCESS, GCore.inst().getName(), "Changed status of NPC with ID " + npcId + " for player " + owner.getName() + ".");
						}
					}.operate();
				}
			}
		}
	}

}
