package com.guillaumevdn.gcore.integration.placeholderapi;

import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.integration.Integration;
import com.guillaumevdn.gcore.lib.integration.IntegrationInstance;

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
			expansion.unregister();
			expansion = null;
		});
	}

}
