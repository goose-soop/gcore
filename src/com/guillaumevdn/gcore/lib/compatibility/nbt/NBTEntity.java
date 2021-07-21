package com.guillaumevdn.gcore.lib.compatibility.nbt;

import org.bukkit.entity.Entity;

import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.reflection.Reflection;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;

/**
 * @author GuillaumeVDN
 */
public class NBTEntity extends NBTCompound {

	private Entity entity;

	public NBTEntity(Entity entity) throws Throwable {
		super(null, "root", 0, getTag(entity));
		this.entity = entity;
	}

	private static ReflectionObject getTag(Entity entity) throws Throwable {
		ReflectionObject nbtEntity = ReflectionObject.of(entity);
		ReflectionObject tag = Reflection.newNmsInstance(Version.REMAPPED ? "nbt.NBTTagCompound" : "NBTTagCompound");
		nbtEntity.invokeMethod("getHandle").invokeVoidMethod(Version.ATLEAST_1_16 ? "saveData" : "b", tag.get(Object.class));  // nbt entity isn't stored directly, so it needs an empty nbt compound to copy its properties to
		return tag;
	}

	// ----- methods
	public void applyTag() throws Throwable {
		ReflectionObject.of(entity).invokeMethod("getHandle").invokeVoidMethod(Version.ATLEAST_1_16 ? "loadData" : "a", getTag().justGet());  // load data from the nbt tag to the entity
	}

}
