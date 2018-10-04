package com.gengoai.conversion;

import com.gengoai.collection.Collect;
import com.gengoai.collection.Iterators;
import com.gengoai.json.Json;
import com.gengoai.string.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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

      if (source instanceof CharSequence) {
         String str = source.toString();
         //Assume JSON
         if (str.startsWith("[")) {
            try {
               return Collect.addAll(newCollection(), Json.parse(source.toString()).getAsArray(elementType));
            } catch (IOException e) {
               throw new TypeConversionException(source, Collection.class, e);
            }
         }
         List<String> strList = StringUtils.split(str, ',');
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
