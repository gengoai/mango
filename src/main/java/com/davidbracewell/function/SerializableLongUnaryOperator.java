
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.LongUnaryOperator;

@FunctionalInterface
public interface SerializableLongUnaryOperator extends LongUnaryOperator, Serializable {

}//END OF SerializableLongUnaryOperator
