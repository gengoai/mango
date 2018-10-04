package com.gengoai.conversion;

import java.lang.reflect.Type;

/**
 * @author David B. Bracewell
 */
public class TypeConversionException extends Exception {

   public TypeConversionException(Object source, Type destType) {
      this(source, destType, null);
   }

   public TypeConversionException(String message) {
      super(message);
   }

   public TypeConversionException(Object source, Type destType, Throwable cause) {
      super(
         "Cannot convert object (" + source + ") of type (" + source.getClass() + ") to object of type (" + destType + ")",
         cause);
   }


}//END OF TypeConversionException
