package com.gengoai.conversion;

import com.gengoai.tuple.NTuple;
import com.gengoai.tuple.Tuple;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.gengoai.collection.Collect.arrayOf;
import static com.gengoai.reflection.Types.getOrObject;

/**
 * @author David B. Bracewell
 */
public class NTupleTypeConverter implements TypeConverter {

   @Override
   public Object convert(Object source, Type... parameters) throws TypeConversionException {
      List<?> list = Converter.convert(source, List.class);
      List<?> conv = new ArrayList<>();
      for (int i = 0; i < list.size(); i++) {
         conv.add(Converter.convert(list.get(i), getOrObject(i, parameters)));
      }
      return NTuple.of(conv);
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(NTuple.class, Tuple.class);
   }

}//END OF NTupleTypeConverter
