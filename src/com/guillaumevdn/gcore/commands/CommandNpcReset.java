package com.guillaumevdn.gcore.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GPerm;
import com.guillaumevdn.gcore.data.GUser;
import com.guillaumevdn.gcore.data.UserInfo;
import com.guillaumevdn.gcore.lib.command.CommandArgument;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Param;
import com.guillaumevdn.gcore.lib.messenger.Messenger;
import com.guillaumevdn.gcore.lib.npc.Npc;
import com.guillaumevdn.gcore.lib.util.GUserOperator;
import com.guillaumevdn.gcore.lib.util.Utils;

public class CommandNpcReset extends CommandArgument {

	// base
	private static final Param paramNpc = new Param(Utils.asList("npc"), "npc id", null, false, true);
	private static final Param paramAllNpcs = new Param(Utils.asList("allnpcs"), null, null, false, false);
	private static final Param paramPlayer = new Param(Utils.asList("owner", "o"), "NPC 'owner' name", null, false, false);
	private static final Param paramAllplayers = new Param(Utils.asList("allowners", "ao"), null, null, false, false);

	static {
		paramNpc.setIncompatibleWith(paramAllNpcs);
		paramAllNpcs.setIncompatibleWith(paramNpc);

		paramPlayer.setIncompatibleWith(paramAllplayers);
		paramAllplayers.setIncompatibleWith(paramPlayer);
	}

	public CommandNpcReset() {
		super(GCore.inst(), Utils.asList("reset"), "reset a NPC data", GPerm.GCORE_NPC_MANIPULATE, true, paramNpc, paramAllNpcs, paramPlayer, paramAllplayers);
	}

	// perform
	private List<CommandSender> confirm = new ArrayList<CommandSender>();

	@Override
	public void perform(final CommandCall call) {
		// disabled command
		if (GCore.inst().getNpcManager() == null) {
			Messenger.send(call.getSender(), Messenger.Level.SEVERE_ERROR, GCore.inst().getName(), "This command is disabled (no NPC manager found).");
			return;
		}
		// confirm if all players or all NPCs
		final CommandSender sender = call.getSender();
		if ((paramAllNpcs.has(call) || paramAllplayers.has(call)) && !confirm.remove(sender)) {
			confirm.add(sender);
			Messenger.send(call.getSender(), Messenger.Level.SEVERE_INFO, GCore.inst().getName(), "You're about to reset " + (paramAllNpcs.has(call) ? "all NPCs" : "a NPC") + " for " + (paramAllplayers.has(call) ? "all players" : "a player") + ". Please perform the command again to confirm this action (it's irreversible and might have unknown side-effects in plugins using GCore NPCs). This action expires in 15 seconds.");
			new BukkitRunnable() {
				@Override
				public void run() {
					confirm.remove(sender);
				}
			}.runTaskLater(GCore.inst(), 20L * 15L);
			return;
		}
		// get owners
		final List<OfflinePlayer> owners = new ArrayList<OfflinePlayer>();
		if (paramAllplayers.has(call)) {
			for (UUID uuid : GCore.inst().getData().getDataProfiles().getAll().keySet()) {
				OfflinePlayer player = Utils.getOfflinePlayer(uuid);
				if (player != null) owners.add(player);
			}
			if (owners.isEmpty()) {
				Messenger.send(call.getSender(), Messenger.Level.SEVERE_ERROR, GCore.inst().getName(), "No known player was found. Try waiting for a little while for users to initialize their default data profile, then re-use the command.");
				return;
			}
		} else {
			OfflinePlayer player = paramPlayer.getOfflinePlayer(call, true);
			if (player == null) return;
			owners.add(player);
		}
		// get NPCs
		final Set<Integer> npcsIds = new HashSet<Integer>();
		if (paramAllNpcs.has(call)) {
			for (int npcId : GCore.inst().getNpcManager().getNpcsData().keySet()) {
				npcsIds.add(npcId);
			}
			if (npcsIds.isEmpty()) {
				Messenger.send(call.getSender(), Messenger.Level.SEVERE_ERROR, GCore.inst().getName(), "No known NPC data was found. If you removed a NPC, you'll have to specify the NPC id with parameter <-npc:ID>.");
				return;
			}
		} else {
			Integer npcId = paramNpc.getInt(call);
			if (npcId == Integer.MIN_VALUE) return;
			npcsIds.add(npcId);
		}
		// operate on users
		Messenger.send(call.getSender(), Messenger.Level.NORMAL_SUCCESS, GCore.inst().getName(), "Starting to reset " + npcsIds.size() + " NPC" + Utils.getPlural(npcsIds.size()) + " for " + owners.size() + " known user" + Utils.getPlural(owners.size()) + " (processing 100 users per 5 seconds).");
		new BukkitRunnable() {
			private List<UUID> processing = new ArrayList<UUID>();
			private List<GUser> toPush = new ArrayList<GUser>();
			@Override
			public void run() {
				// push current users
				if (!toPush.isEmpty()) {
					GCore.inst().getData().getUsers().pushAsync(Utils.asList(toPush));
					Messenger.send(call.getSender(), Messenger.Level.NORMAL_SUCCESS, GCore.inst().getName(), "... pushed " + toPush.size() + " user" + Utils.getPlural(toPush.size()) + " after resetting " + npcsIds.size() + " NPC" + Utils.getPlural(npcsIds.size()) + ".");
					for (GUser user : toPush) {
						processing.remove(user.getInfo().getUniqueId());
					}
					toPush.clear();
				}
				// no more users to proceed
				if (owners.isEmpty() && toPush.isEmpty() && processing.isEmpty()) {
					Messenger.send(call.getSender(), Messenger.Level.NORMAL_SUCCESS, GCore.inst().getName(), "Reset is done.");
					cancel();
					return;
				}
				// process more users
				int limit = 100, i = 0;
				for (OfflinePlayer owner : Utils.asList(owners)) {
					processing.add(owner.getUniqueId());
					owners.remove(owner);
					operate(sender, owner, npcsIds, toPush);
					if (++i >= limit) {
						break;
					}
				}
			}
		}.runTaskTimer(GCore.inst(), 0L, owners.size() < 100 ? 20L : 20L * 5L);
	}

	private void operate(final CommandSender sender, final OfflinePlayer owner, final Set<Integer> npcsIds, final List<GUser> toPush) {
		new GUserOperator(new UserInfo(owner)) {
			@Override
			protected void process(GUser user) {
				// process NPCs
				int changed = 0;
				Player onlineOwner = owner.getPlayer();
				for (int npcId : npcsIds) {
					// remove npc data
					user.removeNpc(npcId, false);
					if (onlineOwner != null) {
						// despawn npc
						Npc npc = GCore.inst().getNpcManager().getNpc(onlineOwner, npcId);
						if (npc != null) {
							GCore.inst().getNpcManager().removeNpc(onlineOwner, npc);
						}
						// reinitialize default npc data
						changed += GCore.inst().getNpcManager().createDefaultNpcData(user, onlineOwner);
					}
				}
				// push user data later
				if (changed > 0) {
					toPush.add(user);
				}
			}
		}.operate();
	}

}
