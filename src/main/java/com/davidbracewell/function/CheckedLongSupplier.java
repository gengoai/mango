
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.LongSupplier;

@FunctionalInterface
public interface CheckedLongSupplier extends Serializable {

	long getAsLong() throws Throwable;

}//END OF CheckedLongSupplier
