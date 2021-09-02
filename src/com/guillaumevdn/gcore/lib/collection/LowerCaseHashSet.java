package com.guillaumevdn.gcore.lib.collection;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public class LowerCaseHashSet extends HashSet<String> {

	private static final long serialVersionUID = 8645174203269985787L;

	public LowerCaseHashSet(int initialCapacity) {
		super(initialCapacity);
	}

	@Override
	public boolean containsAll(Collection<?> elements) {
		return super.containsAll(lower(elements));
	}

	@Override
	public boolean add(String element) {
		return super.add(lower(element));
	}

	@Override
	public boolean addAll(Collection<? extends String> elements) {
		return super.addAll(lower(elements));
	}

	@Override
	public boolean contains(Object element) {
		return super.contains(lower(element));
	}

	@Override
	public boolean remove(Object element) {
		return super.remove(lower(element));
	}

	@Override
	public boolean removeAll(Collection<?> elements) {
		return super.removeAll(lower(elements));
	}

	private static String lower(Object key) {
		if (key == null) {
			return null;
		}
		if (!ObjectUtils.instanceOf(key, String.class)) {
			throw new IllegalArgumentException("key isn't a string : " + key);
		}
		return ((String) key).toLowerCase();
	}

	private static List<String> lower(Collection<?> coll) {
		return coll.stream().map(obj -> lower(obj)).collect(Collectors.toList());
	}

}
