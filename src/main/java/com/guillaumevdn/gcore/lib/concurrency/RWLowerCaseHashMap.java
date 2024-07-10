package com.guillaumevdn.gcore.lib.concurrency;

import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * Uses a ReentrantReadWriteLock (allows multiple readers and prioritizes the only writer if any) Subsets methods will
 * throw an UnsupportedOperationException when called and forEach() or iterate() should be used instead.
 * 
 * @author GuillaumeVDN
 */
public class RWLowerCaseHashMap<V> extends RWHashMap<String, V> {

	private static final long serialVersionUID = -7860239421985019956L;

	public RWLowerCaseHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	@Override
	protected String keyModifier(Object key) {
		if (key == null) {
			return null;
		}
		if (!ObjectUtils.instanceOf(key, String.class)) {
			throw new IllegalArgumentException("key isn't a string : " + key);
		}
		return ((String) key).toLowerCase();
	}

}
