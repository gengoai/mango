
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.ToIntFunction;

@FunctionalInterface
public interface CheckedToIntFunction<T> extends Serializable {

	int applyAsInt(T t) throws Throwable;

}//END OF CheckedToIntFunction
