package com.guillaumevdn.gcore.lib.parseable.editor;

import org.bukkit.entity.Player;

public abstract class ModifCallback<T> {

	// base
	private T instance;

	public ModifCallback(T instance) {
		this.instance = instance;
	}

	// get
	public T getInstance() {
		return instance;
	}

	// methods
	public final void callback(EditorGUI from, Player player) {
		onModif(instance, from, player);
	}

	public final void callback(T instance, EditorGUI from, Player player) {
		if (instance != null && (this.instance == null || this.instance.getClass().isAssignableFrom(instance.getClass()))) {
			this.instance = instance;
		}
		onModif(this.instance, from, player);
	}

	// abstract methods
	protected abstract void onModif(T instance, EditorGUI from, Player player);

}
