package com.guillaumevdn.gcore.integration.holographicdisplays;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.integration.Integration;
import com.guillaumevdn.gcore.lib.integration.IntegrationInstance;

/**
 * @author GuillaumeVDN
 */
public class IntegrationInstanceHolographicDisplays extends IntegrationInstance {

	public IntegrationInstanceHolographicDisplays(Integration integration) {
		super(integration);
	}

	// ----- holograms
	private Map<String, Hologram> holograms = new HashMap<>();

	public void setTemporaryHologram(String id, Location location, Player player, List<String> lines, ItemStack item, int deleteInTicks) {
		setTemporaryHologram(id, location, CollectionUtils.asList(player), lines, item, deleteInTicks);
	}

	public void setTemporaryHologram(String id, Location location, List<Player> players, List<String> lines, ItemStack item, int deleteInTicks) {
		GCore.inst().operateSync(() -> {
			Hologram hologram = HologramsAPI.createHologram(GCore.inst(), location);
			lines.forEach(line -> hologram.appendTextLine(line));
			hologram.getVisibilityManager().setVisibleByDefault(false);
			players.forEach(player -> hologram.getVisibilityManager().showTo(player));
			if (item != null) {
				hologram.appendItemLine(item);
			}
			GCore.inst().operateSyncLater(() -> {
				if (!hologram.isDeleted()) {
					hologram.delete();
				}
			}, null, deleteInTicks);
		});
	}

	// ----- activation
	@Override
	public void deactivate() {
		GCore.inst().operateSync(() -> {
			holograms.forEach((id, hologram) -> {
				if (!hologram.isDeleted()) {
					hologram.delete();
				}
			});
			holograms.clear();
		});
	}

}
