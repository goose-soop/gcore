package com.guillaumevdn.gcore.lib.reflection.field;

import java.util.Map.Entry;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;

/**
 * @author GuillaumeVDN
 */
public class IntMapper extends FieldMapper<Integer> {

	public IntMapper() {
	}

	public static IntMapper fromMap(Object... mapElements) {
		final IntMapper mapper = new IntMapper();
		for (Entry<Object, Object> entry : CollectionUtils.asMap(mapElements).entrySet()) {
			if ((boolean) entry.getKey()) {
				mapper.setIf(true, (int) entry.getValue());
				break;
			}
		}
		return mapper;
	}

}
