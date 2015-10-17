
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.DoubleConsumer;

@FunctionalInterface
public interface CheckedDoubleConsumer extends Serializable {

	void apply(double t) throws Throwable;

}//END OF CheckedDoubleConsumer
