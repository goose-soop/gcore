package com.guillaumevdn.gcore.lib.compatibility.nbt;

import java.util.Objects;

import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.reflection.Reflection;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;

/**
 * @author GuillaumeVDN
 */
public final class NBTList extends NBTBase {

	public NBTList(NBTBase parent, String name, int depth, ReflectionObject tag) throws Throwable {
		super(parent, name, depth, tag);
	}

	// get value
	public final int size() throws Throwable {
		return getTag().invokeMethod("size").get();
	}

	public final NBTType getValueType() throws Throwable {
		for (int i = 0; i < size(); ++i) {
			NBTType type = NBTType.getByWrappedClass(getTag().invokeMethod(getValueMethod, i).get());
			//final int ii = i; ConfigGCore.logspamItemNbt(this, () -> "-- List type by value " + getTag().invokeMethod(getValueMethod, ii).get().getClass() + ", type " + type, false);
			if (!type.equals(NBTType.UNKNOWN)) {
				return type;
			}
		}
		return NBTType.UNKNOWN;
	}

	private static final String getValueMethod = Reflection.getForThisVersion(Version.MC_1_7_R4, "g", Version.MC_1_9_R2, "h", Version.MC_1_12_R1, "i", Version.MC_1_13_R2, "get");

	private final Object doGetObject(int index) throws Throwable {
		return getTag().invokeMethod(getValueMethod, index).getField("data").get();
	}

	public final Byte getByte(int index) throws Throwable {
		return (Byte) doGetObject(index);
	}

	public final Short getShort(int index) throws Throwable {
		return (Short) doGetObject(index);
	}

	public final Integer getInt(int index) throws Throwable {
		return (Integer) doGetObject(index);
	}

	public final Long getLong(int index) throws Throwable {
		return (Long) doGetObject(index);
	}

	public final Float getFloat(int index) throws Throwable {
		return (Float) doGetObject(index);
	}

	public final Double getDouble(int index) throws Throwable {
		return (Double) doGetObject(index);
	}

	public final String getString(int index) throws Throwable {
		return (String) doGetObject(index);
	}

	public final Boolean getBoolean(int index) throws Throwable {
		Byte value = getByte(index);
		return value != null && value.intValue() != 0;
	}

	public final Byte[] getByteArray(int index) throws Throwable {
		return (Byte[]) doGetObject(index);
	}

	public final Integer[] getIntArray(int index) throws Throwable {
		return (Integer[]) doGetObject(index);
	}

	public final NBTList getList(int index) throws Throwable {
		return new NBTList(this, "" + index, getDepth() + 1, getTag().invokeMethod(getValueMethod, index));
	}

	public final NBTCompound getCompound(int index) throws Throwable {
		return new NBTCompound(this, "" + index, getDepth() + 1, getTag().invokeMethod(getValueMethod, index));
	}

	public final Object get(int index, NBTType type) throws Throwable {
		if (type.equals(NBTType.COMPOUND)) {
			return getCompound(index);
		} else if (type.equals(NBTType.LIST)) {
			return getList(index);
		} else if (!type.equals(NBTType.UNKNOWN)) {
			return doGetObject(index);
		}
		return null;
	}

	// set value
	private static final String removeKeyMethod = Reflection.getForThisVersion(Version.MC_1_7_R4, "a", Version.MC_1_9_R2, "remove");

	public final void remove(int index) throws Throwable {
		getTag().invokeMethod(removeKeyMethod, index);
	}

	private static final String setIndexMethod = Reflection.getForThisVersion(Version.MC_1_7_R4, "a", Version.MC_1_13_R2, "set");

	private final void doSet(NBTType type, int index, Object value) throws Throwable {
		if (setIndexMethod == null) throw new UnsupportedOperationException("can't add value in nbt list for version " + Version.CURRENT);
		if (type.equals(NBTType.UNKNOWN)) throw new UnsupportedOperationException("can't add value in nbt list for type " + type);
		if (size() != 0 && !type.equals(getValueType())) throw new UnsupportedOperationException("type " + type + " doesn't match list value type " + getValueType());
		getTag().invokeMethod(setIndexMethod, type.equals(NBTType.COMPOUND) || type.equals(NBTType.LIST) ? value : type.newNmsWrapper(value).get());
	}

	public final void setObject(int index, Object value) throws Throwable {
		doSet(NBTType.OBJECT, index, value);
	}

	public final void setByte(int index, byte value) throws Throwable {
		doSet(NBTType.BYTE, index, value);
	}

	public final void setShort(int index, short value) throws Throwable {
		doSet(NBTType.SHORT, index, value);
	}

	public final void setInt(int index, int value) throws Throwable {
		doSet(NBTType.INT, index, value);
	}

	public final void setLong(int index, long value) throws Throwable {
		doSet(NBTType.LONG, index, value);
	}

	public final void setFloat(int index, float value) throws Throwable {
		doSet(NBTType.FLOAT, index, value);
	}

	public final void setDouble(int index, double value) throws Throwable {
		doSet(NBTType.DOUBLE, index, value);
	}

	public final void setBoolean(int index, boolean value) throws Throwable {
		setByte(index, (byte) (value ? 1 : 0));
	}

	public final void setString(int index, String value) throws Throwable {
		doSet(NBTType.STRING, index, value);
	}

	public final void setByteArray(int index, byte[] value) throws Throwable {
		doSet(NBTType.BYTE_ARRAY, index, value);
	}

	public final void setIntArray(int index, int[] value) throws Throwable {
		doSet(NBTType.INT_ARRAY, index, value);
	}

	public final void setList(int index, NBTList list) throws Throwable {
		list.parent = this;
		doSet(NBTType.LIST, index, list.getTag().get());
	}

	public final void setCompound(int index, NBTCompound compound) throws Throwable {
		compound.parent = this;
		doSet(NBTType.COMPOUND, index, compound.getTag().get());
	}

	public final void set(NBTType type, int index, Object object) throws Throwable {
		if (type.equals(NBTType.COMPOUND)) {
			setCompound(index, (NBTCompound) object);
		} else if (type.equals(NBTType.LIST)) {
			setList(index, (NBTList) object);
		} else if (!type.equals(NBTType.UNKNOWN)) {
			doSet(type, index, object);
		}
	}

	private final void doAdd(NBTType type, Object value) throws Throwable {
		if (setIndexMethod == null) throw new UnsupportedOperationException("can't add value in nbt list for version " + Version.CURRENT);
		if (type.equals(NBTType.UNKNOWN)) throw new UnsupportedOperationException("can't add value in nbt list for type " + type);
		if (size() != 0 && !type.equals(getValueType())) throw new UnsupportedOperationException("type " + type + " doesn't match list value type " + getValueType());
		if (Version.ATLEAST_1_14) { // add method with no index was removed in 1.14
			getTag().invokeMethod("b", size(), type.equals(NBTType.COMPOUND) || type.equals(NBTType.LIST) ? value : type.newNmsWrapper(value).get());
		} else {
			getTag().invokeMethod("add", type.equals(NBTType.COMPOUND) || type.equals(NBTType.LIST) ? value : type.newNmsWrapper(value).get());
		}
	}

	public final void addObject(Object value) throws Throwable {
		doAdd(NBTType.OBJECT, value);
	}

	public final void addByte(byte value) throws Throwable {
		doAdd(NBTType.BYTE, value);
	}

	public final void addShort(short value) throws Throwable {
		doAdd(NBTType.SHORT, value);
	}

	public final void addInt(int value) throws Throwable {
		doAdd(NBTType.INT, value);
	}

	public final void addLong(long value) throws Throwable {
		doAdd(NBTType.LONG, value);
	}

	public final void addFloat(float value) throws Throwable {
		doAdd(NBTType.FLOAT, value);
	}

	public final void addDouble(double value) throws Throwable {
		doAdd(NBTType.DOUBLE, value);
	}

	public final void addBoolean(boolean value) throws Throwable {
		addByte((byte) (value ? 1 : 0));
	}

	public final void addString(String value) throws Throwable {
		doAdd(NBTType.STRING, value);
	}

	public final void addByteArray(byte[] value) throws Throwable {
		doAdd(NBTType.BYTE_ARRAY, value);
	}

	public final void addIntArray(int[] value) throws Throwable {
		doAdd(NBTType.INT_ARRAY, value);
	}

	public final void addList(NBTList list) throws Throwable {
		list.parent = this;
		doAdd(NBTType.LIST, list.getTag().get());
	}

	public final void addCompound(NBTCompound compound) throws Throwable {
		compound.parent = this;
		doAdd(NBTType.COMPOUND, compound.getTag().get());
	}

	public final void add(NBTType type, Object object) throws Throwable {
		if (type.equals(NBTType.COMPOUND)) {
			addCompound((NBTCompound) object);
		} else if (type.equals(NBTType.LIST)) {
			addList((NBTList) object);
		} else if (!type.equals(NBTType.UNKNOWN)) {
			doAdd(type, object);
		}
	}

	// match
	public boolean match(NBTList reference, boolean exactMatch) throws Throwable {
		if (exactMatch && size() != reference.size()) return false;
		else if (!exactMatch && size() < reference.size()) return false;
		// invalid type
		NBTType type = getValueType();
		if (size() != 0 && !type.equals(reference.getValueType())) return false;
		// list of list
		if (type.equals(NBTType.LIST)) {
			for (int index = 0; index < size(); ++index) {
				if (!getList(index).match(reference.getList(index), exactMatch)) return false;
			}
		}
		// list of objects
		else if (type.equals(NBTType.COMPOUND)) {
			for (int index = 0; index < size(); ++index) {
				if (!getCompound(index).match(reference.getCompound(index), exactMatch)) return false;
			}
		}
		// list of values
		else if (!type.equals(NBTType.UNKNOWN)) {
			for (int index = 0; index < size(); ++index) {
				if (!Objects.deepEquals(get(index, type), reference.get(index, type))) return false;
			}
		}
		// seems good
		return true;
	}

}
