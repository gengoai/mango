
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.LongConsumer;

@FunctionalInterface
public interface CheckedLongConsumer extends Serializable {

	void apply(long t) throws Throwable;

}//END OF CheckedLongConsumer
