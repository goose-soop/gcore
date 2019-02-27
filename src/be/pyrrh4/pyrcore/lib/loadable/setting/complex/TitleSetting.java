package be.pyrrh4.pyrcore.lib.loadable.setting.complex;

import java.util.List;

import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.loadable.Loadable;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingInteger;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingString;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.messenger.Title;

public class TitleSetting extends Loadable<TitleSetting> {

	// base
	public TitleSetting(Loadable<?> parent, String id, boolean mandatory, Mat icon, List<String> description) {
		super(parent, id, mandatory, icon, description);
		registerSetting(new SettingString("title", "Fresh Avocado", false, PCLocale.GUI_GENERIC_EDITOR_TITLE_TITLELORE.getLines()));
		registerSetting(new SettingString("subtitle", "Fresh a voca doooo !", false, PCLocale.GUI_GENERIC_EDITOR_TITLE_SUBTITLELORE.getLines()));
		registerSetting(new SettingInteger("fade_in", "5", false, PCLocale.GUI_GENERIC_EDITOR_TITLE_FADEINLORE.getLines()));
		registerSetting(new SettingInteger("duration", "50", false, PCLocale.GUI_GENERIC_EDITOR_TITLE_DURATIONLORE.getLines()));
		registerSetting(new SettingInteger("fade_out", "5", false, PCLocale.GUI_GENERIC_EDITOR_TITLE_FADEOUTLORE.getLines()));
	}

	// methods
	public Title get(Player parsingPlayer) {
		String title = getSettingString("title").getParsed(parsingPlayer);
		String subtitle = getSettingString("subtitle").getParsed(parsingPlayer);
		int fadeIn = getSettingInteger("fade_in").getParsed(parsingPlayer);
		int duration = getSettingInteger("duration").getParsed(parsingPlayer);
		int fadeOut = getSettingInteger("fade_out").getParsed(parsingPlayer);
		return new Title(title, subtitle, fadeIn, duration, fadeOut);
	}

}
