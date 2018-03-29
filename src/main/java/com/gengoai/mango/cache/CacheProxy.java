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

import com.gengoai.mango.conversion.Cast;
import com.gengoai.mango.function.Unchecked;
import com.gengoai.mango.logging.Logger;
import com.gengoai.mango.reflection.Reflect;
import com.gengoai.mango.reflection.ReflectionUtils;
import com.gengoai.mango.string.StringUtils;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Wraps an object automatically caching calls to methods with the <code>Cached</code> annotation.
 *
 * @param <T> the type parameter
 * @author David B. Bracewell
 */
public class CacheProxy<T> implements InvocationHandler, Serializable {
   private static final long serialVersionUID = 1L;
   private static final Logger log = Logger.getLogger(CacheProxy.class);
   private final T object;
   private final Map<String, CacheInfo> cachedMethods = new HashMap<>();
   private final String defaultCacheName;
   private final KeyMaker defaultKeyMaker;

   protected CacheProxy(T object, String cacheName) throws Exception {
      this.object = object;
      Reflect r = Reflect.onObject(object).allowPrivilegedAccess();

      if (r.getReflectedClass().isAnnotationPresent(Cached.class)) {
         Cached cached = r.getReflectedClass().getAnnotation(Cached.class);
         defaultCacheName = StringUtils.firstNonNullOrBlank(cacheName, cached.name(), CacheManager.GLOBAL_CACHE);
         defaultKeyMaker = cached.keyMaker() == KeyMaker.DefaultKeyMaker.class
                           ? new KeyMaker.HashCodeKeyMaker()
                           : Reflect.onClass(cached.keyMaker()).create().get();
      } else {
         defaultCacheName = StringUtils.firstNonNullOrBlank(cacheName, CacheManager.GLOBAL_CACHE);
         defaultKeyMaker = new KeyMaker.HashCodeKeyMaker();
      }

      r.getMethods().forEach(Unchecked.consumer(method -> {
         if (method.isAnnotationPresent(Cached.class)) {
            Cached cached = method.getAnnotation(Cached.class);
            KeyMaker keyMaker = cached.keyMaker() == KeyMaker.DefaultKeyMaker.class
                                ? defaultKeyMaker
                                : Reflect.onClass(cached.keyMaker()).create().get();
            cachedMethods.put(createMethodKey(method),
                              new CacheInfo(
                                              CacheManager.get(
                                                 StringUtils.firstNonNullOrBlank(cached.name(), defaultCacheName)),
                                              keyMaker)
                             );
         }
      }));
   }

   private String createMethodKey(Method method) {
      return method.getName() + "::" + method.getParameterTypes().length + "::" + Arrays.toString(
         method.getParameterTypes());
   }

   /**
    * Creates a proxy object that automatically caches calls to methods with the <code>Cached</code> annotation.
    *
    * @param <T>    The type of the object
    * @param object The object being wrapped
    * @return The wrapped object
    */
   public static <T> T cache(Object object) {
      return cache(object, null);
   }

   @SneakyThrows
   public static <T> T cache(@NonNull Object object, String defaultCacheName) {
      return Cast.as(Proxy.newProxyInstance(object.getClass().getClassLoader(),
                                            ReflectionUtils.getAncestorInterfaces(object).toArray(new Class[1]),
                                            new CacheProxy<>(object, defaultCacheName)
                                           )
                    );
   }

   @Override
   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      final String methodKey = createMethodKey(method);
      //If this is a cached method then do the caching...
      if (cachedMethods.containsKey(methodKey)) {
         CacheInfo cacheInfo = cachedMethods.get(methodKey);
         Object key = cacheInfo.getKeyMaker().make(object.getClass(), method, args);
         try {
            return cacheInfo.getCache().get(key, Unchecked.supplier(() -> method.invoke(object, args)));
         } catch (RuntimeException e) {
            throw e.getCause();
         }
      }
      //Otherwise just invoke it
      return method.invoke(object, args);
   }


   @Value
   private static class CacheInfo implements Serializable {
      private static final long serialVersionUID = 1L;
      public Cache<Object, Object> cache;
      public KeyMaker keyMaker;
   }


}//END OF CacheProxy
