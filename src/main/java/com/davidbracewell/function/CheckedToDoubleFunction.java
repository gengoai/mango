
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.ToDoubleFunction;

@FunctionalInterface
public interface CheckedToDoubleFunction<T> extends Serializable {

	double applyAsDouble(T t) throws Throwable;

}//END OF CheckedToDoubleFunction
