
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.DoubleBinaryOperator;

@FunctionalInterface
public interface CheckedDoubleBinaryOperator extends Serializable {

	double apply(double t, double u) throws Throwable;

}//END OF CheckedDoubleBinaryOperator
