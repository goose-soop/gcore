package com.guillaumevdn.gcore.lib.integration;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public class IntegrationEvent<T extends Event> {

	private IntegrationInstance integration;
	private Class<T> eventClass;
	private EventPriority priority;
	private boolean ignoreCancelled;
	private boolean cancellable;
	private Consumer<T> performer;

	public IntegrationEvent(IntegrationInstance integration, Class<T> eventClass, Consumer<T> performer) {
		this(integration, eventClass, EventPriority.HIGHEST, true, performer);
	}

	public IntegrationEvent(IntegrationInstance integration, Class<T> eventClass, EventPriority priority, boolean ignoreCancelled, Consumer<T> performer) {
		this.integration = integration;
		this.eventClass = eventClass;
		this.priority = priority;
		this.ignoreCancelled = ignoreCancelled;
		this.cancellable = ObjectUtils.instanceOf(eventClass, Cancellable.class);
		this.performer = performer;
	}

	// ----- get
	public IntegrationInstance getIntegration() {
		return integration;
	}

	public Class<T> getEventClass() {
		return eventClass;
	}

	public EventPriority getPriority() {
		return priority;
	}

	public boolean mustIgnoreCancelled() {
		return ignoreCancelled;
	}

	public boolean isCancellable() {
		return cancellable;
	}

	// ----- listener
	private Listener listener = null;

	public void registerListener() {
		unregisterListener();
		Bukkit.getPluginManager().registerEvent(eventClass, listener = new Listener() {}, priority, new EventExecutor() {
			@Override
			public void execute(Listener listener, Event evt) throws EventException {
				try {
					T event = (T) evt;
					if (event != null) {
						performer.accept(event);
					}
				} catch (ClassCastException ignored) {}  // seems to happen sometimes even though I asked for a class ? https://pastebin.com/raw/Lnsuv5QB
			}
		}, integration.getIntegration().getPlugin());
	}

	public void unregisterListener() {
		if (listener != null) {
			HandlerList.unregisterAll(listener);
			listener = null;
		}
	}

}
