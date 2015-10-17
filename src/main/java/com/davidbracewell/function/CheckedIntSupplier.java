
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.IntSupplier;

@FunctionalInterface
public interface CheckedIntSupplier extends Serializable {

	int getAsInt() throws Throwable;

}//END OF CheckedIntSupplier
