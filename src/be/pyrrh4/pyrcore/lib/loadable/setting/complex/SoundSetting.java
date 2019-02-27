package be.pyrrh4.pyrcore.lib.loadable.setting.complex;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.configuration.YMLConfiguration;
import be.pyrrh4.pyrcore.lib.loadable.Loadable;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingEnum;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingFloat;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.util.Utils;
import be.pyrrh4.pyrcore.lib.versioncompat.sound.Sound;

public class SoundSetting extends Loadable<SoundSetting> implements Cloneable {

	// base
	public SoundSetting(Loadable<?> parent, String id, boolean mandatory, Mat icon, List<String> description) {
		super(parent, id, mandatory, icon, description);
		registerSetting(new SettingEnum<Sound>("type", null, false, Sound.class, PCLocale.GUI_GENERIC_EDITOR_SOUND_TYPELORE.getLines()));
		registerSetting(new SettingFloat("volume", "1", false, PCLocale.GUI_GENERIC_EDITOR_SOUND_VOLUMELORE.getLines()));
		registerSetting(new SettingFloat("pitch", "1", false, PCLocale.GUI_GENERIC_EDITOR_SOUND_PITCH.getLines()));
	}

	// overriden methods
	@Override
	public SoundSetting clone() {
		SoundSetting clone = new SoundSetting(loadParent(), getId(), loadMandatory(), loadIcon(), Utils.asList(loadDescription()));
		clone.getSettingEnum("type", Sound.class).setValue(getSettingEnum("type", Sound.class).getValue());
		clone.getSettingFloat("volume").setValue(getSettingFloat("volume").getValue());
		clone.getSettingFloat("pitch").setValue(getSettingFloat("pitch").getValue());
		return clone;
	}

	@Override
	public void loadSettings(YMLConfiguration config, String configRoot) {
		if (!config.contains(configRoot) || config.isConfigurationSection(configRoot)) {
			super.loadSettings(config, configRoot);
		} else {
			getSettingEnum("type", Sound.class).setValue(config.getString(configRoot, null));
		}
	}

	/** Copy settings to the specified object */
	public void copySettings(SoundSetting other) {
		other.getSettingEnum("type", Sound.class).setValue(getSettingEnum("type", Sound.class).getValue());
		other.getSettingFloat("volume").setValue(getSettingFloat("volume").getValue());
		other.getSettingFloat("pitch").setValue(getSettingFloat("pitch").getValue());
	}

	// methods
	public boolean isValid() {
		return getSettingEnum("type", Sound.class).getValue() != null;
	}

	public void play(Player basePlayer, List<Player> players) {
		Sound type = getSettingEnum("type", Sound.class).getParsed(basePlayer);
		float volume = getSettingFloat("volume").getParsed(basePlayer);
		float pitch = getSettingFloat("pitch").getParsed(basePlayer);
		if (type != null) {
			type.play(players, volume, pitch);
		}
	}

	public void play(Player player) {
		Sound type = getSettingEnum("type", Sound.class).getParsed(player);
		float volume = getSettingFloat("volume").getParsed(player);
		float pitch = getSettingFloat("pitch").getParsed(player);
		if (type != null) {
			type.play(player, volume, pitch);
		}
	}

	public void play(Location location, Player player) {
		Sound type = getSettingEnum("type", Sound.class).getParsed(player);
		float volume = getSettingFloat("volume").getParsed(player);
		float pitch = getSettingFloat("pitch").getParsed(player);
		if (type != null) {
			type.play(location, volume, pitch);
		}
	}

}
