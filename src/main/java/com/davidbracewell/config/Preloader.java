package com.davidbracewell.config;

import com.davidbracewell.io.resource.ClasspathResource;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.reflection.ReflectionUtils;
import lombok.NonNull;

import java.io.IOException;

/**
 * @author David B. Bracewell
 */
public final class Preloader {

  public static void preload() {
    preload(Thread.currentThread().getContextClassLoader());
  }

  public static void preload(@NonNull ClassLoader classLoader) {
    Resource r = new ClasspathResource("META-INF/preload.ini", classLoader);
    if (r.exists()) {
      try {
        r.readLines().forEach(ReflectionUtils::getClassForNameQuietly);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}// END OF Preloader
