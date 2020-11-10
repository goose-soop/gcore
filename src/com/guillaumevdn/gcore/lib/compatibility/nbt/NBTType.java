package com.guillaumevdn.gcore.lib.compatibility.nbt;

import java.util.List;

import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.reflection.Reflection;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;

/**
 * @author GuillaumeVDN
 */
public enum NBTType {

	UNKNOWN(null, null, 0),
	OBJECT(null, null, 0),
	BYTE("NBTTagByte", byte.class, 1),
	SHORT("NBTTagShort", short.class, 1),
	INT("NBTTagInt", int.class, 3),
	LONG("NBTTagLong", long.class, 4),
	FLOAT("NBTTagFloat", float.class, 5),
	DOUBLE("NBTTagDouble", double.class, 6),
	STRING("NBTTagString", String.class, 8),
	BYTE_ARRAY("NBTTagByteArray", byte[].class, 7),
	INT_ARRAY("NBTTagIntArray", int[].class, 11),
	LIST("NBTTagList", null, 9),
	COMPOUND("NBTTagCompound", null, 10);

	private String nmsClass;
	private Class<?> valueClass;
	private int internalId;

	NBTType(String nmsClass, Class<?> valueClass, int internalId) {
		this.nmsClass = nmsClass;
		this.valueClass = valueClass;
		this.internalId = internalId;
	}

	// get
	public int getInternalId() {
		return internalId;
	}

	// do
	public <T> T getValue(NBTCompound compound, String key) throws Throwable {
		return compound.getTag().invokeMethod("get", key).getField("data").get();
	}

	public void setValue(NBTCompound compound, String key, Object object) throws Throwable {
		compound.getTag().invokeMethod("set", key, newNmsWrapper(object).get());
	}

	public ReflectionObject newNmsWrapper(Object value) throws Throwable {
		return nmsClass == null ? null : Reflection.newNmsInstance(nmsClass, value);
	}

	// static
	public static ReflectionObject getObject(NBTCompound compound, String key) throws Throwable {
		return compound.getTag().invokeMethod("get", key);
	}

	public static NBTType getByWrappedClass(Object wrapper) throws Throwable {
		if (wrapper != null) {
			String simpleName = wrapper.getClass().getSimpleName();
			//ConfigGCore.logspamItemNbt(null, () -> "-- Type by wrapper : " + simpleName, false);
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
			//ConfigGCore.logspamItemNbt(null, () -> "-- Type by value : " + object.getClass(), false);
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
		ReflectionObject tag = Reflection.newNmsInstance("NBTTagList");
		tag.setField("type", (byte) type.getInternalId());
		return tag;
	}

	public static ReflectionObject createCompoundTag() throws Throwable {
		return Reflection.newNmsInstance("NBTTagCompound");
	}

}
