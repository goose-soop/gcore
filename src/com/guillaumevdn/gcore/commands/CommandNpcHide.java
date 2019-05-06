package com.guillaumevdn.gcore.commands;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GPerm;
import com.guillaumevdn.gcore.data.GUser;
import com.guillaumevdn.gcore.data.ModifiedNpcData;
import com.guillaumevdn.gcore.data.UserInfo;
import com.guillaumevdn.gcore.lib.command.CommandArgument;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Param;
import com.guillaumevdn.gcore.lib.messenger.Messenger;
import com.guillaumevdn.gcore.lib.npc.Npc;
import com.guillaumevdn.gcore.lib.util.GUserOperator;
import com.guillaumevdn.gcore.lib.util.Utils;

public class CommandNpcHide extends CommandArgument {

	private Param paramNpc = new Param(Utils.asList("npc"), "npc id", null, false, true);
	private Param paramPlayer = new Param(Utils.asList("owner", "o"), "NPC 'owner' name", null, false, false);

	public CommandNpcHide() {
		super(GCore.inst(), Utils.asList("hide"), "hide a shown spawned NPC", GPerm.GCORE_NPC_MANIPULATE, true);
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
				// modify user data
				new GUserOperator(new UserInfo(owner)) {
					@Override
					protected void process(GUser user) {
						// change data
						ModifiedNpcData modif = user.getNpc(npcId);
						if (modif == null) modif = new ModifiedNpcData(npcId, false);
						modif.setShown(false);
						user.updateNpc(npcId, modif);
						// hide
						npc.despawn();
						Messenger.send(call.getSender(), Messenger.Level.NORMAL_SUCCESS, GCore.inst().getName(), "Hidden NPC with ID " + npcId + " for player " + owner.getName() + ".");
					}
				}.operate();
			}
		}
	}

}
