
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.DoubleToLongFunction;

@FunctionalInterface
public interface SerializableDoubleToLongFunction extends DoubleToLongFunction, Serializable {

}//END OF SerializableDoubleToLongFunction
