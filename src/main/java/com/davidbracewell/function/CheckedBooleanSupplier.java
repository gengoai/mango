
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.BooleanSupplier;

@FunctionalInterface
public interface CheckedBooleanSupplier extends Serializable {

	boolean getAsBoolean() throws Throwable;

}//END OF CheckedBooleanSupplier
