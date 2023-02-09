package com.guillaumevdn.gcore.lib.element.struct.parsing;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public interface ParseableListElement<T, E extends ParseableElement<T>> extends ParseableElement<List<T>> {

	List<Pair<String, E>> getElements();

	@Override
	default List<T> doParse(@Nonnull Replacer replacer) throws ParsingError {
		List<T> result = new ArrayList<>();
		for (Pair<String, E> elem : getElements()) {
			elem.getB().parse(replacer).ifPresentDo(e -> result.add(e));
		}
		return result;
	}

}
