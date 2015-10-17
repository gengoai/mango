
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.IntConsumer;

@FunctionalInterface
public interface CheckedIntConsumer extends Serializable {

	void apply(int t) throws Throwable;

}//END OF CheckedIntConsumer
