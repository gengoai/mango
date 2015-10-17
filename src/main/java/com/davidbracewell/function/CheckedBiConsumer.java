
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.BiConsumer;

@FunctionalInterface
public interface CheckedBiConsumer<T,U> extends Serializable {

	void accept(T t, U u) throws Throwable;

}//END OF CheckedBiConsumer
