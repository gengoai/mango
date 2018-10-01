package com.gengoai.conversion;

import com.gengoai.collection.Collect;
import com.gengoai.collection.Iterators;
import com.gengoai.json.Json;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;

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
      Class sourceClass = source.getClass();
      Type elementType = (parameters == null || parameters.length == 0) ? Object.class
                                                                        : parameters[0];

      if (CharSequence.class.isAssignableFrom(sourceClass)) {
         try {
            return Collect.addAll(newCollection(), Json.parse(source.toString()).getAsArray(elementType));
         } catch (IOException e) {
            throw new TypeConversionException(sourceClass, Collection.class, e);
         }
      }

      Collection<?> collection = newCollection();
      for (Iterator<?> iterator = Iterators.asIterator(source); iterator.hasNext(); ) {
         collection.add(Converter.convert(iterator.next(), elementType));
      }
      return collection;
   }


}//END OF CollectionTypeConverter
