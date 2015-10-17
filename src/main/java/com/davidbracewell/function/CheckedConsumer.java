
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.Consumer;

@FunctionalInterface
public interface CheckedConsumer<T> extends Serializable {

	void accept(T t) throws Throwable;

}//END OF CheckedConsumer
