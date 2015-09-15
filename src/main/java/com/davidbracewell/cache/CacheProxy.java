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

package com.davidbracewell.cache;

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.logging.Logger;
import com.davidbracewell.reflection.Reflect;
import com.davidbracewell.reflection.ReflectionException;
import com.davidbracewell.reflection.ReflectionUtils;
import com.davidbracewell.tuple.Tuple2;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.logging.Level;

/**
 * Wraps an object automatically caching calls to methods with the <code>Cached</code> annotation.
 *
 * @param <T> the type parameter
 * @author David B. Bracewell
 */
public class CacheProxy<T> implements InvocationHandler {

  private static final Logger log = Logger.getLogger(CacheProxy.class);
  private final T object;
  private final Map<Method, Tuple2<Cached, KeyMaker>> cachedMethods = Maps.newHashMap();
  private final String defaultCacheName;
  private final KeyMaker defaultKeyMaker;

  /**
   * Instantiates a new Cache proxy.
   *
   * @param object the object
   * @throws Exception the exception
   */
  protected CacheProxy(T object) throws Exception {
    this.object = object;
    Reflect r = Reflect.onObject(object).allowPrivilegedAccess();

    if (r.getReflectedClass().isAnnotationPresent(Cached.class)) {
      Cached cached = r.getReflectedClass().getAnnotation(Cached.class);
      defaultCacheName = cached.name();
      defaultKeyMaker = Reflect.onClass(cached.keyMaker()).create().get();
    } else {
      defaultCacheName = CacheManager.GLOBAL_CACHE;
      defaultKeyMaker = new KeyMaker.HashCodeKeyMaker();
    }

    r.getMethods().forEach(method -> {
      if (method.isAnnotationPresent(Cached.class)) {
        Cached cached = method.getAnnotation(Cached.class);
        try {
          cachedMethods.put(method, Tuple2.of(cached, Reflect.onClass(cached.keyMaker()).create().<KeyMaker>get()));
        } catch (ReflectionException e) {
          throw Throwables.propagate(e);
        }
      }
    });
  }

  private Method findMethod(Method childMethod) {
    for (Method m : cachedMethods.keySet()) {
      if (ReflectionUtils.methodsEqual(childMethod, m)) {
        return m;
      }
    }
    return null;
  }

  /**
   * Creates a proxy object that automatically caches calls to methods with the <code>Cached</code> annotation.
   *
   * @param <T>    The type of the object
   * @param object The object being wrapped
   * @return The wrapped object
   */
  public static <T> T newInstance(Object object) {
    Preconditions.checkNotNull(object);
    try {
      return Cast.as(Proxy.newProxyInstance(
          object.getClass().getClassLoader(),
          ReflectionUtils.getAllInterfaces(object).toArray(new Class[1]),
          new CacheProxy<>(object)
      ));
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Method cachedMethod = findMethod(method);
    if (cachedMethod == null) {
      return method.invoke(object, args);
    }

    Tuple2<Cached, KeyMaker> Tuple2 = cachedMethods.get(cachedMethod);
    KeyMaker maker = (Tuple2.getValue() instanceof KeyMaker.DefaultKeyMaker) ?
        defaultKeyMaker :
        Tuple2.getValue();
    Object key = maker.make(object.getClass(), cachedMethod, args);
    Cache<Object, Object> cache = CacheManager.getInstance().get(
        Strings.isNullOrEmpty(Tuple2.getKey().name()) ?
            defaultCacheName :
            Tuple2.getKey().name()
    );

    if (cache.containsKey(key)) {
      if (log.isLoggable(Level.FINEST)) {
        log.finest("Cache contained key: '{0}' '{1}'", method.getName(), key);
      }
      return cache.get(key);
    } else if (cache instanceof AutoCalculatingCache) {
      return cache.get(key);
    }

    Object result = method.invoke(object, args);
    cache.putIfAbsent(key, result);
    if (log.isLoggable(Level.FINEST)) {
      log.finest("Adding to cache '{0}' key : '{1}'", cache.getName(), key);
    }
    return result;
  }

}//END OF CacheProxy
