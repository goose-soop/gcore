package be.pyrrh4.pyrcore.lib.loadable.setting.complex;

import java.util.List;

import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.loadable.Loadable;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingString;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.messenger.Tab;

public class TabSetting extends Loadable<TabSetting> {

	// base
	public TabSetting(Loadable<?> parent, String id, boolean mandatory, Mat icon, List<String> description) {
		super(parent, id, mandatory, icon, description);
		registerSetting(new SettingString("header", "QuestCreator is like the best questing plugin", false, PCLocale.GUI_GENERIC_EDITOR_TAB_HEADERLORE.getLines()));
		registerSetting(new SettingString("footer", "Honestly, just take a look ! #selfadINSIDEtheproduct :D", false, PCLocale.GUI_GENERIC_EDITOR_TAB_FOOTERLORE.getLines()));
	}

	// methods
	public Tab get(Player parsingPlayer) {
		String header = getSettingString("header").getParsed(parsingPlayer);
		String footer = getSettingString("footer").getParsed(parsingPlayer);
		return new Tab(header, footer);
	}

}
