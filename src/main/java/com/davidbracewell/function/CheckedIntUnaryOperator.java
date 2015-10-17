
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface CheckedIntUnaryOperator extends Serializable {

	int applyAsInt(int t) throws Throwable;

}//END OF CheckedIntUnaryOperator
