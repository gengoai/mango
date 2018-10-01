package com.gengoai.conversion;

import java.lang.reflect.Type;

/**
 * @author David B. Bracewell
 */
public interface TypeConverter {

   Object convert(Object source, Type... parameters) throws TypeConversionException;

   Class[] getConversionType();


}//END OF TypeConverter
