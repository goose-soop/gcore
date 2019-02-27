package be.pyrrh4.pyrcore.lib.parseable.container;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.parseable.ContainerParseable;
import be.pyrrh4.pyrcore.lib.parseable.Parseable;
import be.pyrrh4.pyrcore.lib.parseable.editor.EditorGUI;
import be.pyrrh4.pyrcore.lib.parseable.primitive.PPInteger;
import be.pyrrh4.pyrcore.lib.parseable.primitive.PPPotionEffectType;

public class CPPotionEffect extends ContainerParseable {

	// base
	private PPPotionEffectType type = addComponent(new PPPotionEffectType("type", this, null, false, 0, EditorGUI.ICON_BLOCK, PCLocale.GUI_GENERIC_EDITOR_POTIONEFFECT_TYPELORE.getLines()));
	private PPInteger level = addComponent(new PPInteger("level", this, "0", 0, Integer.MAX_VALUE, false, 1, EditorGUI.ICON_NUMBER_LEVEL, PCLocale.GUI_GENERIC_EDITOR_POTIONEFFECT_LEVELLORE.getLines()));
	private PPInteger duration = addComponent(new PPInteger("duration", this, "0", 0, Integer.MAX_VALUE, false, 2, EditorGUI.ICON_NUMBER, PCLocale.GUI_GENERIC_EDITOR_POTIONEFFECT_DURATIONLORE.getLines()));

	public CPPotionEffect(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, "potion effect", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// get
	public PPPotionEffectType getType() {
		return type;
	}

	public PotionEffectType getType(Player parser) {
		return type.getParsedValue(parser);
	}

	public PPInteger getLevel() {
		return level;
	}

	public Integer getLevel(Player parser) {
		return level.getParsedValue(parser);
	}

	public PPInteger getDuration() {
		return duration;
	}

	public Integer getDuration(Player parser) {
		return duration.getParsedValue(parser);
	}

	// methods
	public void give(LivingEntity entity, Player parser) {
		PotionEffectType type = getType(parser);
		int level = getLevel(parser);
		int duration = getDuration(parser);
		if (type != null && duration > 0) {
			entity.addPotionEffect(new PotionEffect(type, duration, level == 0 ? level : level - 1));
		}
	}

	public void remove(LivingEntity entity, Player parser) {
		PotionEffectType type = getType(parser);
		if (type != null) {
			entity.removePotionEffect(type);
		}
	}

	// clone
	protected CPPotionEffect() {
		super();
	}

	@Override
	public CPPotionEffect clone() {
		// clone
		CPPotionEffect clone = (CPPotionEffect) super.clone();
		// clone properties
		clone.type = type.clone();
		clone.level = level.clone();
		clone.duration = duration.clone();
		// success
		return clone;
	}

}
