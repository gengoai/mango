
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.ToIntBiFunction;

@FunctionalInterface
public interface CheckedToIntBiFunction<T,U> extends Serializable {

	int applyAsInt(T t, U u) throws Throwable;

}//END OF CheckedToIntBiFunction
