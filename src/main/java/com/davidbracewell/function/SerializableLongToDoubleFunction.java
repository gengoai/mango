
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.LongToDoubleFunction;

@FunctionalInterface
public interface SerializableLongToDoubleFunction extends LongToDoubleFunction, Serializable {

}//END OF SerializableLongToDoubleFunction
