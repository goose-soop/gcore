package com.guillaumevdn.gcore.lib.integration;

import java.util.function.Function;

import com.guillaumevdn.gcore.lib.serialization.Serializer;

/**
 * @author GuillaumeVDN
 */
public class IntegrationSerializer<T> {

	private Class<T> typeClass;
	private Function<T, String> serializer;
	private Function<String, T> deserializer;

	public IntegrationSerializer(Class<T> typeClass, Function<T, String> serializer, Function<String, T> deserializer) {
		this.typeClass = typeClass;
		this.serializer = serializer;
		this.deserializer = deserializer;
	}

	// get
	public Class<T> getTypeClass() {
		return typeClass;
	}

	public Function<T, String> getSerializer() {
		return serializer;
	}

	public Function<String, T> getDeserializer() {
		return deserializer;
	}

	// do
	public void register() {
		Serializer.of(typeClass, serializer, deserializer);
	}

	public void unregister() {
		Serializer.unregister(typeClass);
	}

}
