
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface SerializableIntUnaryOperator extends IntUnaryOperator, Serializable {

}//END OF SerializableIntUnaryOperator
