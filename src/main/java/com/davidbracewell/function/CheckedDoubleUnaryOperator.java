
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.DoubleUnaryOperator;

@FunctionalInterface
public interface CheckedDoubleUnaryOperator extends Serializable {

	double applyAsDouble(double t) throws Throwable;

}//END OF CheckedDoubleUnaryOperator
