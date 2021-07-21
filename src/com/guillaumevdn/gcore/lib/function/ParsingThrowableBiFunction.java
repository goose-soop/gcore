package com.guillaumevdn.gcore.lib.function;

import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;

/**
 * @author GuillaumeVDN
 */
@FunctionalInterface
public interface ParsingThrowableBiFunction<A, B, R> {

	R apply(A a, B b) throws ParsingError;

}
