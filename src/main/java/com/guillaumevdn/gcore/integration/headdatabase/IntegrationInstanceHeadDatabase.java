package com.guillaumevdn.gcore.integration.headdatabase;

import javax.annotation.Nullable;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.integration.Integration;
import com.guillaumevdn.gcore.lib.integration.IntegrationInstance;

import me.arcaniax.hdb.api.HeadDatabaseAPI;

/**
 * @author GuillaumeVDN
 */
public class IntegrationInstanceHeadDatabase extends IntegrationInstance {

	private HeadDatabaseAPI api = null;

	public IntegrationInstanceHeadDatabase(Integration integration) {
		super(integration);
	}

	@Override
	public boolean activate() {
		api = new HeadDatabaseAPI();
		return true;
	}

	@Override
	public void deactivate() {
		api = null;
	}

	@Nullable
	public ItemStack getItemStack(String id) {
		try {
			return api.getItemHead(id);
		} catch (Throwable ignored) {
			return null;
		}
	}

}
