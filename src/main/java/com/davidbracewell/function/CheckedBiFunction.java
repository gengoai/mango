
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.BiFunction;

@FunctionalInterface
public interface CheckedBiFunction<T,U,R> extends Serializable {

	R apply(T t, U u) throws Throwable;

}//END OF CheckedBiFunction
