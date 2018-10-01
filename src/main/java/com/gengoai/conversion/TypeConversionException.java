package com.gengoai.conversion;

import java.lang.reflect.Type;

/**
 * @author David B. Bracewell
 */
public class TypeConversionException extends Exception {

   public TypeConversionException(Type sourceType, Type destType) {
      this(sourceType, destType, null);
   }

   public TypeConversionException(String message) {
      super(message);
   }

   public TypeConversionException(Type sourceType, Type destType, Throwable cause) {
      super("Cannot convert object of type (" + sourceType + ") to object of type (" + destType + ")", cause);
   }


}//END OF TypeConversionException
