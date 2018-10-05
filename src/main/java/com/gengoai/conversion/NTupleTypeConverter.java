package com.gengoai.conversion;

import com.gengoai.tuple.NTuple;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
public class NTupleTypeConverter implements TypeConverter {

   @Override
   public Object convert(Object source, Type... parameters) throws TypeConversionException {
      List<?> list = Converter.convert(source, List.class);
      List<?> conv = new ArrayList<>();
      for (int i = 0; i < list.size(); i++) {
         conv.add(Converter.convert(list.get(i),
                                    parameters.length > i ? parameters[i] : Object.class));
      }
      return NTuple.of(conv);
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(NTuple.class);
   }
}//END OF NTupleTypeConverter
