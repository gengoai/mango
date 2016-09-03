package com.davidbracewell.config;

import com.davidbracewell.io.resource.ClasspathResource;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.reflection.ReflectionUtils;
import lombok.NonNull;

import java.io.IOException;

/**
 * The type Preloader.
 *
 * @author David B. Bracewell
 */
public final class Preloader {

  /**
   * Preload.
   */
  public static void preload() {
    preload(Thread.currentThread().getContextClassLoader());
  }

  /**
   * Preload.
   *
   * @param classLoader the class loader
   */
  public static void preload(@NonNull ClassLoader classLoader) {
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
