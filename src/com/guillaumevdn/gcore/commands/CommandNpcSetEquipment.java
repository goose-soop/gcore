package com.guillaumevdn.gcore.commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GPerm;
import com.guillaumevdn.gcore.data.GUser;
import com.guillaumevdn.gcore.data.GUserNpcData;
import com.guillaumevdn.gcore.data.UserInfo;
import com.guillaumevdn.gcore.lib.command.CommandArgument;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Param;
import com.guillaumevdn.gcore.lib.gui.ItemData;
import com.guillaumevdn.gcore.lib.messenger.Messenger;
import com.guillaumevdn.gcore.lib.npc.Npc;
import com.guillaumevdn.gcore.lib.util.GUserOperator;
import com.guillaumevdn.gcore.lib.util.Utils;

public class CommandNpcSetEquipment extends CommandArgument {

	private Param paramNpc = new Param(Utils.asList("npc"), "npc id", null, false, true);
	private Param paramPlayer = new Param(Utils.asList("owner", "o"), "NPC 'owner' name", null, false, false);

	public CommandNpcSetEquipment() {
		super(GCore.inst(), Utils.asList("setquipment"), "set the equipment of a NPC", GPerm.GCORE_NPC_MANIPULATE, true);
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
						// build stuff array
						PlayerInventory inv = call.getSenderAsPlayer().getInventory();
						// change data
						GUserNpcData userNpc = user.getUserNpcData(npcId);
						ItemData heldItem = new ItemData("mainhand", inv.getItemInMainHand());
						ItemData heldItemOff = new ItemData("offhand", inv.getItemInOffHand());
						ItemData boots = new ItemData("boots", inv.getBoots());
						ItemData leggings = new ItemData("leggings", inv.getLeggings());
						ItemData chestplate = new ItemData("chestplate", inv.getChestplate());
						ItemData helmet = new ItemData("helmet", inv.getHelmet());
						userNpc.setHeldItem(heldItem);
						userNpc.setHeldItemOff(heldItemOff);
						userNpc.setBoots(boots);
						userNpc.setLeggings(leggings);
						userNpc.setChestplate(chestplate);
						userNpc.setHelmet(helmet);
						user.updateNpc(npcId, userNpc);
						// set items
						npc.setItems(new ItemData[] { heldItem, heldItemOff, boots, leggings, chestplate, helmet });
						Messenger.send(call.getSender(), Messenger.Level.NORMAL_SUCCESS, GCore.inst().getName(), "Set equipment of NPC with ID " + npcId + " for player " + owner.getName() + ".");
					}
				}.operate();
			}
		}
	}

}
