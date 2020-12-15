package com.guillaumevdn.gcore.lib.string.placeholder;

import java.util.Arrays;

/**
 * @author GuillaumeVDN
 */
@FunctionalInterface
public interface CustomMatcher {

	final long MAX_RECURSION = 5L;

	default Object applyCheckOverflow(String placeholder) {
		if (Arrays.stream(Thread.currentThread().getStackTrace()).filter(elem -> elem.getClassName().contains("CustomMatcher")).count() > MAX_RECURSION) {
			throw new CustomMatcherOverflowError();
		}
		return justApply(placeholder);
	}

	Object justApply(String placeholder);

}
