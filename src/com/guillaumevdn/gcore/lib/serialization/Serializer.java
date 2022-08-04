package com.guillaumevdn.gcore.lib.serialization;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.lib.block.BlockState;
import com.guillaumevdn.gcore.lib.block.BlockStateType;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.compatibility.particle.Particle;
import com.guillaumevdn.gcore.lib.compatibility.sound.Sound;
import com.guillaumevdn.gcore.lib.concurrency.RWHashMap;
import com.guillaumevdn.gcore.lib.data.board.Board;
import com.guillaumevdn.gcore.lib.economy.Currency;
import com.guillaumevdn.gcore.lib.element.struct.SuperElement;
import com.guillaumevdn.gcore.lib.element.struct.container.typable.TypableElementType;
import com.guillaumevdn.gcore.lib.element.struct.container.typable.TypableElementTypes;
import com.guillaumevdn.gcore.lib.element.struct.list.referenceable.ElementsContainer;
import com.guillaumevdn.gcore.lib.element.type.basic.LinearObject;
import com.guillaumevdn.gcore.lib.element.type.basic.LinearObjectType;
import com.guillaumevdn.gcore.lib.gui.ItemFlag;
import com.guillaumevdn.gcore.lib.gui.element.item.overrideclick.OverrideClick;
import com.guillaumevdn.gcore.lib.gui.element.item.overrideclick.OverrideClickType;
import com.guillaumevdn.gcore.lib.gui.element.item.type.GUIItemType;
import com.guillaumevdn.gcore.lib.gui.element.item.type.GUIItemTypes;
import com.guillaumevdn.gcore.lib.location.Point;
import com.guillaumevdn.gcore.lib.location.position.PositionType;
import com.guillaumevdn.gcore.lib.location.position.PositionTypes;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.particlescript.ParticleScript;
import com.guillaumevdn.gcore.lib.permission.Permission;
import com.guillaumevdn.gcore.lib.plugin.PluginUtils;
import com.guillaumevdn.gcore.lib.reflection.Reflection;
import com.guillaumevdn.gcore.lib.serialization.gson.SerializableGsonAdapter;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.time.frame.TimeFrameType;
import com.guillaumevdn.gcore.lib.time.frame.TimeFrameTypes;

/**
 * @author GuillaumeVDN
 */
public abstract class Serializer<T> {

	private final String typeName;
	private final Class<T> typeClass;
	private final SerializableGsonAdapter<T> gsonAdapter;
	private final boolean registered;

	protected Serializer(Class<T> typeClass, boolean register) {
		this(StringUtils.getReadableName(typeClass).toLowerCase(), typeClass, register);
	}

	protected Serializer(String typeName, Class<T> typeClass, boolean register) {
		// register
		if (register) {
			if (registration.containsKey(typeClass)) throw new IllegalStateException("there's already a serializer for type " + typeClass);
			registration.put(typeClass, this);
		}
		// init
		this.typeName = typeName;
		this.typeClass = typeClass;
		this.gsonAdapter = new SerializableGsonAdapter<>(this);
		this.registered = register;
	}

	// ----- get
	public final String getTypeName() {
		return typeName;
	}

	public final Class<T> getTypeClass() {
		return typeClass;
	}

	public final SerializableGsonAdapter<T> getGsonAdapter() {
		return gsonAdapter;
	}

	public final boolean isRegistered() {
		return registered;
	}

	// ----- serialization
	public abstract String serialize(T value);
	public abstract T deserialize(String string);

	public List<String> serialize(Collection<T> value) {
		return value == null ? null : value.stream().map(t -> serialize(t)).collect(Collectors.toList());
	}

	public List<T> deserialize(Collection<String> strings) {
		return strings == null ? null : strings.stream().map(t -> t == null ? null : deserialize(t.trim())).collect(Collectors.toList());
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- registration
	// ----------------------------------------------------------------------------------------------------

	private static final RWHashMap<Class, Serializer> registration = new RWHashMap<>(10, 1f);  // #2284, concurrent modification exception

	public static <T> Serializer<T> find(T object) {
		if (object == null) throw new NullPointerException();
		return (Serializer<T>) find(object.getClass());
	}

	public static <T> Serializer<T> find(Class<T> typeClass) {

		if (String.class.equals(typeClass)) {
			return (Serializer<T>) STRING;  // so feeeest
		}

		Serializer ser = registration.streamResult(str -> str
				.filter(e -> ObjectUtils.instanceOf(typeClass, e.getKey()))
				.findFirst()
				.map(e -> e.getValue())
				.orElse(null)
				);
		if (ser != null) {
			return ser;
		}
		if (typeClass.isEnum()) {
			return ofEnum((Class<? extends Enum>) typeClass);
		}
		throw new IllegalArgumentException("no serializer found for class " + typeClass);
	}

	public static <T> Collection<Serializer> values() {
		return Collections.unmodifiableCollection(registration.copyValues());
	}

	public static void unregister(Class<?> serializerClass) {
		registration.remove(serializerClass);
	}

	// ----- init
	public static void init() {}

	// ----------------------------------------------------------------------------------------------------
	// ----- creation
	// ----------------------------------------------------------------------------------------------------

	public static <T> Serializer<T> of(Class<T> typeClass, Function<T, String> serializer, Function<String, T> deserializer) {
		return of(typeClass, serializer, deserializer, true);
	}

	public static <T> Serializer<T> of(Class<T> typeClass, Function<T, String> serializer, Function<String, T> deserializer, boolean register) {
		return of(StringUtils.getReadableName(typeClass).toLowerCase(), typeClass, serializer, deserializer, register);
	}

	public static <T> Serializer<T> of(String typeName, Class<T> typeClass, Function<T, String> serializer, Function<String, T> deserializer) {
		return of(typeName, typeClass, serializer, deserializer, true);
	}

	public static <T> Serializer<T> of(String typeName, Class<T> typeClass, Function<T, String> serializer, Function<String, T> deserializer, boolean register) {
		if (register) {
			Serializer<T> existing = registration.get(typeClass);
			if (existing != null) {
				throw new IllegalStateException("Serializer for " + typeClass + " already exists");
			}
		}
		return new Serializer<T>(typeName, typeClass, register) {
			@Override
			public String serialize(T value) {
				return value == null ? "null" : serializer.apply(value);
			}
			@Override
			public T deserialize(String string) {
				return string == null || string.equalsIgnoreCase("null") ? null : deserializer.apply(string);
			}
		};
	}

	public static <E extends Enum<E>> Serializer<E> ofEnum(Class<E> typeClass) {
		return ofEnum(StringUtils.getReadableName(typeClass).toLowerCase(), typeClass);
	}

	public static <E extends Enum<E>> Serializer<E> ofEnum(String typeName, Class<E> typeClass) {
		Serializer serializer = registration.get(typeClass);
		if (serializer != null) {
			return serializer;
		}
		return of(typeName, typeClass, value -> value.name(), string -> ObjectUtils.safeValueOf(string, typeClass));
	}

	public static <T> Serializer<T[]> ofArray(Class<T[]> arrayClass, Class<T> valueClass) {
		Serializer<T> valueSerializer = find(valueClass);
		return of(arrayClass, array -> {
			String serialized = "";
			if (array.length != 0) {
				for (int i = 0; i < array.length; ++i) {
					serialized += valueSerializer.serialize(array[i]);
					if (i + 1 < array.length) serialized += "@@@";
				}
			}
			return serialized;
		}, string -> {
			List<T> result = new ArrayList<>();
			if (!string.isEmpty()) {
				for (String value : string.split("@@@")) {
					result.add(valueSerializer.deserialize(value));
				}
			}
			return result.toArray((T[]) Array.newInstance(valueClass, result.size()));
		});
	}

	public static <T extends TypableElementType> Serializer<T> ofTypable(Class<T> typeClass, Supplier<TypableElementTypes<T>> typables) {
		return of(StringUtils.getReadableName(typeClass).toLowerCase(), typeClass, value -> value.getId(), string -> typables.get().safeValueOf(string));
	}

	public static <T extends SuperElement> Serializer<T> ofContainer(Class<T> typeClass, Supplier<ElementsContainer<T>> container) {
		return of(StringUtils.getReadableName(typeClass).toLowerCase(), typeClass, value -> value.getId(), string -> container.get().getElement(string).orNull());
	}

	public static <T extends LinearObjectType, L extends LinearObject<T>> LinearSerializer<T, L> ofLinear(Serializer<T> typeSerializer, Class<L> typeClass, BiFunction<T, List<String>, L> deserializer) {
		return ofLinear(typeSerializer, StringUtils.getReadableName(typeClass).toLowerCase(), typeClass, deserializer);
	}

	public static <T extends LinearObjectType, L extends LinearObject<T>> LinearSerializer<T, L> ofLinear(Serializer<T> typeSerializer, String typeName, Class<L> typeClass, BiFunction<T, List<String>, L> deserializer) {
		return new LinearSerializer<T, L>(typeSerializer, typeName, typeClass, true) {
			@Override
			protected L deserialize(T type, List<String> params) {
				return deserializer.apply(type, params);
			}
		};
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- types : java
	// ----------------------------------------------------------------------------------------------------

	public static final Serializer<String> STRING = of(String.class, value -> value, string -> string);
	public static final Serializer<Boolean> BOOLEAN = of(Boolean.class, value -> value.toString(), string -> string.equalsIgnoreCase("true"));
	public static final Serializer<Double> DOUBLE = of(Double.class, value -> new BigDecimal(value).toPlainString(), string -> {
		try {
			return string.contains("E") ? new BigDecimal(string.trim()).doubleValue() : Double.valueOf(string.trim());
		} catch (Throwable ignored) {
			return null;
		}
	});
	public static final Serializer<Float> FLOAT = of(Float.class, value -> value.toString(), string -> NumberUtils.floatOrNull(string));
	public static final Serializer<Byte> BYTE = of(Byte.class, value -> value.toString(), string -> NumberUtils.byteOrNull(string));
	public static final Serializer<Integer> INTEGER = of(Integer.class, value -> value.toString(), string -> NumberUtils.integerOrNull(string.trim()));
	public static final Serializer<Long> LONG = of(Long.class, value -> value.toString(), string -> NumberUtils.longOrNull(string.trim()));
	public static final Serializer<Class> CLASS = of(Class.class, value -> value.getName(), string -> ObjectUtils.safeClass(string));
	public static final Serializer<File> FILE = of(File.class, value -> value.getPath(), string -> new File(string.trim()));
	public static final Serializer<java.util.UUID> UUID = of(java.util.UUID.class, value -> value.toString(), string -> java.util.UUID.fromString(string));

	public static final Serializer<Integer[]> INT_ARRAY = ofArray(Integer[].class, Integer.class);
	public static final Serializer<Byte[]> BYTE_ARRAY = ofArray(Byte[].class, Byte.class);

	public static final Serializer<int[]> PRIMITIVE_INT_ARRAY = of(int[].class, array -> {
		String serialized = "";
		if (array.length != 0) {
			for (int i = 0; i < array.length; ++i) {
				serialized += INTEGER.serialize(array[i]);
				if (i + 1 < array.length) serialized += "@@@";
			}
		}
		return serialized;
	}, string -> {
		List<Integer> result = new ArrayList<>();
		if (!string.isEmpty()) {
			for (String value : string.split("@@@")) {
				result.add(INTEGER.deserialize(value));
			}
		}
		int[] array = new int[result.size()];
		for (int i = 0; i < result.size(); ++i) {
			array[i] = result.get(i);
		}
		return array;
	});

	public static final Serializer<byte[]> PRIMITIVE_BYTE_ARRAY = of(byte[].class, array -> {
		String serialized = "";
		if (array.length != 0) {
			for (int i = 0; i < array.length; ++i) {
				serialized += BYTE.serialize(array[i]);
				if (i + 1 < array.length) serialized += "@@@";
			}
		}
		return serialized;
	}, string -> {
		List<Byte> result = new ArrayList<>();
		if (!string.isEmpty()) {
			for (String value : string.split("@@@")) {
				result.add(BYTE.deserialize(value));
			}
		}
		byte[] array = new byte[result.size()];
		for (int i = 0; i < result.size(); ++i) {
			array[i] = result.get(i);
		}
		return array;
	});

	// ----------------------------------------------------------------------------------------------------
	// ----- types : mine
	// ----------------------------------------------------------------------------------------------------

	public static final Serializer<TimeFrameType> TIME_FRAME_TYPE = ofTypable(TimeFrameType.class, () -> TimeFrameTypes.inst());
	public static final Serializer<PositionType> POSITION_TYPE = ofTypable(PositionType.class, () -> PositionTypes.inst());
	public static final Serializer<GUIItemType> GUI_ITEM_TYPE = ofTypable(GUIItemType.class, () -> GUIItemTypes.inst());
	public static final Serializer<Currency> CURRENCY = of(Currency.class, value -> value.getId(), string -> {
		Currency curr = Currency.safeValueOf(string);
		return curr != null && curr.isEnabled() ? curr : null;
	});
	public static final Serializer<ParticleScript> PARTICLE_SCRIPT = of(ParticleScript.class, value -> value.getId(), string -> ConfigGCore.particleScripts.get(string));
	public static final Serializer<ItemFlag> ITEM_FLAG = ofEnum(ItemFlag.class);
	public static final LinearSerializer<OverrideClickType, OverrideClick> OVERRIDE_CLICK = Serializer.ofLinear(Serializer.ofEnum(OverrideClickType.class), OverrideClick.class, (type, params) -> new OverrideClick(type, params));
	public static final Serializer<Permission> PERMISSION = of(Permission.class, value -> value.getName(), string -> new Permission(string));
	public static final Serializer<Point> POINT = of(Point.class,
			value -> value.getWorld().getName() + "," + value.getX() + "," + value.getY() + "," + value.getZ(),
			string -> {
				// not enough params
				String[] split = string.split(",");
				if (split.length < 4) {
					return null;
				}
				// decode
				try {
					return new Point(split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
				} catch (Throwable exception) {
					throw new Error("couldn't deserialize point " + string, exception);
				}
			});
	public static final Serializer<Board> BOARD = of(Board.class, b -> b.getId(), id -> {
		return PluginUtils.getGPlugins().stream()
				.flatMap(pl -> pl.getData().streamResult(str -> str.collect(Collectors.toList()).stream()))
				.filter(b -> b.getKey().equalsIgnoreCase(id))
				.findFirst().map(e -> e.getValue())
				.orElse(null);
	});

	// ----------------------------------------------------------------------------------------------------
	// ----- types : bukkit
	// ----------------------------------------------------------------------------------------------------

	public static final Serializer NAMESPACED_KEY = !Version.ATLEAST_1_12 ? null : of(org.bukkit.NamespacedKey.class, value -> value.getNamespace() + ":" + value.getKey(), string -> {
		String[] split = string.split(":");
		return split.length < 2 ? null : new org.bukkit.NamespacedKey(split[0], split[1]);
	});
	public static final Serializer BANNER_PATTERN = !Version.ATLEAST_1_8 ? null : of(org.bukkit.block.banner.Pattern.class, value -> value.getPattern() + "-" + value.getColor(), string -> {
		String[] split = string.split("-");
		org.bukkit.block.banner.PatternType ptype = split.length < 1 ? null : ObjectUtils.safeValueOf(split[0], org.bukkit.block.banner.PatternType.class);
		DyeColor pcolor = split.length < 2 ? null : ObjectUtils.safeValueOf(split[1], DyeColor.class);
		return ptype != null && pcolor != null ? new org.bukkit.block.banner.Pattern(pcolor, ptype) : null;
	});
	public static final Serializer<EntityType> ENTITY_TYPE = ofEnum(EntityType.class);
	public static final Serializer<PotionEffectType> POTION_EFFECT_TYPE = of(PotionEffectType.class, value -> value.getName(), string -> ObjectUtils.potionEffectTypeOrNull(string));
	public static final Serializer<Enchantment> ENCHANTMENT = of(Enchantment.class, value -> value.getName(), string -> ObjectUtils.enchantmentOrNull(string));
	public static final Serializer<ChatColor> CHAT_COLOR = ofEnum(ChatColor.class);
	public static final Serializer<Color> COLOR = of(Color.class, value -> {
		try {
			for (Field field : Color.class.getFields()) {
				if (Modifier.isStatic(field.getModifiers())) {
					Color color = ObjectUtils.castOrNull(field.get(null), Color.class);
					if (color != null && color.equals(value)) {
						return field.getName();
					}
				}
			}
		} catch (Throwable ignored) {}
		return value.getRed() + "," + value.getGreen() + "," + value.getBlue();
	}, string -> {
		if (!string.contains(",")) {
			try {
				return Reflection.getField("org.bukkit.Color", string.toUpperCase()).retrieve(null).get();
			} catch (Throwable ignored) {}
			return null;
		}
		List<Integer> ints = NumberUtils.integersIn(string.split(","));
		return ints.size() < 3 ? null : Color.fromRGB(ints.get(0), ints.get(1), ints.get(2));
	});
	public static final Serializer<World> WORLD = of(World.class, value -> value.getName(), string -> Bukkit.getWorld(string.trim()));
	public static final Serializer<Location> LOCATION = of(Location.class,
			value -> {
				return value.getWorld().getName() + ","
						+ StringUtils.toTextString(value.getX(), 3) + ","
						+ StringUtils.toTextString(value.getY(), 3) + ","
						+ StringUtils.toTextString(value.getZ(), 3) + ","
						+ StringUtils.toTextString((double) value.getYaw(), 3) + ","
						+ StringUtils.toTextString((double) value.getPitch(), 3);
			},
			string -> {
				// not enough params
				String[] split = string.split(",");
				if (split.length < 4) {
					return null;
				}
				// unknown world (entertainment)
				World world = Bukkit.getWorld(split[0]);
				if (world == null) {
					return null;
				}
				// decode
				try {
					return split.length >= 6 ? new Location(world, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5])) : new Location(world, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
				} catch (Throwable exception) {
					throw new Error("couldn't deserialize location " + string, exception);
				}
			});

	// ----------------------------------------------------------------------------------------------------
	// ----- types : linear
	// ----------------------------------------------------------------------------------------------------

	public static final LinearSerializer<BlockStateType, BlockState> BLOCK_STATE = Serializer.ofLinear(Serializer.ofEnum(BlockStateType.class), BlockState.class, (type, params) -> new BlockState(type, params));

	// ----------------------------------------------------------------------------------------------------
	// ----- types : variants
	// ----------------------------------------------------------------------------------------------------

	public static final Serializer<Sound> SOUND = of(Sound.class, value -> value.getId(), string -> Sound.firstFromIdOrDataName(string).orNull());
	public static final Serializer<Particle> PARTICLE = of(Particle.class, value -> value.getId(), string -> Particle.firstFromIdOrDataName(string).orNull());
	public static final Serializer<Mat> MAT = of(Mat.class, value -> value.getId(), string -> Mat.firstFromIdOrDataName(string).orElse(null));

}
