package com.guillaumevdn.gcore.lib.compatibility.nbt;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.reflection.Reflection;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;

/**
 * @author GuillaumeVDN
 */
public enum NBTType {

	UNKNOWN(null, null, 0), ANY(null, null, 0), BYTE("NBTTagByte", byte.class, 1, CollectionUtils.asMap(Version.MC_1_17_R1, "x")),
	SHORT("NBTTagShort", short.class, 1, CollectionUtils.asMap(Version.MC_1_17_R1, "c")),
	INT("NBTTagInt", int.class, 3, CollectionUtils.asMap(Version.MC_1_17_R1, "c")),
	LONG("NBTTagLong", long.class, 4, CollectionUtils.asMap(Version.MC_1_17_R1, "c")),
	FLOAT("NBTTagFloat", float.class, 5, CollectionUtils.asMap(Version.MC_1_17_R1, "w")),
	DOUBLE("NBTTagDouble", double.class, 6, CollectionUtils.asMap(Version.MC_1_17_R1, "w")),
	STRING("NBTTagString", String.class, 8, CollectionUtils.asMap(Version.MC_1_17_R1, "A")),
	BYTE_ARRAY("NBTTagByteArray", byte[].class, 7, CollectionUtils.asMap(Version.MC_1_17_R1, "c")),
	INT_ARRAY("NBTTagIntArray", int[].class, 11, CollectionUtils.asMap(Version.MC_1_17_R1, "c")),
	LIST("NBTTagList", null, 9, CollectionUtils.asMap(Version.MC_1_17_R1, "c")),
	COMPOUND("NBTTagCompound", null, 10, CollectionUtils.asMap(Version.MC_1_17_R1, "x"));

	private String nmsClass;
	private Class<?> valueClass;
	private int internalId;
	private String dataFieldName = null;

	NBTType(String nmsClass, Class<?> valueClass, int internalId) {
		this(nmsClass, valueClass, internalId, null);
	}

	NBTType(String nmsClass, Class<?> valueClass, int internalId, Map<Version, String> dataFieldName) {
		this.nmsClass = nmsClass;
		this.valueClass = valueClass;
		this.internalId = internalId;

		if (dataFieldName != null) {
			for (Version version : dataFieldName.keySet().stream().sorted(Comparator.comparing(Version::ordinal).reversed()).collect(Collectors.toList())) {
				if (Version.CURRENT.isMoreOrEqualsTo(version)) {
					this.dataFieldName = dataFieldName.get(version);
					break;
				}
			}
		}
		if (this.dataFieldName == null) {
			this.dataFieldName = "data";
		}
	}

	// ----- get
	public int getInternalId() {
		return internalId;
	}

	public String getDataFieldName() {
		return dataFieldName;
	}

	// ----- do
	public <T> T getValue(NBTCompound compound, String key) throws Throwable {
		ReflectionObject value = compound.getTag().invokeMethod(Version.ATLEAST_1_18 ? "c" : "get", key);
		return value.justGet() == null ? null : value.getField(dataFieldName).get();
	}

	public void setValue(NBTCompound compound, String key, Object object) throws Throwable {
		compound.getTag().invokeMethod(Version.ATLEAST_1_18 ? "a" : "set", key, newNmsWrapper(object).get());
	}

	public ReflectionObject newNmsWrapper(Object value) throws Throwable {
		return nmsClass == null ? null : Reflection.newNmsInstance(Version.REMAPPED ? "nbt." + nmsClass : nmsClass, value);
	}

	// ----- static
	public static ReflectionObject getObject(NBTCompound compound, String key) throws Throwable {
		return compound.getTag().invokeMethod(Version.ATLEAST_1_18 ? "c" : "get", key);
	}

	public static NBTType getByWrappedClass(Object wrapper) throws Throwable {
		if (wrapper != null) {
			String simpleName = wrapper.getClass().getSimpleName();
			// ConfigGCore.logspamItemNbt(null, () -> "-- Type by wrapper : " + simpleName, false);
			for (NBTType type : NBTType.values()) {
				if (simpleName.equals(type.nmsClass)) {
					return type;
				}
			}
		}
		return UNKNOWN;
	}

	public static NBTType getByValueClass(Object object) throws Throwable {
		if (object != null) {
			// ConfigGCore.logspamItemNbt(null, () -> "-- Type by value : " + object.getClass(), false);
			if (object instanceof DataIO) {
				return NBTType.COMPOUND;
			}
			if (object instanceof List) {
				return NBTType.LIST;
			}
			for (NBTType type : NBTType.values()) {
				if (ObjectUtils.instanceOf(object, type.valueClass)) {
					return type;
				}
			}
		}
		return UNKNOWN;
	}

	public static ReflectionObject createListTag(NBTType type) throws Throwable {
		ReflectionObject tag = Reflection.newNmsInstance(Version.REMAPPED ? "nbt.NBTTagList" : "NBTTagList");
		tag.setField(Version.REMAPPED ? "w" : "type", (byte) type.getInternalId());
		return tag;
	}

	public static ReflectionObject createCompoundTag() throws Throwable {
		return Reflection.newNmsInstance(Version.REMAPPED ? "nbt.NBTTagCompound" : "NBTTagCompound");
	}

}
