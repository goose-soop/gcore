package com.guillaumevdn.gcore.lib.element.struct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.serialization.Serializer;

/**
 * @author GuillaumeVDN
 */
public final class Need {

	private final NeedType need;
	private final Object def;

	private Need(NeedType need, Object def) {
		this.need = need;
		this.def = def;
	}

	// get
	public NeedType getType() {
		return need;
	}

	public boolean isRequired() {
		return need.equals(NeedType.REQUIRED);
	}

	public Object getDef() {
		return def;
	}

	public <T> List<String> serializeDef(Serializer<T> serializer) {
		if (!isRequired()) {
			T single = ObjectUtils.castOrNull(def, serializer.getTypeClass());
			if (single != null) {
				return CollectionUtils.asList(serializer.serialize(single));
			}
			Collection coll = ObjectUtils.castOrNull(def, Collection.class);
			if (coll != null) {
				List<String> def = new ArrayList<>();
				for (Object elem : coll) {
					def.add(serializer.serialize((T) elem));
				}
				return def;
			}
		}
		return null;
	}

	// static
	private static final Need REQUIRED = new Need(NeedType.REQUIRED, null);
	private static final Need OPTIONAL_EMPTY = new Need(NeedType.OPTIONAL, null);

	public static Need required() {
		return REQUIRED;
	}

	public static Need optional() {
		return OPTIONAL_EMPTY;
	}

	public static Need optional(Object def) {
		return new Need(NeedType.OPTIONAL, def);
	}

}
