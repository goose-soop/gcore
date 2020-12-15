package com.guillaumevdn.gcore.lib.compatibility.bossbar;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.function.ThrowableConsumer;
import com.guillaumevdn.gcore.lib.reflection.Reflection;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;
import com.guillaumevdn.gcore.lib.reflection.procedure.ReflectionProcedureBiConsumer;
import com.guillaumevdn.gcore.lib.reflection.procedure.ReflectionProcedureConsumer;

/**
 * @author GuillaumeVDN
 */
public final class BossbarCompat {

	// get or create modern instance
	private static ReflectionObject getModernInstance(Bossbar bossbar, Player player) throws Throwable {
		ReflectionObject instance = bossbar.getInstance(null);
		if (instance == null) {
			org.bukkit.boss.BarColor color = org.bukkit.boss.BarColor.valueOf(bossbar.getColor().name());
			org.bukkit.boss.BarStyle style = org.bukkit.boss.BarStyle.valueOf(bossbar.getStyle().name());
			org.bukkit.boss.BarFlag[] flags = new org.bukkit.boss.BarFlag[bossbar.getFlags().size()];
			if (flags.length != 0) {
				Iterator<BossbarFlag> iterator = bossbar.getFlags().iterator();
				int i = -1;
				while (iterator.hasNext()) {
					flags[++i] = org.bukkit.boss.BarFlag.valueOf(iterator.next().name());
				}
			}
			instance = ReflectionObject.of(Bukkit.createBossBar(bossbar.getTitle(), color, style, flags));
			instance.invokeMethod("setVisible", true);
			instance.invokeMethod("setProgress", bossbar.getProgress());
			bossbar.getInstances().put(null, instance);
		}
		return instance;
	}

	private static void forExistingModernInstance(Bossbar bossbar, ThrowableConsumer<ReflectionObject> ifPresent) throws Throwable {
		ReflectionObject instance = bossbar.getInstance(null);
		if (instance != null) {
			ifPresent.accept(instance);
		}
	}

	private static void refreshExistingLegacyInstance(Bossbar bossbar) throws Throwable {
		if (bossbar.getInstances() != null) {
			for (ReflectionObject instance : bossbar.getInstances().values()) {
				FakeDragon dragon = instance.get(FakeDragon.class);
				dragon.sendDestroy();
				dragon.sendSpawn();
			}
		}
	}

	// add player
	private static final ReflectionProcedureBiConsumer<Bossbar, Player> ADD_PLAYER = new ReflectionProcedureBiConsumer<Bossbar, Player>()
			.set((bossbar, player) -> {
				FakeDragon instance = new FakeDragon(bossbar, player);
				instance.sendSpawn();
				bossbar.getInstances().put(player, ReflectionObject.of(instance));
			})
			.setIf(Version.ATLEAST_1_9, (bossbar, player) -> {
				getModernInstance(bossbar, player).invokeMethod("addPlayer", player);
			});

	public static void addPlayer(Bossbar bossbar, Player player) {
		ADD_PLAYER.process(bossbar, player);
	}

	// remove player
	private static final ReflectionProcedureBiConsumer<Bossbar, Player> REMOVE_PLAYER = new ReflectionProcedureBiConsumer<Bossbar, Player>()
			.set((bossbar, player) -> {
				ReflectionObject instance = bossbar.getInstances().remove(player);
				if (instance != null) {
					instance.get(FakeDragon.class).sendDestroy();
				}
			})
			.setIf(Version.ATLEAST_1_9, (bossbar, player) -> {
				forExistingModernInstance(bossbar, instance -> {
					instance.invokeMethod("removePlayer", player);
				});
			});

	public static void removePlayer(Bossbar bossbar, Player player) {
		REMOVE_PLAYER.process(bossbar, player);
	}

	// set title
	private static final ReflectionProcedureConsumer<Bossbar> SET_TITLE = new ReflectionProcedureConsumer<Bossbar>()
			.set((bossbar) -> {
				refreshExistingLegacyInstance(bossbar);
			})
			.setIf(Version.ATLEAST_1_9, (bossbar) -> {
				forExistingModernInstance(bossbar, instance -> {
					instance.invokeMethod("setTitle", bossbar.getTitle());
				});
			});

	public static void setTitle(Bossbar bossbar) {
		SET_TITLE.process(bossbar);
	}

	// set progress
	private static final ReflectionProcedureConsumer<Bossbar> SET_PROGRESS = new ReflectionProcedureConsumer<Bossbar>()
			.set((bossbar) -> {
				refreshExistingLegacyInstance(bossbar);
			})
			.setIf(Version.ATLEAST_1_9, (bossbar) -> {
				forExistingModernInstance(bossbar, instance -> {
					instance.invokeMethod("setProgress", bossbar.getProgress());
				});
			});

	public static void setProgress(Bossbar bossbar) {
		SET_PROGRESS.process(bossbar);
	}

	// set color
	private static final ReflectionProcedureConsumer<Bossbar> SET_COLOR = new ReflectionProcedureConsumer<Bossbar>()
			.setIf(Version.ATLEAST_1_9, (bossbar) -> {
				forExistingModernInstance(bossbar, instance -> {
					Object style = Reflection.getEnum("org.bukkit.boss.BarColor").valueOf(bossbar.getColor().name());
					instance.invokeMethod("setColor", style);
				});
			});

	public static void setColor(Bossbar bossbar) {
		SET_COLOR.process(bossbar);
	}

	// set style
	private static final ReflectionProcedureConsumer<Bossbar> SET_STYLE = new ReflectionProcedureConsumer<Bossbar>()
			.setIf(Version.ATLEAST_1_9, (bossbar) -> {
				forExistingModernInstance(bossbar, instance -> {
					Object style = Reflection.getEnum("org.bukkit.boss.BarStyle").valueOf(bossbar.getStyle().name());
					instance.invokeMethod("setStyle", style);
				});
			});

	public static void setStyle(Bossbar bossbar) {
		SET_STYLE.process(bossbar);
	}

	// set flags
	private static final ReflectionProcedureConsumer<Bossbar> SET_FLAGS = new ReflectionProcedureConsumer<Bossbar>()
			.setIf(Version.ATLEAST_1_9, (bossbar) -> {
				forExistingModernInstance(bossbar, instance -> {
					for (BossbarFlag bossbarFlag : BossbarFlag.values()) {
						Object flag = Reflection.getEnum("org.bukkit.boss.BarFlag").valueOf(bossbarFlag.name());
						instance.invokeMethod(bossbar.getFlags().contains(bossbarFlag) ? "addFlag" : "removeFlag", flag);
					}
				});
			});

	public static void setFlags(Bossbar bossbar) {
		SET_FLAGS.process(bossbar);
	}

	// send temporary
	public static Bossbar sendTemp(GPlugin plugin, String title, BossbarColor color, BossbarStyle style, Collection<BossbarFlag> flags, Collection<Player> players, long millis, boolean noAutoProgress) {
		int ticks = (int) (millis / 50L);
		return ticks <= 0 ? null : sendTemp(plugin, title, color, style, flags, players, ticks, noAutoProgress);
	}

	public static Bossbar sendTemp(GPlugin plugin, String title, BossbarColor color, BossbarStyle style, Collection<BossbarFlag> flags, Collection<Player> players, int ticks, boolean noAutoProgress) {
		Bossbar bossbar = new Bossbar(plugin, "temp_" + UUID.randomUUID(), title, color, style, flags, 1f, players);
		bossbar.startTempAutoProgress(ticks, noAutoProgress);
		return bossbar;
	}

}
