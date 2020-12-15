package com.guillaumevdn.gcore.lib.compatibility.bossbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;
import com.guillaumevdn.gcore.lib.wrapper.WrapperInteger;

/**
 * @author GuillaumeVDN
 */
public final class Bossbar {

	private GPlugin plugin;
	private String id;
	private String title;
	private BossbarColor color;
	private BossbarStyle style;
	private Collection<BossbarFlag> flags;
	private double progress;
	private Set<Player> players;

	public Bossbar(GPlugin plugin, String id, String title, BossbarColor color, BossbarStyle style, Collection<BossbarFlag> flags, double progress, Collection<Player> players) {
		this.plugin = plugin;
		this.id = id;
		this.title = title.length() > 64 ? title.substring(0, 64) : title;
		this.color = color;
		this.style = style;
		this.flags = flags == null ? new ArrayList<>() : flags;
		this.players = CollectionUtils.asSet(players);
	}

	// get
	public GPlugin getPlugin() {
		return plugin;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public BossbarColor getColor() {
		return color;
	}

	public BossbarStyle getStyle() {
		return style;
	}

	public Collection<BossbarFlag> getFlags() {
		return flags;
	}

	public double getProgress() {
		return progress;
	}

	public Set<Player> getPlayers() {
		return Collections.unmodifiableSet(players);
	}

	// set
	public void setTitle(String title) {
		this.title = title;
		BossbarCompat.setColor(this);
	}

	public void setProgress(double progress) {
		this.progress = progress;
		BossbarCompat.setProgress(this);
	}

	public void setColor(BossbarColor color) {
		this.color = color;
		BossbarCompat.setColor(this);
	}

	public void setStyle(BossbarStyle style) {
		this.style = style;
		BossbarCompat.setStyle(this);
	}

	public void setFlags(Collection<BossbarFlag> flags) {
		this.flags = flags;
		BossbarCompat.setFlags(this);
	}

	public void addPlayer(Player player) {
		if (players.add(player) && instances != null) {
			BossbarCompat.addPlayer(this, player);
		}
	}

	public void removePlayer(Player player) {
		if (players.remove(player) && instances != null) {
			BossbarCompat.removePlayer(this, player);
		}
	}

	// instance
	private Map<Player, ReflectionObject> instances = null;

	Map<Player, ReflectionObject> getInstances() {
		return instances;
	}

	public ReflectionObject getInstance(Player player) {
		return instances == null ? null : instances.get(player);
	}

	public void startTempAutoProgress(int ticks, boolean noAutoProgress) {
		start();
		if (noAutoProgress) {
			setProgress(1d);
		}
		WrapperInteger remainingTicks = WrapperInteger.of(ticks);
		plugin.registerTask("bossbar_temp_autoprogress_" + id, true, 1, () -> {
			if (remainingTicks.alter(-1) <= 0) {
				stop();
			} else if (!noAutoProgress) {
				setProgress(remainingTicks.get().doubleValue() / ((double) ticks));
			}
		});
	}

	public void start() {
		if (instances != null) {
			stop();
		}
		plugin.registerBossbar(this);
		instances = new HashMap<>();
		players.forEach(player -> BossbarCompat.addPlayer(this, player));
		plugin.registerListener("bossbar_" + id, new Listener() {
			@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
			public void event(PlayerQuitEvent event) {
				removePlayer(event.getPlayer());
			}
			@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
			public void event(PlayerTeleportEvent event) {
				handleTeleport(event.getPlayer());
			}
			@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
			public void event(PlayerRespawnEvent event) {
				handleTeleport(event.getPlayer());
			}
			private void handleTeleport(Player player) {
				removePlayer(player);
				addPlayer(player);
			}
		});
	}

	public void stop() {
		if (instances != null) {
			players.forEach(player -> BossbarCompat.removePlayer(this, player));
			instances = null;
		}
		plugin.unregisterBossbar(this);
		plugin.stopListener("bossbar_" + id);
		plugin.stopTask("bossbar_temp_autoprogress_" + id);
	}

}
