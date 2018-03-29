/*
 * (c) 2005 David B. Bracewell
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.gengoai.mango.cache;

import com.gengoai.mango.config.Config;
import com.gengoai.mango.conversion.Cast;
import com.gengoai.mango.reflection.Reflect;
import com.gengoai.mango.string.StringUtils;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Manages the creation and retrieval of caches. Caches are defined as specification {@link CacheSpec}. Additionally,
 * there can be <code>cacheSpecClass</code> configuration setting if a special <code>CacheSpec</code> needs to be used
 * to parse the string specification.
 *
 * @author David Bracewell
 */
public final class CacheManager {

   private CacheManager() {
      throw new IllegalAccessError();
   }

   /**
    * The name of the global cache.
    */
   public static final String GLOBAL_CACHE = "com.gengoai.mango.cache.globalCache";
   private static final Map<String, Cache<?, ?>> caches = new ConcurrentHashMap<>();


   /**
    * Creates an object that automatically caches results for methods annotated with the <Code>@Cached</Code>
    * annotation.
    *
    * @param <T>    The type of the object
    * @param object The object, which must implement an interface,  that we are wrapping
    * @return The Proxy object
    */
   public static <T> T cacheObject(T object) {
      return CacheProxy.cache(object);
   }


   /**
    * Determines if a cache with a given name has been created via the CacheManager
    *
    * @param name The cache name
    * @return True if the cache has been created, false if not.
    */
   public static boolean exists(String name) {
      return caches.containsKey(name);
   }

   /**
    * Gets the cache associated with the given name. If the name is not known, i.e. the associated cache has not been
    * defined, a no operation cache (no cache) is returned.
    *
    * @param <K>  the type parameter
    * @param <V>  the type parameter
    * @param name The name of the cache
    * @return A cache
    */
   @SuppressWarnings("unchecked")
   public static <K, V> Cache<K, V> get(String name) {
      Preconditions.checkArgument(StringUtils.isNotNullOrBlank(name), "Cache name must not be null or blank.");
      if (GLOBAL_CACHE.equals(name)) {
         return Cast.as(getGlobalCache());
      } else if (caches.containsKey(name)) {
         return Cast.as(caches.get(name));
      } else if (Config.hasProperty(name) && caches.containsKey(Config.get(name).asString())) {
         return Cast.as(caches.get(Config.get(name).asString()));
      }
      return register(getCacheSpec(name));
   }

   /**
    * Gets the names of the existing managed caches
    *
    * @return The names of all of the existing caches.
    */
   public static Set<String> getCacheNames() {
      return caches.keySet();
   }

   @SneakyThrows
   private static CacheSpec getCacheSpec(String property) {

      //Sanity check for the global cache. If one is not defined in the configuration create a default with max size 1,000
      if (GLOBAL_CACHE.equals(property) && !Config.hasProperty(property)) {
         return new CacheSpec<>().name(GLOBAL_CACHE).maxSize(1000);
      }

      String specString = Config.get(property).asString();

      //Make sure the cache is defined
      checkArgument(StringUtils.isNotNullOrBlank(specString),
                    property + " is not a known cache and is not defined via a config file.");

      CacheSpec spec;
      //See if there is special implementation of the cache spec class
      if (Config.hasProperty(property + ".cacheSpecClass")) {
         spec = Reflect.onClass(Config.get(property + ".cacheSpecClass").asClass()).create().get();
      } else {
         spec = new CacheSpec<>();
      }

      try {
         spec.fromString(specString);
         if (!specString.contains("name:")) {
            spec.name(property);
         }
      } catch (Exception e) {
         throw new IllegalStateException(specString + " is an invalid specification string.");
      }

      return spec;
   }

   /**
    * Gets global cache.
    *
    * @return The global cache
    */
   @SuppressWarnings("unchecked")
   public static <K, V> Cache<K, V> getGlobalCache() {
      if (caches.containsKey(GLOBAL_CACHE)) {
         return Cast.as(caches.get(GLOBAL_CACHE));
      }
      return register(getCacheSpec(GLOBAL_CACHE));
   }

   /**
    * Creates a cache for a given cache specification. If the name in the specification is a known cache, then
    * it will return that cache.
    *
    * @param <K>           The key type
    * @param <V>           The value type
    * @param specification The specification
    * @return A cache for the specification
    */
   public static <K, V> Cache<K, V> register(@NonNull CacheSpec<K, V> specification) {
      return Cast.as(caches.computeIfAbsent(specification.getName(),
                                            name -> Cast.as(specification.getEngine().create(specification))
                                           ));
   }

}//END OF CacheManager
