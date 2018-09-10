package com.gengoai;

import java.lang.reflect.Type;

/**
 * @author David B. Bracewell
 */
public interface ValueTypeInformation {


   Type getValueType();

   default <T> T defaultValue(){
      return null;
   }

}//END OF ParameterName
