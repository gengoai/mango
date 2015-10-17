
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.ObjDoubleConsumer;

@FunctionalInterface
public interface CheckedObjDoubleConsumer<T> extends Serializable {

	void accept(T t, double value) throws Throwable;

}//END OF CheckedObjDoubleConsumer
