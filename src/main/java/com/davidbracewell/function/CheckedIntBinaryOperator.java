
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.IntBinaryOperator;

@FunctionalInterface
public interface CheckedIntBinaryOperator extends Serializable {

	int apply(int t, int u) throws Throwable;

}//END OF CheckedIntBinaryOperator
