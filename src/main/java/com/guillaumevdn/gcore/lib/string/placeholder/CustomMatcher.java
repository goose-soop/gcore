package com.guillaumevdn.gcore.lib.string.placeholder;

/**
 * @author GuillaumeVDN
 */
@FunctionalInterface
public interface CustomMatcher {

	// final long MAX_RECURSION = 5L;

	default Object applyCheckOverflow(String placeholder) {
		/*
		 * This is not really optimized (#1734) and it'll throw an error anyways, just a less nice one if
		 * (Arrays.stream(Thread.currentThread().getStackTrace()).filter(elem ->
		 * elem.getClassName().contains("CustomMatcher")).count() > MAX_RECURSION) { throw new CustomMatcherOverflowError(); }
		 */
		return justApply(placeholder);
	}

	Object justApply(String placeholder);

}
