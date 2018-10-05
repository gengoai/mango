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

   @Override
   public Object convert(Object source, Type... parameters) throws TypeConversionException {
      Type elementType = (parameters == null || parameters.length == 0) ? Object.class
                                                                        : parameters[0];

      if (source instanceof JsonEntry) {
         JsonEntry je = Cast.as(source);
         if (je.isArray()) {
            return Collect.addAll(newCollection(),
                                  je.getAsArray(elementType));
         } else if (je.isObject() && isAssignable(Map.Entry.class, elementType)) {
            Collection<?> c = newCollection();
            for (Iterator<Map.Entry<String, JsonEntry>> itr = je.propertyIterator(); itr.hasNext(); ) {
               c.add(Converter.convert(itr.next(), elementType));
            }
            return c;
         } else if (je.isPrimitive()) {
            return Collect.addAll(newCollection(), singletonList(je.getAsVal().cast()));
         }
         throw new TypeConversionException(source, parameterizedType(Collection.class, parameters));
      }


      if (source instanceof CharSequence) {
         String str = source.toString();
         //Assume JSON
         if (str.startsWith("[")) {
            try {
               return Collect.addAll(newCollection(), Json.parse(source.toString()).getAsArray(elementType));
            } catch (IOException e) {
               //Ignore and try csv style conversion
            }
         }

         //Fallback to csv
         List<String> strList = new ArrayList<>();
         if (asClass(elementType).isArray()) {
            str = str.replaceFirst("^\\[", "").replaceFirst("]$", "").trim();
            if (str.startsWith("[")) {
               for (String s : StringUtils.split(str, ',')) {
                  strList.add(s.replaceFirst("^\\[", "").replaceFirst("]$", ""));
               }
            } else {
               strList.add(str);
            }
         } else {
            strList = StringUtils.split(str.replaceFirst("^\\[", "").replaceFirst("]$", ""), ',');
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
