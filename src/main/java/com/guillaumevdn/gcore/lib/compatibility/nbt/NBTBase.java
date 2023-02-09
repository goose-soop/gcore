package com.guillaumevdn.gcore.lib.compatibility.nbt;

import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;

/**
 * @author GuillaumeVDN
 */
public abstract class NBTBase {

	protected NBTBase parent;
	private final String name;
	private final int depth;
	private ReflectionObject tag;

	protected NBTBase(NBTBase parent, String name, int depth, ReflectionObject tag) throws Throwable {
		this.parent = parent;
		this.name = name;
		this.depth = depth;
		this.tag = tag;
	}

	// ----- get
	public final NBTBase getParent() {
		return parent;
	}

	public final NBTBase getSuperParent() {
		NBTBase parent = this;
		while (parent.getParent() != null) {
			parent = parent.getParent();
		}
		return parent;
	}

	public final String getName() {
		return name;
	}

	public int getDepth() {
		return depth;
	}

	public final ReflectionObject getTag() {
		return tag;
	}

	// ----- set
	public final void setTag(ReflectionObject tag) {
		this.tag = tag;
	}
	
}
