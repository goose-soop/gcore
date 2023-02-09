package com.guillaumevdn.gcore.lib.integration;

import org.bukkit.plugin.Plugin;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.plugin.PluginUtils;
import com.guillaumevdn.gcore.lib.reflection.Reflection;

/**
 * @author GuillaumeVDN
 */
public class Integration<T extends IntegrationInstance> {

	private GPlugin gplugin;
	private Class<T> instanceClass;
	private T instance;
	private String pluginName;

	public Integration(GPlugin gplugin, String pluginName, Class<T> instanceClass) {
		this.gplugin = gplugin;
		this.pluginName = pluginName;
		this.instanceClass = instanceClass;
	}

	// ----- get
	public GPlugin getGPlugin() {
		return gplugin;
	}

	public String getPluginName() {
		return pluginName;
	}

	public Plugin getPlugin() {
		return PluginUtils.getPlugin(pluginName);
	}

	public <P extends Plugin> P getPlugin(Class<P> clazz) {
		return ObjectUtils.castOrNull(getPlugin(), clazz);
	}

	public T getInstance() {
		return instance;
	}

	public boolean isActivated() {
		return instance != null;
	}

	// ----- activation
	public boolean activate() throws Throwable {
		deactivate();
		Plugin plugin = getPlugin();
		if (plugin == null || !plugin.isEnabled()) {
			return false;
		}
		instance = Reflection.newInstance(instanceClass, this).get();
		instance.getSerializers().forEach(IntegrationSerializer::register);
		if (instance.activate()) {
			instance.getEvents().forEach(IntegrationEvent::registerListener);
			gplugin.getMainLogger().info("Enabled integration for " + getPluginName());
			return true;
		} else {
			deactivate();
		}
		return false;
	}

	public void deactivate() {
		if  (instance != null) {
			instance.getEvents().forEach(IntegrationEvent::unregisterListener);
			instance.deactivate();
			instance.getSerializers().forEach(IntegrationSerializer::unregister);
			instance = null;
			gplugin.getMainLogger().info("Disabled integration for " + getPluginName());
		}
	}

}
