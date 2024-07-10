package com.guillaumevdn.gcore.command;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.guillaumevdn.gcore.PermissionGCore;
import com.guillaumevdn.gcore.TextGCore;
import com.guillaumevdn.gcore.lib.chat.JsonMessage;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Subcommand;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public final class GcoreItemChatHover extends Subcommand {

    public GcoreItemChatHover() {
        super(true, PermissionGCore.inst().gcoreAdmin, Text.of("show a json hover message of this item"), CollectionUtils.asList("chathover"));
    }

    @Override
    public void perform(CommandCall call) {
        final Player sender = call.getSenderPlayer();
        final ItemStack item = sender.getItemInHand();
        if (Mat.isVoid(item)) {
            TextGCore.messageItemReadNull.send(sender);
        } else {
            final JsonMessage json = new JsonMessage();
            final ItemMeta meta = item.getItemMeta();
            final String name = meta != null && meta.hasDisplayName() ? meta.getDisplayName()
                    : StringUtils.separateAtUnderscore(item.getType().toString()).toLowerCase();
            json.append(name).setHover(item).build();
            json.send(sender);
        }
    }

}
