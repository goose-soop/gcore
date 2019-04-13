package com.guillaumevdn.gcore.lib.parseable.container;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.ContainerParseable;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPInteger;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPPotionEffectType;

public class CPPotionEffect extends ContainerParseable {

	// base
	private PPPotionEffectType type = addComponent(new PPPotionEffectType("type", this, null, false, 0, EditorGUI.ICON_BLOCK, GLocale.GUI_GENERIC_EDITOR_POTIONEFFECT_TYPELORE.getLines()));
	private PPInteger amplifier = addComponent(new PPInteger("amplifier", this, "0", 0, 1, false, 1, EditorGUI.ICON_NUMBER_LEVEL, GLocale.GUI_GENERIC_EDITOR_POTIONEFFECT_AMPLIFIERLORE.getLines()));
	private PPInteger duration = addComponent(new PPInteger("duration", this, "0", 0, Integer.MAX_VALUE, false, 2, EditorGUI.ICON_NUMBER, GLocale.GUI_GENERIC_EDITOR_POTIONEFFECT_DURATIONLORE.getLines()));

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

	public PPInteger getAmplifier() {
		return amplifier;
	}
	
	public Integer getAmplifier(Player parser) {
		return amplifier.getParsedValue(parser);
	}
	
	public PPInteger getDuration() {
		return duration;
	}

	public Integer getDuration(Player parser) {
		return duration.getParsedValue(parser);
	}
	
	public PotionEffect getParsedValue(Player parser) {
		PotionEffectType type = getType(parser);
		Integer amplifier = getAmplifier(parser);
		Integer duration = getDuration(parser);
		return type != null && amplifier != null && duration != null ? new PotionEffect(type, duration, amplifier) : null;
	}

	// methods
	public void give(LivingEntity entity, Player parser) {
		PotionEffect effect = getParsedValue(parser);
		if (effect != null) {
			entity.addPotionEffect(effect);
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
		return (CPPotionEffect) super.clone();
	}

}
