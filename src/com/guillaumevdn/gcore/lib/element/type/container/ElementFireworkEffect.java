package com.guillaumevdn.gcore.lib.element.type.container;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.container.ParseableContainerElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementBoolean;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementColorList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementFireworkEffectType;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class ElementFireworkEffect extends ParseableContainerElement<FireworkEffect> {

	private ElementFireworkEffectType type = addFireworkEffectType("type", Need.required(), TextEditorGeneric.descriptionFireworkEffectType);
	private ElementColorList colors = addColorList("colors", Need.required(), TextEditorGeneric.descriptionFireworkEffectColors);
	private ElementColorList fadeColors = addColorList("fade_colors", Need.required(), TextEditorGeneric.descriptionFireworkEffectFadeColors);
	private ElementBoolean flicker = addBoolean("flicker", Need.optional(false), TextEditorGeneric.descriptionFireworkEffectFlicker);
	private ElementBoolean trail = addBoolean("trail", Need.optional(false), TextEditorGeneric.descriptionFireworkEffectTrail);

	public ElementFireworkEffect(Element parent, String id, Need need, Text editorDescription) {
		super("potion effect", parent, id, need, editorDescription);
	}

	// get
	public ElementFireworkEffectType getType() {
		return type;
	}

	public ElementColorList getColors() {
		return colors;
	}

	public ElementColorList getFadeColors() {
		return fadeColors;
	}

	public ElementBoolean getFlicker() {
		return flicker;
	}

	public ElementBoolean getTrail() {
		return trail;
	}

	// import
	public void importValue(FireworkEffect value) {
		type.setValue(CollectionUtils.asList(value.getType().name()));
		colors.setValue(Serializer.COLOR.serialize(value.getColors()));
		fadeColors.setValue(Serializer.COLOR.serialize(value.getFadeColors()));
		flicker.setValue(value.hasFlicker() ? CollectionUtils.asList("true") : null);
		trail.setValue(value.hasTrail() ? CollectionUtils.asList("true") : null);
	}

	// parse
	@Override
	public FireworkEffect doParse(Replacer replacer) throws ParsingError {
		FireworkEffect.Type type = this.type.parseNoCatchOrThrowParsingNull(replacer);
		List<Color> colors = this.colors.parseNoCatch(replacer).orEmptyList();
		List<Color> fadeColors = this.fadeColors.parseNoCatch(replacer).orEmptyList();
		Boolean flicker = this.flicker.parseNoCatch(replacer).orElse(false);
		Boolean trail = this.trail.parseNoCatch(replacer).orElse(false);
		return FireworkEffect.builder().with(type).withColor(colors).withFade(fadeColors).flicker(flicker != null && flicker).trail(trail != null && trail).build();
	}

	// editor
	@Override
	public Mat editorIconType() {
		return CommonMats.REPEATER;
	}

	@Override
	public List<String> editorCurrentValue() {
		List<String> desc = new ArrayList<>();
		type.parseGeneric().ifPresentDo(type -> desc.add(type.toString()));
		colors.parseGeneric().ifPresentDo(colors -> desc.addAll(Serializer.COLOR.serialize(colors)));
		return desc.isEmpty() ? null : desc;
	}

}
