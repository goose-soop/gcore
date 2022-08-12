package com.guillaumevdn.gcore.integration.placeholderapi;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.integration.Integration;
import com.guillaumevdn.gcore.lib.integration.IntegrationInstance;
import com.guillaumevdn.gcore.lib.string.StringUtils;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;

/**
 * @author GuillaumeVDN
 */
public class IntegrationInstancePlaceholderAPI extends IntegrationInstance {

	public IntegrationInstancePlaceholderAPI(Integration integration) {
		super(integration);
	}

	private PlaceholderExpansionGCore expansion;

	// ----- activation
	@Override
	public boolean activate() {
		GCore.inst().operateSync(() -> {
			expansion = new PlaceholderExpansionGCore();
			expansion.register();
		});
		return true;
	}

	@Override
	public void deactivate() {
		GCore.inst().operateSync(() -> {
			try {
				try {
					expansion.unregister();
				} catch (Throwable ignored) {
					PlaceholderAPIPlugin.getInstance().getLocalExpansionManager().unregister(expansion);  // it seems to bug sometimes for some reason if we use expansion.unregister() directly
				}
			} catch (Throwable ignored) {}  // come on man
			expansion = null;
		});
	}

	// ----- utils
	public String parse(Player player, String string) {
		return StringUtils.format(PlaceholderAPI.setPlaceholders(player, string));
	}

}
