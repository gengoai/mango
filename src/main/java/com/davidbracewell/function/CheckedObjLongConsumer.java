
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.ObjLongConsumer;

@FunctionalInterface
public interface CheckedObjLongConsumer<T> extends Serializable {

	void accept(T t, long value) throws Throwable;

}//END OF CheckedObjLongConsumer
