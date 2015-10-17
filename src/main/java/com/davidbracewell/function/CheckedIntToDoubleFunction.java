
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.IntToDoubleFunction;

@FunctionalInterface
public interface CheckedIntToDoubleFunction extends Serializable {

	double applyAsDouble(int t) throws Throwable;

}//END OF CheckedIntToDoubleFunction
