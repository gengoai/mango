
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.LongToDoubleFunction;

@FunctionalInterface
public interface CheckedLongToDoubleFunction extends Serializable {

	double applyAsDouble(long t) throws Throwable;

}//END OF CheckedLongToDoubleFunction
