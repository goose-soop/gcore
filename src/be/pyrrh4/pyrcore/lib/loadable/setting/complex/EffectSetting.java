package be.pyrrh4.pyrcore.lib.loadable.setting.complex;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.loadable.Loadable;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingInteger;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingPotionEffectType;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class EffectSetting extends Loadable<EffectSetting> implements Cloneable {

	// base
	public EffectSetting(Loadable<?> parent, String id, boolean mandatory, Mat icon, List<String> description) {
		super(parent, id, mandatory, icon, description);
		registerSetting(new SettingPotionEffectType("type", null, false, PCLocale.GUI_GENERIC_EDITOR_POTIONEFFECT_TYPELORE.getLines()));
		registerSetting(new SettingInteger("level", "0", false, PCLocale.GUI_GENERIC_EDITOR_POTIONEFFECT_LEVELLORE.getLines()));
		registerSetting(new SettingInteger("duration", "0", false, PCLocale.GUI_GENERIC_EDITOR_POTIONEFFECT_DURATIONLORE.getLines()));
	}

	// methods
	public void give(LivingEntity entity, Player parsingPlayer) {
		PotionEffectType type = getSettingPotionEffectType("type").getParsed(parsingPlayer);
		int level = getSettingInteger("level").getParsed(parsingPlayer);
		int duration = getSettingInteger("duration").getParsed(parsingPlayer);
		if (type != null && duration > 0) {
			entity.addPotionEffect(new PotionEffect(type, duration, level == 0 ? level : level - 1));
		}
	}

	public void remove(LivingEntity entity, Player parsingPlayer) {
		PotionEffectType type = getSettingPotionEffectType("type").getParsed(parsingPlayer);
		if (type != null && entity.hasPotionEffect(type)) {
			entity.removePotionEffect(type);
		}
	}

	@Override
	public EffectSetting clone() {
		EffectSetting clone = new EffectSetting(loadParent(), getId(), loadMandatory(), loadIcon(), Utils.asList(loadDescription()));
		clone.getSettingPotionEffectType("type").setValue(getSettingPotionEffectType("type").getValue());
		clone.getSettingInteger("level").setValue(getSettingInteger("level").getValue());
		clone.getSettingInteger("duration").setValue(getSettingInteger("duration").getValue());
		return clone;
	}

}
