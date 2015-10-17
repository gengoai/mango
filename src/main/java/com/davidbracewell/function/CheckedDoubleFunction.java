
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.DoubleFunction;

@FunctionalInterface
public interface CheckedDoubleFunction<R> extends Serializable {

	R apply(double t) throws Throwable;

}//END OF CheckedDoubleFunction
