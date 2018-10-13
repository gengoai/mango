package com.gengoai.conversion;

import com.gengoai.collection.Collect;
import com.gengoai.collection.Iterators;
import com.gengoai.json.Json;
import com.gengoai.json.JsonEntry;
import com.gengoai.string.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import static com.gengoai.reflection.Types.*;
import static java.util.Collections.singletonList;

/**
 * The type Collection type converter.
 *
 * @author David B. Bracewell
 */
public abstract class CollectionTypeConverter implements TypeConverter {

   /**
    * New collection collection.
    *
    * @return the collection
    */
   protected abstract Collection<?> newCollection();

   private Collection<?> fromJson(Object source, JsonEntry je, Type elementType) throws TypeConversionException {
      if (je.isArray()) {
         return Collect.addAll(newCollection(),
                               je.getAsArray(elementType));
      } else if (je.isObject() && isAssignable(Map.Entry.class, elementType)) {
         Collection<?> c = newCollection();
         for (Iterator<Map.Entry<String, JsonEntry>> itr = je.propertyIterator(); itr.hasNext(); ) {
            c.add(Converter.convert(itr.next(), elementType));
         }
         return c;
      } else if (je.isObject()) {
         return Collect.addAll(newCollection(), singletonList(Converter.convert(je.getAsMap(), elementType)));
      } else if (je.isPrimitive()) {
         return Collect.addAll(newCollection(), singletonList(je.getAsVal()
                                                                .as(elementType)));
      }
      throw new TypeConversionException(source, parameterizedType(Collection.class, elementType));
   }

   @Override
   public Object convert(Object source, Type... parameters) throws TypeConversionException {
      Type elementType = (parameters == null || parameters.length == 0) ? Object.class
                                                                        : parameters[0];

      if (source instanceof JsonEntry) {
         return fromJson(source, Cast.as(source), elementType);
      }


      if (source instanceof CharSequence) {
         String str = source.toString();

         //Try Json
         try {
            return fromJson(source, Json.parse(source.toString()), elementType);
         } catch (IOException e) {
            //Ignore and try csv style conversion
         }

         //Not Json, so try CSV
         str = str.replaceFirst("^\\[", "").replaceFirst("]$", "").trim();
         List<String> strList = new ArrayList<>();
         if (isArray(elementType) || isAssignable(Collection.class, elementType)) {
            for (String s : str.split("]")) {
               s = s.replaceFirst("^\\[", "")
                    .replaceFirst("^,", "").trim();
               strList.add(s);
            }
         } else {
            strList.addAll(StringUtils.split(str, ','));
         }


         Collection<?> newCollection = newCollection();
         for (String s : strList) {
            newCollection.add(Converter.convert(s, elementType));
         }
         return newCollection;
      }

      Collection<?> collection = newCollection();
      for (Iterator<?> iterator = Iterators.asIterator(source); iterator.hasNext(); ) {
         collection.add(Converter.convert(iterator.next(), elementType));
      }
      return collection;
   }


}//END OF CollectionTypeConverter
