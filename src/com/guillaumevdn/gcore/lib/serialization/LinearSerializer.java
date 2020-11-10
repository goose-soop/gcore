package com.guillaumevdn.gcore.lib.serialization;

import java.util.ArrayList;
import java.util.List;

import com.guillaumevdn.gcore.lib.element.type.basic.LinearObject;
import com.guillaumevdn.gcore.lib.element.type.basic.LinearObjectType;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public abstract class LinearSerializer<T extends LinearObjectType, L extends LinearObject<T>> extends Serializer<L> {

	private final Serializer<T> typeSerializer;

	public LinearSerializer(Serializer<T> typeSerializer, Class<L> valueTypeClass, boolean register) {
		super(valueTypeClass, register);
		this.typeSerializer = typeSerializer;
	}

	public LinearSerializer(Serializer<T> typeSerializer, String valueTypeName, Class<L> valueTypeClass, boolean register) {
		super(valueTypeName, valueTypeClass, register);
		this.typeSerializer = typeSerializer;
	}

	// get
	public final Serializer<T> getTypeSerializer() {
		return typeSerializer;
	}

	// methods
	@Override
	public String serialize(L value) {
		if (value == null) {
			return "null";
		}
		return (value.getType() == null ? null : value.getType().name()) + (value.getArguments() == null || value.getArguments().isEmpty() ? "" : " " + StringUtils.toTextString(" ", value.getArguments()));
	}

	@Override
	public L deserialize(String string) {
		if (string == null || string.equalsIgnoreCase("null")) {
			return null;
		}
		List<String> split = StringUtils.split(string, " ", 1);
		T type = split.get(0).equalsIgnoreCase("null") ? null : typeSerializer.deserialize(split.get(0));
		return type == null ? null : deserialize(
				type,
				(split.size() == 1 ? new ArrayList<>() : StringUtils.split(split.get(1), " ", -1))
				);
	}

	protected abstract L deserialize(T type, List<String> params);

}
