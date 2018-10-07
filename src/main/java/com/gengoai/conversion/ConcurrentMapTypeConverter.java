package com.gengoai.conversion;

import org.kohsuke.MetaInfServices;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class ConcurrentMapTypeConverter extends MapTypeConverter {
   @Override
   public Map<?, ?> createMap() {
      return new ConcurrentHashMap<>();
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(ConcurrentHashMap.class, ConcurrentMap.class);
   }
}//END OF ConcurrentMapTypeConverter