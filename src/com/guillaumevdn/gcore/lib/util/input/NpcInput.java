package com.guillaumevdn.gcore.lib.util.input;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.npc.Npc;

public interface NpcInput {

	// methods
    void onChoose(Player player, Npc value);

}
