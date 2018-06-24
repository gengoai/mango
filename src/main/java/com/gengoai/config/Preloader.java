package com.gengoai.config;

import com.gengoai.io.resource.ClasspathResource;
import com.gengoai.io.resource.Resource;
import com.gengoai.reflection.ReflectionUtils;

import java.io.IOException;

/**
 * <p>Does a safe <code>Class.forName</code> on entries in <code>META-INF/preload.classes</code></p>
 *
 * @author David B. Bracewell
 */
public final class Preloader {

   /**
    * Preloads using the current thread's context class loader and the Preloader's, class loader.
    */
   public static void preload() {
      preload(Thread.currentThread().getContextClassLoader());
      preload(Preloader.class.getClassLoader());
   }

   /**
    * Preloads using the given class loader.
    *
    * @param classLoader the class loader to scan for the preload.classes file
    */
   public static void preload(ClassLoader classLoader) {
      Resource r = new ClasspathResource("META-INF/preload.classes", classLoader);
      if (r.exists()) {
         try {
            r.readLines().forEach(ReflectionUtils::getClassForNameQuietly);
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

}// END OF Preloader
