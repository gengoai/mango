package com.gengoai.config;

import com.gengoai.io.Resources;
import com.gengoai.logging.Logger;
import com.gengoai.reflection.Reflect;

import java.io.IOException;

/**
 * <p>Does a safe <code>Class.forName</code> on entries in <code>META-INF/preload.classes</code></p>
 *
 * @author David B. Bracewell
 */
public final class Preloader {
   private static final Logger log = Logger.getLogger(Preloader.class);

   /**
    * Preloads using the current thread's context class loader and the Preloader's, class loader.
    */
   public static void preload() {
      Resources.findAllClasspathResources("META-INF/preload.classes")
               .forEachRemaining(r -> {
                  try {
                     r.readLines().forEach(Reflect::getClassForNameQuietly);
                  } catch (IOException e) {
                     log.warn("Exception Preloading: {0}", e);
                  }
               });
   }

}// END OF Preloader
