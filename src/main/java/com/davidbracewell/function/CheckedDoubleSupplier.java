
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.DoubleSupplier;

@FunctionalInterface
public interface CheckedDoubleSupplier extends Serializable {

	double getAsDouble() throws Throwable;

}//END OF CheckedDoubleSupplier
