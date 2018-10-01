package com.gengoai.conversion;

import com.gengoai.collection.Iterators;
import com.gengoai.function.Unchecked;
import com.gengoai.json.Json;
import com.gengoai.json.JsonEntry;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

import static com.gengoai.reflection.Types.parameterizedType;

/**
 * @author David B. Bracewell
 */
public abstract class MapTypeConverter implements TypeConverter {


   @Override
   public Object convert(Object source, Type... parameters) throws TypeConversionException {
      Map<?, ?> map = createMap();

      Type keyType = (parameters == null || parameters.length == 0) ? Object.class
                                                                    : parameters[0];
      Type valueType = (parameters == null || parameters.length < 2) ? Object.class
                                                                     : parameters[1];

      //In the case that the source object is a map, convert its keys and values
      if (source instanceof Map) {
         for (Map.Entry<?, ?> entry : ((Map<?, ?>) source).entrySet()) {
            map.put(Converter.convert(entry.getKey(), keyType),
                    Converter.convert(entry.getValue(), valueType));
         }
         return map;
      }


      //CharSequences should be JSON format, so process it with JsonEntry
      if (source instanceof JsonEntry || source instanceof CharSequence) {
         try {
            JsonEntry json = (source instanceof JsonEntry) ? Cast.as(source)
                                                           : Json.parse(source.toString());
            if (!json.isObject()) {
               throw new TypeConversionException(source.getClass(), Map.class);
            }
            json.propertyIterator()
                .forEachRemaining(Unchecked.consumer(
                   (Map.Entry<String, JsonEntry> entry) -> map.put(
                      Converter.convert(entry.getKey(), keyType),
                      Converter.convert(entry.getValue(), valueType))));
            return map;
         } catch (RuntimeException | IOException e) {
            throw new TypeConversionException(source.getClass(),
                                              parameterizedType(Map.class, keyType, valueType),
                                              e.getCause() == null ? e : e.getCause());
         }
      }

      //Last chance is to try and convert the source into an iterable and process the times in the iterable as key value pairs.

      for (Iterator<?> iterator = Iterators.asIterator(source); iterator.hasNext(); ) {
         Object o = iterator.next();
         Object key;
         Object value;
         if (o instanceof Map.Entry) {
            key = Cast.<Map.Entry>as(o).getKey();
            value = Cast.<Map.Entry>as(o).getValue();
         } else {
            key = o;
            if (!iterator.hasNext()) {
               throw new TypeConversionException(source.getClass(), parameterizedType(Map.class, keyType, valueType));
            }
            value = iterator.next();
         }
         map.put(Converter.convert(key, keyType), Converter.convert(value, valueType));
      }
      return map;
   }

   public abstract Map<?, ?> createMap();

}//END OF MapTypeConverter
