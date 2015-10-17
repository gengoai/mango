
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.DoubleUnaryOperator;

@FunctionalInterface
public interface SerializableDoubleUnaryOperator extends DoubleUnaryOperator, Serializable {

}//END OF SerializableDoubleUnaryOperator
