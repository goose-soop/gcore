package com.guillaumevdn.gcore.lib.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.function.ThrowableConsumer;

/**
 * @author GuillaumeVDN
 */
public final class ObjectUtils {

	// ----- enum
	public static <E extends Enum<E>> E safeValueOf(String name, Class<E> enumClass) {
		if (name == null || name.isEmpty()) {
			return null;
		}
		try {
			for (E value : enumClass.getEnumConstants()) {
				if (value.name().equalsIgnoreCase(name)) {
					return value;
				}
			}
		} catch (Throwable ignored) {
			ignored.printStackTrace();  // because why the heck does it happen sometimes ? :CringeHarold:
		}
		return null;
	}

	public static <E extends Enum<E>> List<E> safeValuesOf(Collection<String> names, Class<E> enumClass) {
		List<E> result = new ArrayList<>();
		names.forEach(name -> {
			E t = safeValueOf(name, enumClass);
			if (t != null) {
				result.add(t);
			}
		});
		return result;
	}

	public static <E extends Enum<E>> Set<E> safeValuesSetOf(Collection<String> names, Class<E> enumClass) {
		Set<E> result = new HashSet<>();
		names.forEach(name -> {
			E t = safeValueOf(name, enumClass);
			if (t != null) {
				result.add(t);
			}
		});
		return result;
	}

	public static Class<?> safeClass(String string) {
		try {
			return Class.forName(string.trim());
		} catch (Throwable ignored) {
			return null;
		}
	}

	public static Enchantment enchantmentOrNull(String name) {
		if (name == null || name.isEmpty()) {
			return null;
		}
		try {
			Enchantment ench = Enchantment.getByName(name.toUpperCase());
			if (ench != null) {
				return ench;
			}
			for (Enchantment value : Enchantment.values()) {
				if (value != null /* seems to happen sometimes for some dank reason ??? */ && value.getName().equalsIgnoreCase(name)) {
					return value;
				}
			}
		} catch (Throwable ignored) {
			ignored.printStackTrace();  // because why the heck does it happen sometimes ? :CringeHarold:
		}
		return null;
	}

	public static PotionEffectType potionEffectTypeOrNull(String name) {
		if (name == null || name.isEmpty()) {
			return null;
		}
		try {
			PotionEffectType eff = PotionEffectType.getByName(name.toUpperCase());
			if (eff != null) {
				return eff;
			}
			for (PotionEffectType value : PotionEffectType.values()) {
				if (value != null /* seems to happen sometimes for some dank reason ??? */ && name.equalsIgnoreCase(value.getName())) {
					return value;
				}
			}
		} catch (Throwable ignored) {
			ignored.printStackTrace();  // because why the heck does it happen sometimes ? :CringeHarold:
		}
		return null;
	}

	public static UUID uuidOrNull(String string) {
		try {
			return UUID.fromString(string);
		} catch (Throwable ignored) {}
		return null;
	}

	// ----- cast
	public static <T> void ifCanBeCasted(Object object, Class<T> castClass, Consumer<T> consumer) {
		T casted = castOrNull(object, castClass);
		if (casted != null) {
			consumer.accept(casted);
		}
	}

	public static <T, R> Optional<R> ifCanBeCastedDo(Object object, Class<T> castClass, Function<T, R> consumer) {
		T casted = castOrNull(object, castClass);
		return Optional.of(casted == null ? null : consumer.apply(casted));
	}

	public static <T> T castOrNull(Object object, Class<T> castClass) {
		try {
			if (instanceOf(object, castClass)) {
				return (T) object;
			}
		} catch (ClassCastException | NullPointerException ignored) {}
		return null;
	}

	public static <T> T cast(Object object, Class<T> castClass) throws ClassCastException {
		try {
			if (instanceOf(object, castClass)) {
				return (T) object;
			}
		} catch (ClassCastException | NullPointerException ignored) {}
		throw new ClassCastException("couldn't cast object of type " + (object == null ? "null" : object.getClass()) + " to " + castClass);
	}

	public static <T extends Throwable> T findCauseOrNull(Throwable exception, Class<T> causeClass) {
		Throwable except = exception;
		while (except != null) {
			T found = ObjectUtils.castOrNull(except, causeClass);
			if (found != null) {
				return (T) except;
			}
			except = except.getCause();
		}
		return null;
	}

	// ----- equals
	public static <T> boolean equals(Object object, Class<T> typeClass, Function<T, Boolean> equals) {
		T other = ObjectUtils.castOrNull(object, typeClass);
		return other != null && equals.apply(other);
	}

	// ----- instanceof
	private static final Map<Class<?>, Class<?>> primitiveWrappers = CollectionUtils.asMap(
			int.class, Integer.class,
			double.class, Double.class,
			boolean.class, Boolean.class,
			long.class, Long.class,
			byte.class, Byte.class,
			short.class, Short.class,
			float.class, Float.class,
			char.class, Character.class
			);

	public static <T> boolean instanceOf(T obj, Class<?> typeClass) {
		return instanceOf(obj == null ? null : obj.getClass(), typeClass);
	}

	public static <T> boolean instanceOf(Class<?> objClass, Class<?> typeClass) {
		if (typeClass == null) {
			return objClass == null;
		}
		if (objClass == null) {
			return false;
		}
		if (objClass.isPrimitive()) {
			objClass = primitiveWrappers.get(objClass);
		}
		if (typeClass.isPrimitive()) {
			typeClass = primitiveWrappers.get(typeClass);
		}
		return typeClass.isAssignableFrom(objClass);
	}

	public static <T> boolean instanceOfOne(T object, Class<?>... typeClasses) {
		return instanceOfOne(object, CollectionUtils.asList(typeClasses));
	}

	public static <T> boolean instanceOfOne(T object, Collection<? extends Class<?>> typeClasses) {
		if (object == null) {
			return typeClasses.contains(null);
		}
		for (Class<?> typeClass : typeClasses) {
			if (instanceOf(object, typeClass)) {
				return true;
			}
		}
		return false;
	}

	public static <T> boolean instanceOfOne(Class<T> objClass, Class<?>... typeClasses) {
		return instanceOfOne(objClass, CollectionUtils.asList(typeClasses));
	}

	public static <T> boolean instanceOfOne(Class<T> objClass, Collection<? extends Class<?>> typeClasses) {
		for (Class<?> typeClass : typeClasses) {
			if (instanceOf(objClass, typeClass)) {
				return true;
			}
		}
		return false;
	}

	// ----- misc
	public static <T> Consumer<T> emptyConsumer() {
		return t -> {};
	}

	public static <T> ThrowableConsumer<T> emptyThrowableConsumer() {
		return t -> {};
	}

	public static <T> Supplier<T> supplier(Class<T> clazz, Supplier<T> supplier) { // wrapping function
		return supplier;
	}

	public static <R> R produce(Supplier<R> result) {  // useful in super() calls for instance
		return result.get();
	}

}
