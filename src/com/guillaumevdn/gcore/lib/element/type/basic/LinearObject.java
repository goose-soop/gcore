package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.potion.PotionEffectType;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.lib.compatibility.particle.Particle;
import com.guillaumevdn.gcore.lib.compatibility.sound.Sound;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.particlescript.ParticleScript;
import com.guillaumevdn.gcore.lib.reflection.Reflection;
import com.guillaumevdn.gcore.lib.reflection.ReflectionEnum;
import com.guillaumevdn.gcore.lib.string.StringUtils;

public class LinearObject<T extends LinearObjectType> {

	private T type;
	private List<String> arguments;

	public LinearObject(T type) {
		this(type, null);
	}

	public LinearObject(T type, List<String> arguments) {
		this.type = type;
		this.arguments = arguments == null ? new ArrayList<>() : arguments;
	}

	// ----- object
	@Override
	public String toString() {
		return type + " " + StringUtils.toTextString(" ", arguments);
	}

	// ----- get
	public T getType() {
		return type;
	}

	public List<String> getArguments() {
		return arguments;
	}

	public String getValidArguments() {
		if (arguments.isEmpty()) {
			throw new IllegalArgumentException("type " + type.name() + " is missing arguments");
		}
		return StringUtils.toTextString(" ", arguments);
	}

	public String getValidArguments(int startIndex) {
		if (arguments.size() < startIndex) {
			throw new IllegalArgumentException("type " + type.name() + " is missing argument " + (startIndex + 1));
		}
		String result = "";
		for (int i = startIndex; i < arguments.size(); ++i) {
			if (i != startIndex) result += " ";
			result += arguments.get(i);
		}
		return result;
	}

	public String getValidArgument(int index) {
		if (index >= arguments.size()) {
			throw new IllegalArgumentException("type " + type.name() + " is missing argument " + (index + 1) + " (arguments : " + arguments + ")");
		}
		return arguments.get(index);
	}

	public boolean getValidBooleanArgument(int index) {
		return Boolean.parseBoolean(getValidArgument(index));
	}

	public int getValidIntArgument(int index) {
		String found = getValidArgument(index);
		Integer value = NumberUtils.integerOrNull(found);
		if (value != null) return value;
		throw new IllegalArgumentException("type " + type.name() + " needed a number for argument " + (index + 1) + " (found '" + found + "') (arguments : " + arguments + ")");
	}

	public double getValidDoubleArgument(int index) {
		String found = getValidArgument(index);
		Double value = NumberUtils.doubleOrNull(found);
		if (value != null) return value;
		throw new IllegalArgumentException("type " + type.name() + " needed a number for argument " + (index + 1) + " (found '" + found + "') (arguments : " + arguments + ")");
	}

	public long getValidLongArgument(int index) {
		String found = getValidArgument(index);
		Long value = NumberUtils.longOrNull(found);
		if (value != null) return value;
		throw new IllegalArgumentException("type " + type.name() + " needed a number for argument " + (index + 1) + " (found '" + found + "') (arguments : " + arguments + ")");
	}

	public Sound getValidSoundArgument(int index) {
		String found = getValidArgument(index);
		Sound value = Sound.firstFromIdOrDataName(found).orNull();
		if (value != null) return value;
		throw new IllegalArgumentException("type " + type.name() + " needed a sound for argument " + (index + 1) + " (found '" + found + "') (found '" + found + "') (arguments : " + arguments + ")");
	}

	public Particle getValidParticleArgument(int index) {
		String found = getValidArgument(index);
		Particle value = Particle.firstFromIdOrDataName(found).orNull();
		if (value != null) return value;
		throw new IllegalArgumentException("type " + type.name() + " needed a particle for argument " + (index + 1) + " (found '" + found + "') (arguments : " + arguments + ")");
	}

	public PotionEffectType getValidPotionEffectTypeArgument(int index) {
		String found = getValidArgument(index);
		PotionEffectType value = ObjectUtils.potionEffectTypeOrNull(found);
		if (value != null) return value;
		throw new IllegalArgumentException("type " + type.name() + " needed a potion effect for argument " + (index + 1) + " (found '" + found + "') (arguments : " + arguments + " ; available : " + StringUtils.toTextString(",", Arrays.stream(PotionEffectType.values()).map(PotionEffectType::getName)) + ")");
	}

	public ParticleScript getValidParticleScriptArgument(int index) {
		String found = getValidArgument(index);
		ParticleScript value = ConfigGCore.particleScripts.get(found);
		if (value != null) return value;
		throw new IllegalArgumentException("type " + type.name() + " needed a particle script for argument " + (index + 1) + " (found '" + found + "') (arguments : " + arguments + ")");
	}

	public <E extends Enum<E>> E getValidEnumTypeArgument(Class<E> enumClass, int index) {
		String found = getValidArgument(index);
		E value = ObjectUtils.safeValueOf(found, enumClass);
		if (value != null) return value;
		throw new IllegalArgumentException("type " + type.name() + " needed a " + enumClass.getSimpleName() + " for argument " + (index + 1) + " (found '" + found + "') (arguments : " + arguments + ")");
	}

	public Object getValidUnknownEnumTypeArgument(String enumPath, int index) {
		String found = getValidArgument(index);
		try {
			ReflectionEnum enumeration = Reflection.getEnum(enumPath);
			Object value = enumeration.valueOf(found).orNull();
			if (value != null) return value;
		} catch (Throwable ignored) {}
		throw new IllegalArgumentException("type " + type.name() + " needed a " + enumPath.substring(enumPath.lastIndexOf('.') + 1) + " for argument " + (index + 1) + " (found '" + found + "') (arguments : " + arguments + ")");
	}

	public <E extends Enum<E>> List<E> getValidEnumTypeArguments(Class<E> enumClass) {
		List<E> result = new ArrayList<>();
		for (int i = 0; i < arguments.size(); ++i) {
			result.add(getValidEnumTypeArgument(enumClass, i));
		}
		return result;
	}

	public List<Object> getValidUnknownEnumTypeArguments(String enumPath) {
		List<Object> result = new ArrayList<>();
		for (int i = 0; i < arguments.size(); ++i) {
			result.add(getValidUnknownEnumTypeArgument(enumPath, i));
		}
		return result;
	}

}
