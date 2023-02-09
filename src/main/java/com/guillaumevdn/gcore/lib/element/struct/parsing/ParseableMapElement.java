package com.guillaumevdn.gcore.lib.element.struct.parsing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public interface ParseableMapElement<K, V, E extends ParseableElement<V>> extends ParseableElement<Map<K, V>> {

	List<Pair<K, E>> getElements();

	@Override
	default Map<K, V> doParse(@Nonnull Replacer replacer) throws ParsingError {
		Map<K, V> result = new HashMap<>();
		for (Pair<K, E> elem : getElements()) {
			elem.getB().parse(replacer).ifPresentDo(e -> result.put(elem.getA(), e));
		}
		return result;
	}

}
