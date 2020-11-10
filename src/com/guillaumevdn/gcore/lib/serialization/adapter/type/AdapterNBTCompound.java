package com.guillaumevdn.gcore.lib.serialization.adapter.type;

import java.util.ArrayList;
import java.util.List;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.lib.compatibility.nbt.NBTBase;
import com.guillaumevdn.gcore.lib.compatibility.nbt.NBTCompound;
import com.guillaumevdn.gcore.lib.compatibility.nbt.NBTItem;
import com.guillaumevdn.gcore.lib.compatibility.nbt.NBTList;
import com.guillaumevdn.gcore.lib.compatibility.nbt.NBTType;
import com.guillaumevdn.gcore.lib.reflection.Reflection;
import com.guillaumevdn.gcore.lib.serialization.adapter.DataAdapter;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;
import com.guillaumevdn.gcore.lib.serialization.data.DataType;

/**
 * @author GuillaumeVDN
 */
public final class AdapterNBTCompound extends DataAdapter<NBTCompound> {

	public static final AdapterNBTCompound INSTANCE = new AdapterNBTCompound();

	private AdapterNBTCompound() {
		super(NBTCompound.class, 1);
	}

	@Override
	public void write(NBTCompound tag, DataIO writer) throws Throwable {
		writeCompound(tag, writer);
	}

	private void writeCompound(NBTCompound nbt, DataIO writer) throws Throwable {
		for (String key : nbt.getKeys()) {
			// ignore some item tags
			if (nbt.getDepth() == 0 && NBTItem.IGNORE_TAGS.contains(key)) {
				continue;
			}
			// list
			NBTType type = nbt.getValueType(key);
			if (type.equals(NBTType.LIST)) {
				writer.writeDirectList(key, writeList(nbt.getList(key)));
			}
			// object
			else if (type.equals(NBTType.COMPOUND)) {
				writer.writeObjectOrThrow(key, d -> writeCompound(nbt.getCompound(key), d));
			}
			// value
			else if (!type.equals(NBTType.UNKNOWN)) {
				writer.write(key, nbt.getObject(key));
			}
		}
	}

	private List writeList(NBTList nbt) throws Throwable {
		List list = new ArrayList();
		// list of list
		NBTType type = nbt.getValueType();
		if (type.equals(NBTType.LIST)) {
			for (int index = 0; index < nbt.size(); ++index) {
				list.add(writeList(nbt.getList(index)));
			}
		}
		// list of objects
		else if (type.equals(NBTType.COMPOUND)) {
			for (int index = 0; index < nbt.size(); ++index) {
				DataIO w = new DataIO();
				writeCompound(nbt.getCompound(index), w);
				list.add(w);
			}
		}
		// list of values
		else if (!type.equals(NBTType.UNKNOWN)) {
			for (int index = 0; index < nbt.size(); ++index) {
				list.add(nbt.get(index, type));
			}
		}
		// done
		return list;
	}

	@Override
	public NBTCompound read(int version, DataIO reader) throws Throwable {
		if (version == 1) {
			ConfigGCore.logspamItemNbt(null, () -> "Reading compound");
			return readCompound(null, "root", 0, reader);
		}
		throw new IllegalArgumentException("unknown adapter version " + version);
	}

	private NBTCompound readCompound(NBTBase parent, String name, int depth, DataIO reader) throws Throwable {
		NBTCompound nbt = new NBTCompound(parent, name, depth, Reflection.newNmsInstance("NBTTagCompound"));
		ConfigGCore.logspamItemNbt(nbt, () -> "Compound " + name + ", keys " + reader.getKeys());
		for (String key : reader.getKeys()) {
			DataType type = reader.getType(key);
			ConfigGCore.logspamItemNbt(nbt, () -> " Key " + key + ", type " + type);
			// list
			if (type.equals(DataType.LIST)) {
				nbt.setList(key, readList(nbt, key, depth + 1, reader.readDirectList(key)));
			}
			// object
			else if (type.equals(DataType.OBJECT)) {
				DataIO obj = reader.readObject(key);
				// list but formatted as section
				if (obj.getKeys().stream().anyMatch(k -> k.startsWith("__list__"))) {
					ConfigGCore.logspamItemNbt(nbt, () -> " Section list " + obj.getKeys());
					List<DataIO> components = new ArrayList<>();
					for (String k : obj.getKeys()) {
						components.add(obj.readObject(k));
					}
					nbt.setList(key, readList(nbt, key, depth + 1, components));
				}
				// actual object
				else {
					ConfigGCore.logspamItemNbt(nbt, () -> " Actual objects " + obj.getKeys());
					nbt.setCompound(key, readCompound(nbt, key, depth + 1, reader.readObject(key)));
				}
			}
			// value
			else {
				Object value = reader.readUnknown(key);
				ConfigGCore.logspamItemNbt(nbt, () -> " Value " + value);
				if (value != null) {
					nbt.setObject(key, value);
				}
			}
		}
		return nbt;
	}

	private NBTList readList(NBTBase parent, String name, int depth, List reading) throws Throwable {
		// find and validate list value type
		NBTType type = null;
		for (Object object : reading) {
			NBTType t = NBTType.getByValueClass(object);
			if (type == null) {
				if (t.equals(NBTType.UNKNOWN)) {
					throw new IllegalStateException("no nbt type found for list value of type " + object.getClass());
				}
				type = t;
			} else if (!t.equals(type)) {
				throw new IllegalStateException("inconsistent nbt type for list, previous was " + type + " and next " + t);
			}
		}
		NBTList list = new NBTList(parent, name, depth, NBTType.createListTag(type));
		final NBTType tt = type; ConfigGCore.logspamItemNbt(list, () -> "List " + name + ", type " + tt);
		// list of list
		if (type.equals(NBTType.LIST)) {
			for (int index = 0; index < reading.size(); ++index) {
				list.addList(readList(list, "__list__" + index, depth + 1, (List) reading.get(index)));
			}
		}
		// list of objects
		else if (type.equals(NBTType.COMPOUND)) {
			for (int index = 0; index < reading.size(); ++index) {
				NBTCompound comp = readCompound(list, "" + index, depth + 1, (DataIO) reading.get(index));
				final int i = index; ConfigGCore.logspamItemNbt(list, () -> " Read compound " + i + " with keys " + comp.getKeys());
				list.addCompound(comp);
			}
		}
		// list of values
		else if (!type.equals(NBTType.UNKNOWN)) {
			for (int index = 0; index < reading.size(); ++index) {
				final int i = index; ConfigGCore.logspamItemNbt(list, () -> " Read value " + i + " : " + reading.get(i));
				list.add(type, reading.get(index));
			}
		}
		ConfigGCore.logspamItemNbt(list, () -> "< Finished reading list " + name + ", size " + list.size() + ", type " + list.getValueType());
		// done
		return list;
	}

}
