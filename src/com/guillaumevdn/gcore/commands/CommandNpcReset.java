package com.guillaumevdn.gcore.commands;

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
import com.guillaumevdn.gcore.lib.npc.NpcData;
import com.guillaumevdn.gcore.lib.util.GUserOperator;
import com.guillaumevdn.gcore.lib.util.Utils;

public class CommandNpcReset extends CommandArgument {

	// base
	private static final Param paramNpc = new Param(Utils.asList("npc"), "npc id", null, false, true);
	private static final Param paramPlayer = new Param(Utils.asList("owner", "o"), "NPC 'owner' name", null, false, false);

	public CommandNpcReset() {
		super(GCore.inst(), Utils.asList("reset"), "reset a NPC data", GPerm.GCORE_NPC_MANIPULATE, true, paramNpc, paramPlayer);
	}

	// perform
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
				// modify user data
				new GUserOperator(new UserInfo(owner)) {
					@Override
					protected void process(GUser user) {
						// npc exists
						final NpcData npcData = GCore.inst().getNpcManager().getNpcData(npcId);
						if (npcData != null) {
							// change data
							GUserNpcData userNpc = user.getUserNpcData(npcId);
							userNpc.replaceValues(npcData, owner, true);
							user.updateNpc(npcId, userNpc);
							GCore.inst().getNpcManager().spawnNpc(owner, npcId, null);
							Messenger.send(call.getSender(), Messenger.Level.NORMAL_SUCCESS, GCore.inst().getName(), "Reset NPC with ID " + npcId + " for player " + owner.getName() + ".");
						}
						// npc doesn't exist, remove it
						else {
							user.removeNpc(npcId, true);
							Npc npc = GCore.inst().getNpcManager().getNpc(owner, npcId);
							if (npc != null) {
								GCore.inst().getNpcManager().removeNpc(owner, npc);
							}
							Messenger.send(call.getSender(), Messenger.Level.NORMAL_SUCCESS, GCore.inst().getName(), "Removed unexisting NPC with ID " + npcId + " for player " + owner.getName() + ".");
						}
					}
				}.operate();
			}
		}
	}

}
