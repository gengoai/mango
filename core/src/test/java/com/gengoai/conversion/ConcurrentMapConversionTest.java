package com.gengoai.conversion;

import org.jboss.netty.util.internal.ConcurrentHashMap;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * @author David B. Bracewell
 */
public class ConcurrentMapConversionTest extends BaseMapConversionTest {
   public ConcurrentMapConversionTest() {
      super(ConcurrentMap.class);
   }

   @Override
   protected Map<Object, Object> newMap() {
      return new ConcurrentHashMap<>();
   }
}//END OF HashMapConversionTest
