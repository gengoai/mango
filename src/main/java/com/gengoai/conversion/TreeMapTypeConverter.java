package com.gengoai.conversion;

import org.kohsuke.MetaInfServices;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class TreeMapTypeConverter extends MapTypeConverter {
   @Override
   public Map<?, ?> createMap() {
      return new TreeMap<>();
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(TreeMap.class, SortedMap.class);
   }
}//END OF TreeMapTypeConverter