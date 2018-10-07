package com.gengoai.conversion;

import com.gengoai.reflection.Types;
import com.gengoai.tuple.Tuple2;
import org.kohsuke.MetaInfServices;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static com.gengoai.collection.Collect.arrayOf;
import static com.gengoai.reflection.Types.getOrObject;
import static com.gengoai.reflection.Types.parameterizedType;
import static com.gengoai.tuple.Tuples.$;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class Tuple2TypeConverter implements TypeConverter {

   protected Object getValue(int index, List<?> list, Type[] parameters) throws TypeConversionException {
      if (list.size() <= index) {
         return null;
      }
      return Converter.convert(list.get(index), getOrObject(index, parameters));
   }

   protected List<?> createList(Object source, Type... parameters) throws TypeConversionException {
      if (Types.asClass(getOrObject(0, parameters)).isArray()) {
         return Converter.convert(source, List.class, Object[].class);

      }
      return Converter.convert(source, List.class);
   }

   @Override
   public Object convert(Object source, Type... parameters) throws TypeConversionException {
      if (source instanceof Map.Entry) {
         Map.Entry<?, ?> m = Cast.as(source);
         return $(Converter.convert(m.getKey(), getOrObject(0, parameters)),
                  Converter.convert(m.getValue(), getOrObject(1, parameters)));
      }
      List<?> list = createList(source, parameters);
      if (list.size() <= 2) {
         return $(getValue(0, list, parameters),
                  getValue(1, list, parameters));
      }
      throw new TypeConversionException(source, parameterizedType(Map.Entry.class, parameters));
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(Map.Entry.class, Tuple2.class);
   }
}//END OF TupleTypeConverter