package com.guillaumevdn.gcore.lib.concurrency;

import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * This is to avoid having to/forgetting to synchronize manually on this set.
 * Iteration/stream methods will therefore throw an UnsupportedOperationException when called and forEach() or iterate() should be used instead.
 * @author GuillaumeVDN
 */
public class RWLowerCaseArrayList extends RWArrayList<String> {

	private static final long serialVersionUID = 2584863854373010795L;

	public RWLowerCaseArrayList(int initialCapacity) {
		super(initialCapacity);
	}

	@Override
	protected String elementModifier(Object element) {
		if (element == null) {
			return null;
		}
		if (!ObjectUtils.instanceOf(element, String.class)) {
			throw new IllegalArgumentException("key isn't a string : " + element);
		}
		return ((String) element).toLowerCase();
	}

}
