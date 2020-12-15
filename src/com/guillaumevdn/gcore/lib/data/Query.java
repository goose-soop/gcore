package com.guillaumevdn.gcore.lib.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public final class Query {

	private LinkedHashMap<String, List<Object>> parts = new LinkedHashMap<>();

	public Query() {
	}

	public Query(String query, Object... params) {
		add(query, params);
	}

	public Query(String query, Collection<?> params) {
		add(query, params);
	}

	// get
	public Map<String, List<Object>> getParts() {
		return Collections.unmodifiableMap(parts);
	}

	public boolean isEmpty() {
		return parts.isEmpty();
	}

	// methods
	public Query add(String part, Object... partParams) {
		return add(part, partParams == null ? null : CollectionUtils.asList(partParams));
	}

	public Query add(String part, Collection<?> partParams) {
		List<Object> params = new ArrayList<>();
		if (partParams != null) {
			for (Object param : partParams) {
				if (param == null) throw new IllegalArgumentException("params can't be null");
				params.add(param);
			}
		}
		parts.put(part, Collections.unmodifiableList(params));
		return this;
	}

	public Query add(Query query) {
		parts.putAll(query.parts);
		return this;
	}

	// static methods
	public static Query buildSelectAll(String tableName) {
		return new Query("SELECT * FROM `" + tableName + "`;");
	}

	public static <K> Query buildSelectKeysIn(String tableName, String keyRowName, Collection<K> keys, Serializer<K> keySerializer) {
		if (keys.isEmpty()) {
			return new Query();
		}
		List<String> params = new ArrayList<>();
		keys.forEach(key -> {
			params.add(keySerializer.serialize(key));
		});
		return new Query("SELECT * FROM `" + tableName + "` WHERE `" + keyRowName + "` IN (" + StringUtils.repeatStringSeparated("?", ",", params.size()) + ");", params);
	}

}
