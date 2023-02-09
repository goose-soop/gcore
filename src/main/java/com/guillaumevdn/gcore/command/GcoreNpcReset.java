package com.guillaumevdn.gcore.command;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.PermissionGCore;
import com.guillaumevdn.gcore.TextGeneric;
import com.guillaumevdn.gcore.data.usernpcs.BoardUsersNPCs;
import com.guillaumevdn.gcore.data.usernpcs.UserNPCs;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Subcommand;
import com.guillaumevdn.gcore.lib.command.argument.ArgumentLegacyNPC;
import com.guillaumevdn.gcore.lib.command.argument.ArgumentPlayer;
import com.guillaumevdn.gcore.lib.legacy_npc.ElementNPC;
import com.guillaumevdn.gcore.lib.legacy_npc.NPC;
import com.guillaumevdn.gcore.lib.legacy_npc.NPCManager;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.string.TextElement;

/**
 * @author GuillaumeVDN
 */
public final class GcoreNpcReset extends Subcommand {

	private ArgumentPlayer argumentTarget = addArgumentPlayer(NeedType.REQUIRED, false, null, TextGeneric.commandParameterUsageTarget, true);
	private ArgumentLegacyNPC argumentNpc = addArgument(new ArgumentLegacyNPC(NeedType.REQUIRED, false, null, new TextElement("gcore npc")));

	public GcoreNpcReset() {
		super(false, PermissionGCore.inst().gcoreAdmin, new TextElement("To reset a GCore NPC"), CollectionUtils.asList("npcreset"));
	}

	@Override
	public void perform(CommandCall call) {
		NPCManager.ifPresent(manager -> {
			Player target = argumentTarget.get(call);
			UserNPCs user = UserNPCs.get(target);
			if (user != null) {
				ElementNPC npcConfig = argumentNpc.get(call);
				int id = Integer.parseInt(npcConfig.getId());
				user.removeNpc(id);
				NPC npc = manager.getNpc(target, id);
				if (npc != null) {
					manager.removeNpc(target, npc);
				}
				BoardUsersNPCs.inst().createAndSpawnDefault(target, user, npcConfig);
				call.getSender().sendMessage("§aReset NPC " + id + " for " + target.getName());
			}
		});
	}

}
