package com.davidbracewell.function;

import java.io.Serializable;

/**
 * @author David B. Bracewell
 */
@FunctionalInterface
public interface CheckedRunnable extends Serializable{

  void run() throws Throwable;

}
