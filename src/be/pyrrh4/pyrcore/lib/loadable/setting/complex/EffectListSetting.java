package be.pyrrh4.pyrcore.lib.loadable.setting.complex;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.lib.loadable.Loadable;
import be.pyrrh4.pyrcore.lib.loadable.LoadableListSetting;
import be.pyrrh4.pyrcore.lib.material.Mat;

public class EffectListSetting extends LoadableListSetting<EffectSetting> {

	// base
	public EffectListSetting(Loadable<?> parent, String id, boolean mandatory, Mat icon, List<String> description) {
		super(parent, id, mandatory, icon, description);
	}

	// methods
	@Override
	protected EffectSetting instantiate(Loadable<?> parent, String id, boolean mandatory, Mat icon, List<String> description) {
		return new EffectSetting(parent, id, mandatory, icon, description);
	}

	public void give(LivingEntity entity, Player parsingPlayer) {
		for (EffectSetting effect : list().values()) {
			effect.give(entity, parsingPlayer);
		}
	}

	public void remove(LivingEntity entity, Player parsingPlayer) {
		for (EffectSetting effect : list().values()) {
			effect.remove(entity, parsingPlayer);
		}
	}

}
