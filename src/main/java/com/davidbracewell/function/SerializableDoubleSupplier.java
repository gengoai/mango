
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.DoubleSupplier;

@FunctionalInterface
public interface SerializableDoubleSupplier extends DoubleSupplier, Serializable {

}//END OF SerializableDoubleSupplier
