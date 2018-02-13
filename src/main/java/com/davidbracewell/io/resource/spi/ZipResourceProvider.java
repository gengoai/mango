package com.davidbracewell.io.resource.spi;

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.resource.ZipResource;
import com.davidbracewell.reflection.BeanMap;
import org.kohsuke.MetaInfServices;

import java.util.Map;

/**
 * @author David B. Bracewell
 */
@MetaInfServices
public class ZipResourceProvider implements ResourceProvider {
   @Override
   public String[] getProtocols() {
      return new String[]{"zip", "jar"};
   }

   @Override
   public Resource createResource(String specification, Map<String, String> properties) {
      BeanMap beanMap = new BeanMap(new ZipResource(specification, null));
      beanMap.putAll(properties);
      return Cast.as(beanMap.getBean());
   }

   @Override
   public boolean requiresProtocol() {
      return false;
   }

}//END OF ZipResourceProvider
