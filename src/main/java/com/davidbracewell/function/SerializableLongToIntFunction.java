
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.LongToIntFunction;

@FunctionalInterface
public interface SerializableLongToIntFunction extends LongToIntFunction, Serializable {

}//END OF SerializableLongToIntFunction
