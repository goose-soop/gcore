package com.guillaumevdn.gcore.integration.placeholderapi;

import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.integration.Integration;
import com.guillaumevdn.gcore.lib.integration.IntegrationInstance;

import me.clip.placeholderapi.PlaceholderAPIPlugin;

/**
 * @author GuillaumeVDN
 */
public class IntegrationInstancePlaceholderAPI extends IntegrationInstance {

	public IntegrationInstancePlaceholderAPI(Integration integration) {
		super(integration);
	}

	private PlaceholderExpansionGCore expansion;

	// activation
	@Override
	public boolean activate() {
		BukkitThread.SYNC.operate(() -> {
			expansion = new PlaceholderExpansionGCore();
			expansion.register();
		});
		return true;
	}

	@Override
	public void deactivate() {
		BukkitThread.SYNC.operate(() -> {
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

}
