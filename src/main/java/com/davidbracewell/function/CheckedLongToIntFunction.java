
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.LongToIntFunction;

@FunctionalInterface
public interface CheckedLongToIntFunction extends Serializable {

	int applyAsInt(long t) throws Throwable;

}//END OF CheckedLongToIntFunction
