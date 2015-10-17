
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.DoubleToIntFunction;

@FunctionalInterface
public interface SerializableDoubleToIntFunction extends DoubleToIntFunction, Serializable {

}//END OF SerializableDoubleToIntFunction
