package com.gengoai.conversion;

import com.gengoai.tuple.*;
import org.kohsuke.MetaInfServices;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static com.gengoai.collection.Collect.arrayOf;
import static com.gengoai.tuple.Tuples.$;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class TupleTypeConverter implements TypeConverter {

   private Object createTuple(Object source, Type... parameters) throws TypeConversionException {
      List<?> list = Converter.convert(source, List.class);
      if (list.size() != parameters.length) {
         throw new TypeConversionException(source, Map.Entry.class);
      }
      switch (parameters.length) {
         case 2:
            return $(Converter.convert(list.get(0), parameters[0]),
                     Converter.convert(list.get(1), parameters[1]));
         case 3:
            return $(Converter.convert(list.get(0), parameters[0]),
                     Converter.convert(list.get(1), parameters[1]),
                     Converter.convert(list.get(2), parameters[2]));
         default:
            return $(Converter.convert(list.get(0), parameters[0]),
                     Converter.convert(list.get(1), parameters[1]),
                     Converter.convert(list.get(2), parameters[2]),
                     Converter.convert(list.get(3), parameters[3]));
      }
   }

   @Override
   public Object convert(Object source, Type... parameters) throws TypeConversionException {
      switch (parameters.length) {
         case 1:
            return $(Converter.<Object>convert(source, parameters[0]));
         case 2:
            if (source instanceof Map.Entry) {
               Map.Entry<?, ?> m = Cast.as(source);
               return $(Converter.convert(m.getKey(), parameters[0]),
                        Converter.convert(m.getValue(), parameters[1]));
            }
            return createTuple(source, parameters);
         case 3:
            if (source instanceof Tuple3) {
               Tuple3<?, ?, ?> m = Cast.as(source);
               return $(Converter.convert(m.v1, parameters[0]),
                        Converter.convert(m.v2, parameters[1]),
                        Converter.convert(m.v3, parameters[2]));
            }
            return createTuple(source, parameters);
         default:
            if (source instanceof Tuple4) {
               Tuple4<?, ?, ?, ?> m = Cast.as(source);
               return $(Converter.convert(m.v1, parameters[0]),
                        Converter.convert(m.v2, parameters[1]),
                        Converter.convert(m.v3, parameters[2]),
                        Converter.convert(m.v3, parameters[3]));
            }
            return createTuple(source, parameters);
      }
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(Map.Entry.class,
                     Tuple.class,
                     Tuple1.class,
                     Tuple2.class,
                     Tuple3.class,
                     Tuple4.class);
   }
}//END OF TupleTypeConverter
