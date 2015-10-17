
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.ToLongBiFunction;

@FunctionalInterface
public interface CheckedToLongBiFunction<T,U> extends Serializable {

	Long applyAsLong(T t, U u) throws Throwable;

}//END OF CheckedToLongBiFunction
