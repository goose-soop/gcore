package com.guillaumevdn.gcore.lib.element.type.container;

import java.util.List;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.container.ParseableContainerElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementBoolean;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementInteger;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementPotionEffectType;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;
import com.guillaumevdn.gcore.lib.time.duration.ElementDuration;

/**
 * @author GuillaumeVDN
 */
public class ElementPotionEffect extends ParseableContainerElement<PotionEffect> {

	private ElementPotionEffectType type = addPotionEffectType("type", Need.required(), TextEditorGeneric.descriptionPotionEffectType);
	private ElementDuration duration = addDuration("duration", Need.required(), null, null, TextEditorGeneric.descriptionPotionEffectDuration);
	private ElementInteger amplifier = addInteger("amplifier", Need.optional(0), 0, TextEditorGeneric.descriptionPotionEffectAmplifier);
	private ElementBoolean ambient = addBoolean("ambient", Need.optional(true), TextEditorGeneric.descriptionPotionEffectAmbient);
	private ElementBoolean particles = !Version.ATLEAST_1_8 ? null : addBoolean("particles", Need.optional(true), TextEditorGeneric.descriptionPotionEffectParticles);
	private ElementBoolean icon = !Version.ATLEAST_1_13 ? null : addBoolean("icon", Need.optional(true), TextEditorGeneric.descriptionPotionEffectIcon);

	public ElementPotionEffect(Element parent, String id, Need need, Text editorDescription) {
		super("time limit", parent, id, need, editorDescription);
	}

	// get
	public ElementPotionEffectType getType() {
		return type;
	}

	public ElementDuration getDuration() {
		return duration;
	}

	public ElementInteger getAmplifier() {
		return amplifier;
	}

	public ElementBoolean getAmbient() {
		return ambient;
	}

	public ElementBoolean getParticles() {
		return particles;
	}

	public ElementBoolean getIcon() {
		return icon;
	}

	// import
	public void importValue(PotionEffect effect) {
		type.setValue(CollectionUtils.asList(effect.getType().getName()));
		duration.setValue(((long) effect.getDuration()) * 50L);
		amplifier.setValue(CollectionUtils.asList("" + effect.getAmplifier()));
		ambient.setValue(effect.isAmbient() ? CollectionUtils.asList("true") : null);
		if (particles != null) {
			particles.setValue(effect.hasParticles() ? CollectionUtils.asList("particles") : null);
		}
		if (icon != null) {
			icon.setValue(effect.hasIcon() ? CollectionUtils.asList("particles") : null);
		}
	}

	// parse
	@Override
	public PotionEffect doParse(Replacer replacer) throws ParsingError {
		PotionEffectType type = this.type.parseNoCatchOrThrowParsingNull(replacer);
		int duration = (int) (this.duration.parseNoCatchOrThrowParsingNull(replacer) / 50L);
		Integer amplifier = this.amplifier.parseNoCatch(replacer).orNull();
		Boolean ambient = this.ambient.parseNoCatch(replacer).orNull();
		Boolean particles = this.particles == null ? null : this.particles.parseNoCatch(replacer).orNull();
		Boolean icon = this.icon == null ? null : this.icon.parseNoCatch(replacer).orNull();
		if (Version.ATLEAST_1_13) {
			return new PotionEffect(type, duration, amplifier, ambient != null && ambient, particles != null && particles, icon != null && icon);
		} else if (Version.ATLEAST_1_8) {
			return new PotionEffect(type, duration, amplifier, ambient != null && ambient, particles != null && particles);
		} else {
			return new PotionEffect(type, duration, amplifier, ambient != null && ambient);
		}
	}

	// editor
	@Override
	public Mat editorIconType() {
		return CommonMats.REPEATER;
	}

	@Override
	public List<String> editorCurrentValue() {
		return type.parseGeneric().ifPresentMap(type -> CollectionUtils.asList(type.toString())).orNull();
	}

}
