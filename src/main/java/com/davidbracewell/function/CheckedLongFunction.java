
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.LongFunction;

@FunctionalInterface
public interface CheckedLongFunction<R> extends Serializable {

	R apply(long t) throws Throwable;

}//END OF CheckedLongFunction
