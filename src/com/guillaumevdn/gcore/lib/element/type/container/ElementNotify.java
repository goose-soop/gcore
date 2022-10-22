package com.guillaumevdn.gcore.lib.element.type.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Compat;
import com.guillaumevdn.gcore.lib.compatibility.bossbar.Bossbar;
import com.guillaumevdn.gcore.lib.compatibility.bossbar.BossbarColor;
import com.guillaumevdn.gcore.lib.compatibility.bossbar.BossbarCompat;
import com.guillaumevdn.gcore.lib.compatibility.bossbar.BossbarFlag;
import com.guillaumevdn.gcore.lib.compatibility.bossbar.BossbarStyle;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.compatibility.sound.Sound;
import com.guillaumevdn.gcore.lib.concurrency.RWHashMap;
import com.guillaumevdn.gcore.lib.element.editor.SlotPlacement;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.container.ContainerElement;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementBossbarColor;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementBossbarFlagList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementBossbarStyle;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementDouble;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementSound;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementString;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementText;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;
import com.guillaumevdn.gcore.lib.time.TimeUnit;
import com.guillaumevdn.gcore.lib.time.duration.ElementDuration;

/**
 * @author GuillaumeVDN
 */

public class ElementNotify extends ContainerElement {

	private ElementText message = addText("message", Need.optional(), TextEditorGeneric.descriptionNotifyMessage);
	private ElementString actionbar = addString("actionbar", Need.optional(), TextEditorGeneric.descriptionNotifyActionbar);
	private ElementDuration actionbarDuration = addDuration("actionbar_duration", Need.optional(), 10, TimeUnit.SECOND, TextEditorGeneric.descriptionNotifyActionbarDuration);

	private ElementString bossbar = addString("bossbar", Need.optional(), SlotPlacement.START_ROW, TextEditorGeneric.descriptionNotifyBossbar);
	private ElementBossbarColor bossbarColor = addBossbarColor("bossbar_color", Need.optional(BossbarColor.BLUE), TextEditorGeneric.descriptionNotifyBossbarColor);
	private ElementBossbarStyle bossbarStyle = addBossbarStyle("bossbar_style", Need.optional(BossbarStyle.SOLID), TextEditorGeneric.descriptionNotifyBossbarStyle);
	private ElementBossbarFlagList bossbarFlags = addBossbarFlagList("bossbar_flags", Need.optional(new ArrayList<>()), TextEditorGeneric.descriptionNotifyBossbarFlags);
	private ElementDuration bossbarDuration = addDuration("bossbar_duration", Need.optional(), 10, TimeUnit.SECOND, TextEditorGeneric.descriptionNotifyBossbarDuration);

	private ElementString title = addString("title", Need.optional(), SlotPlacement.START_ROW, TextEditorGeneric.descriptionNotifyTitle);
	private ElementString titleSubtitle = addString("title_subtitle", Need.optional(), TextEditorGeneric.descriptionNotifyTitleSubtitle);
	private ElementDuration titleFadeIn = addDuration("title_fade_in", Need.optional(), 10, TimeUnit.TICK, TextEditorGeneric.descriptionNotifyTitleFadeIn);
	private ElementDuration titleDuration = addDuration("title_duration", Need.optional(), 4, TimeUnit.SECOND, TextEditorGeneric.descriptionNotifyTitleDuration);
	private ElementDuration titleFadeOut = addDuration("title_fade_out", Need.optional(), 10, TimeUnit.TICK, TextEditorGeneric.descriptionNotifyTitleFadeOut);

	private ElementSound sound = addSound("sound", Need.optional(), SlotPlacement.START_ROW, TextEditorGeneric.descriptionNotifySound);
	private ElementDouble soundVolume = addDouble("sound_volume", Need.optional(1d), TextEditorGeneric.descriptionNotifySoundVolume);
	private ElementDouble soundPitch = addDouble("sound_pitch", Need.optional(1d), TextEditorGeneric.descriptionNotifySoundPitch);

	public ElementNotify(Element parent, String id, Need need, Text editorDescription) {
		super(parent, id, need, editorDescription);
	}

	// ----- get
	public ElementText getMessage() {
		return message;
	}

	public ElementString getActionbar() {
		return actionbar;
	}

	public ElementDuration getActionbarDuration() {
		return actionbarDuration;
	}

	public ElementString getBossbar() {
		return bossbar;
	}

	public ElementBossbarColor getBossbarColor() {
		return bossbarColor;
	}

	public ElementBossbarStyle getBossbarStyle() {
		return bossbarStyle;
	}

	public ElementBossbarFlagList getBossbarFlags() {
		return bossbarFlags;
	}

	public ElementDuration getBossbarDuration() {
		return bossbarDuration;
	}

	public ElementString getTitle() {
		return title;
	}

	public ElementString getTitleSubtitle() {
		return titleSubtitle;
	}

	public ElementDuration getTitleFadeIn() {
		return titleFadeIn;
	}

	public ElementDuration getTitleDuration() {
		return titleDuration;
	}

	public ElementDuration getTitleFadeOut() {
		return titleFadeOut;
	}

	public ElementSound getSound() {
		return sound;
	}

	public ElementDouble getSoundVolume() {
		return soundVolume;
	}

	public ElementDouble getSoundPitch() {
		return soundPitch;
	}

	public boolean isNotifyEmpty() {
		return !Stream.of(actionbar, bossbar, message, title, titleSubtitle, sound).anyMatch(elem -> elem.readContains());
	}

	// ----- methods
	private RWHashMap<Player, BukkitTask> lastActionbarTasks = new RWHashMap<>(5, 1f);
	private RWHashMap<Player, Bossbar> lastBossbars = new RWHashMap<>(5, 1f);

	public void stopLastActionbar(Player player) {
		BukkitTask task = lastActionbarTasks.remove(player);
		if (task != null) {
			task.cancel();
		}
	}

	public void stopLastBossbar(Player player) {
		Bossbar bossbar = lastBossbars.remove(player);
		if (bossbar != null) {
			bossbar.stop();
		}
	}

	public void sendAll(Player player, Replacer replacer) {
		sendAll(player, replacer, null);
	}

	public void sendAll(Player player, Replacer replacer, @Nullable Location soundLocation) {
		sendAll(CollectionUtils.asList(player), replacer);
	}

	public void sendAll(Collection<Player> players, Replacer replacer) {
		sendAll(players, replacer, null);
	}

	public void sendAll(Collection<Player> players, Replacer replacer, @Nullable Location soundLocation) {
		sendAll(players, replacer, soundLocation, null);
	}

	public void sendAll(Collection<Player> players, Replacer replacer, @Nullable Location soundLocation, @Nullable Double forceBossbarProgress) {
		if (!readContains()) return;
		sendMessage(players, replacer);
		sendActionbar(players, replacer);
		sendBossbar(players, replacer, null, forceBossbarProgress, null);
		sendTitle(players, replacer);
		playSound(players, replacer, soundLocation);
	}

	public void sendMessage(Collection<Player> players, Replacer replacer) {
		message.parse(replacer).ifPresentDo(text -> text.send(players));
	}

	public void sendActionbar(Collection<Player> players, Replacer replacer) {
		sendActionbar(players, replacer, null);
	}

	public void sendActionbar(Collection<Player> players, Replacer replacer, Integer forceDurationTicks) {
		sendActionbar(players, replacer, forceDurationTicks, true);
	}

	public void sendActionbar(Collection<Player> players, Replacer replacer, Integer forceDurationTicks, boolean allowLoop) {
		actionbar.parse(replacer).ifPresentDo(actionbar -> {
			int durationTicks = forceDurationTicks != null ? forceDurationTicks : (int) (actionbarDuration.parse(replacer).orElse(1000L) / 50L);
			players.forEach(player -> {
				// send actionbar
				Compat.sendActionbar(player, actionbar);
				// start task if needed
				if (allowLoop) {
					stopLastActionbar(player);
					if (durationTicks > 50) {
						lastActionbarTasks.put(player, new BukkitRunnable() {  // use a bukkit task here so it can be cancelled easily too
							private int remainingTicks = durationTicks;
							private int ticksBeforeUpdate = 50;
							@Override
							public void run() {
								if (--remainingTicks <= 0) {
									cancel();
									lastActionbarTasks.remove(player);
									Compat.sendActionbar(player, "");
									return;
								}
								if (--ticksBeforeUpdate <= 0) {
									Compat.sendActionbar(player, actionbar);
									ticksBeforeUpdate = 50;
								}
							}
						}.runTaskTimerAsynchronously(GCore.inst(), 1L, 1L));
					}
				}
			});
		});
	}

	public void sendBossbar(Collection<Player> players, Replacer replacer) {
		sendBossbar(players, replacer, null, null, null);
	}

	public void sendBossbar(Collection<Player> players, Replacer replacer, Double noAutoProgressForceProgress) {
		sendBossbar(players, replacer, null, noAutoProgressForceProgress, null);
	}

	public void sendBossbar(Collection<Player> players, Replacer replacer, Long forceDurationMillis, Double forceProgress, BossbarColor forceColor) {
		//Bukkit.getLogger().info("[debug WarnD] ------------ BEGIN");

		directParseAndIfPresentDo("bossbar", "bossbar_color", "bossbar_style", "bossbar_flags", replacer, (String bossbar, BossbarColor color, BossbarStyle style, List<BossbarFlag> flags) -> {

			//Bukkit.getLogger().info("[debug WarnD] parsed " + bossbar + ", in config " + getElementAs("bossbar", ElementString.class).getRawValue());

			players.forEach(player -> {
				Long bossbarDurationMillis = forceDurationMillis != null ? forceDurationMillis : this.bossbarDuration.parse(replacer).orNull();

				// already active, simply update parameters to avoid the ugly unregister/register animation
				Bossbar instance = lastBossbars.remove(player);
				if (instance != null && instance.isActive()) {
					instance.setTitle(bossbar);
					instance.setColor(forceColor != null ? forceColor : color);
					instance.setStyle(style);
					instance.setFlags(flags);
					if (bossbarDurationMillis != null) {
						int ticks = (int) (bossbarDurationMillis / 50L);
						instance.changeTemp(ticks, forceProgress);
					} else {
						instance.setProgress(forceProgress != null ? forceProgress : 1f);
					}
					//Bukkit.getLogger().info("[debug WarnD] sending bossbar (case 1) : '" + bossbar + "'");
				}
				// not active, create new
				else {
					if (bossbarDurationMillis != null) {
						instance = BossbarCompat.sendTemp(getSuperElement().getPlugin(), bossbar, forceColor != null ? forceColor : color, style, flags, CollectionUtils.asList(player), bossbarDurationMillis, forceProgress);
						instance.setTitle(bossbar);  // immediately re-send title, otherwise hex codes will not display properly when there are multiple boss bars on the screen
						//Bukkit.getLogger().info("[debug WarnD] sending bossbar (case 2) : '" + bossbar + "'");
					} else {
						instance = new Bossbar(getSuperElement().getPlugin(), "notify_" + UUID.randomUUID(), bossbar, forceColor != null ? forceColor : color, style, flags, forceProgress != null ? forceProgress : 1f, CollectionUtils.asList(player));
						instance.start();
						//Bukkit.getLogger().info("[debug WarnD] sending bossbar (case 3) : '" + bossbar + "'");
					}
				}
				lastBossbars.put(player, instance);

				//Bukkit.getLogger().info("[debug WarnD] END ------------");
			});
		});
	}

	public void sendTitle(Collection<Player> players, Replacer replacer) {
		sendTitle(players, replacer, null, null, null);
	}

	public void sendTitle(Collection<Player> players, Replacer replacer, Integer forceFadeInTicks, Integer forceDurationTicks, Integer forceFadeOutTicks) {
		String title = this.title.parse(replacer).orNull();
		String subtitle = this.titleSubtitle.parse(replacer).orNull();
		if (title != null || subtitle != null) {
			int fadeIn = forceFadeInTicks != null ? forceFadeInTicks : titleFadeIn.parse(replacer).orElse(0L).intValue() / 50;
			int duration = forceDurationTicks != null ? forceDurationTicks : titleDuration.parse(replacer).orElse(20L).intValue() / 50;
			int fadeOut = forceFadeOutTicks != null ? forceFadeOutTicks : titleFadeOut.parse(replacer).orElse(0L).intValue() / 50;
			players.forEach(player -> Compat.sendTitle(player, title, subtitle, fadeIn, duration, fadeOut));
		}
	}

	public void playSound(Collection<Player> players, Replacer replacer) {
		playSound(players, replacer, null);
	}

	public void playSound(Collection<Player> players, Replacer replacer, @Nullable Location soundLocation) {
		directParseAndIfPresentDo("sound", "sound_volume", "sound_pitch", replacer, (Sound sound, Double volume, Double pitch) -> {
			sound.play(players, volume.floatValue(), pitch.floatValue(), soundLocation);
		});
	}

	// ----- editor
	@Override
	public Mat editorIconType() {
		return CommonMats.REPEATER;
	}

	@Override
	public List<String> editorCurrentValue() {
		List<String> desc = new ArrayList<>();
		message.parseGeneric().ifPresentDo(message -> desc.add("message : " + message));
		bossbar.parseGeneric().ifPresentDo(bossbar -> desc.add("bossbar : " + bossbar));
		title.parseGeneric().ifPresentDo(title -> desc.add("title : " + title));
		titleSubtitle.parseGeneric().ifPresentDo(titleSubtitle -> desc.add("subtitle : " + titleSubtitle));
		sound.parseGeneric().ifPresentDo(sound -> desc.add("sound : " + sound));
		return !desc.isEmpty() ? desc : null;
	}

}
