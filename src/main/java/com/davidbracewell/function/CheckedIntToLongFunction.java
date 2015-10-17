
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.IntToLongFunction;

@FunctionalInterface
public interface CheckedIntToLongFunction extends Serializable {

	long applyAsLong(int t) throws Throwable;

}//END OF CheckedIntToLongFunction
