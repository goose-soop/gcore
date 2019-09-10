package com.guillaumevdn.gcore.lib.util.input;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ItemInput {

	// methods
    void onChoose(Player player, ItemStack value);

}
