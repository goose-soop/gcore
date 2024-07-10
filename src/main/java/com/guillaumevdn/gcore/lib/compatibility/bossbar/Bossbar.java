package com.guillaumevdn.gcore.lib.compatibility.bossbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

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

	public Bossbar(GPlugin plugin, String id, String title, BossbarColor color, BossbarStyle style, Collection<BossbarFlag> flags, double progress,
			Collection<Player> players) {
		this.plugin = plugin;
		this.id = id;
		this.title = title.length() > 64 ? title.substring(0, 64) : title;
		this.color = color;
		this.style = style;
		this.flags = flags == null ? new ArrayList<>(0) : flags;
		this.progress = !Double.isFinite(progress) ? 1d : Math.max(Math.min(progress, 1d), 0d);
		this.players = CollectionUtils.asSet(players);
	}

	// ----- get
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

	// ----- set
	public void setTitle(String title) {
		this.title = title;
		BossbarCompat.setTitle(this);
	}

	public void setProgress(double progress) {
		this.progress = !Double.isFinite(progress) ? 1d : Math.max(Math.min(progress, 1d), 0d);
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

	private void addPlayer(Player player) {
		if (players.add(player) && instances != null) {
			BossbarCompat.addPlayer(this, player);
		}
	}

	private boolean removePlayer(Player player) {
		boolean removed = players.remove(player);
		if (removed && instances != null) {
			BossbarCompat.removePlayer(this, player);
		}
		return removed;
	}

	// ----- instance
	private boolean active = false;
	private Map<Player, ReflectionObject> instances = null;

	public boolean isActive() {
		return active;
	}

	Map<Player, ReflectionObject> getInstances() {
		return instances;
	}

	public ReflectionObject getInstance(Player player) {
		return instances == null ? null : instances.get(player);
	}

	public void startTemp(int ticks, Double noAutoProgressForceProgress) {
		start();
		changeTemp(ticks, noAutoProgressForceProgress);
	}

	public void changeTemp(int ticks, Double noAutoProgressForceProgress) {
		if (noAutoProgressForceProgress != null) {
			setProgress(noAutoProgressForceProgress);
		}
		WrapperInteger remainingTicks = WrapperInteger.of(ticks);
		plugin.registerTask("bossbar_temp_" + id, true, 1, () -> {
			if (remainingTicks.alter(-1) <= 0) {
				stop();
			} else if (noAutoProgressForceProgress == null) {
				setProgress(remainingTicks.get().doubleValue() / ((double) ticks));
			}
		});
	}

	public void start() {
		active = true;
		if (instances != null)
			stop();
		plugin.registerBossbar(this);
		instances = new HashMap<>(1);
		players.forEach(player -> BossbarCompat.addPlayer(this, player));
	}

	public void stop() {
		active = false;
		if (instances != null) {
			players.forEach(player -> BossbarCompat.removePlayer(this, player));
			instances = null;
		}
		plugin.unregisterBossbar(this);
		plugin.stopTask("bossbar_temp_" + id);
	}

	// events are in GPlugin, inside a dedicated listener
	public void handleDisconnect(Player player) {
		removePlayer(player);
	}

	public void handleTeleport(Player player) {
		if (removePlayer(player)) {
			addPlayer(player);
		}
	}

}
