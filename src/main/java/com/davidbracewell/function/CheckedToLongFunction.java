
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.ToLongFunction;

@FunctionalInterface
public interface CheckedToLongFunction<T> extends Serializable {

	long applyAsLong(T t) throws Throwable;

}//END OF CheckedToLongFunction
