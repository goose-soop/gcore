package com.guillaumevdn.gcore.lib.integration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

/**
 * @author GuillaumeVDN
 */
public abstract class IntegrationInstance {

	private Integration integration;
	private List<IntegrationSerializer> serializers = new ArrayList<>();
	private List<IntegrationEvent> events = new ArrayList<>();

	public IntegrationInstance(Integration integration) {
		this.integration = integration;
	}

	// ----- get
	public Integration getIntegration() {
		return integration;
	}

	public List<IntegrationSerializer> getSerializers() {
		return serializers;
	}

	public List<IntegrationEvent> getEvents() {
		return events;
	}

	// ----- set
	public <T> void registerSerializer(Class<T> typeClass, Function<T, String> serializer, Function<String, T> deserializer) {
		serializers.add(new IntegrationSerializer<>(typeClass, serializer, deserializer));
	}

	public <T extends Event> void registerEvent(Class<T> eventClass, Consumer<T> performer) {
		events.add(new IntegrationEvent<>(this, eventClass, performer));
	}

	public <T extends Event> void registerEvent(Class<T> eventClass, EventPriority priority, boolean ignoreCancelled, Consumer<T> performer) {
		events.add(new IntegrationEvent<>(this, eventClass, priority, ignoreCancelled, performer));
	}

	// ----- activation
	public boolean activate() {
		return true;
	}

	public void deactivate() {
	}

}
