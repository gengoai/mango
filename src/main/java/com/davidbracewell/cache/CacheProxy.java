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
import com.davidbracewell.function.Unchecked;
import com.davidbracewell.logging.Logger;
import com.davidbracewell.reflection.Reflect;
import com.davidbracewell.reflection.ReflectionUtils;
import com.davidbracewell.string.StringUtils;
import com.davidbracewell.tuple.Tuple2;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import lombok.NonNull;

import java.io.Serializable;
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
public class CacheProxy<T> implements InvocationHandler, Serializable {
  private static final long serialVersionUID = 1L;
  private static final Logger log = Logger.getLogger(CacheProxy.class);
  private final T object;
  private final Map<String, Tuple2<Cached, KeyMaker>> cachedMethods = Maps.newHashMap();
  private final String defaultCacheName;
  private final KeyMaker defaultKeyMaker;

  protected CacheProxy(T object, String cacheName) throws Exception {
    this.object = object;
    Reflect r = Reflect.onObject(object).allowPrivilegedAccess();


    if (r.getReflectedClass().isAnnotationPresent(Cached.class)) {
      Cached cached = r.getReflectedClass().getAnnotation(Cached.class);
      defaultCacheName = StringUtils.firstNonNullOrBlank(cacheName, cached.name(), CacheManager.GLOBAL_CACHE);
      defaultKeyMaker = cached.keyMaker() == KeyMaker.DefaultKeyMaker.class ? new KeyMaker.HashCodeKeyMaker() : Reflect.onClass(cached.keyMaker()).create().get();
    } else {
      defaultCacheName = StringUtils.firstNonNullOrBlank(cacheName, CacheManager.GLOBAL_CACHE);
      defaultKeyMaker = new KeyMaker.HashCodeKeyMaker();
    }

    r.getMethods().forEach(Unchecked.consumer(method -> {
      if (method.isAnnotationPresent(Cached.class)) {
        Cached cached = method.getAnnotation(Cached.class);
        KeyMaker keyMaker = cached.keyMaker() == KeyMaker.DefaultKeyMaker.class ? defaultKeyMaker : Reflect.onClass(cached.keyMaker()).create().get();
        cachedMethods.put(method2String(method), Tuple2.of(cached, keyMaker));
      }
    }));
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

  public static <T> T cache(@NonNull Object object, String defaultCacheName) {
    try {
      return Cast.as(
        Proxy.newProxyInstance(
          object.getClass().getClassLoader(),
          ReflectionUtils.getAllInterfaces(object).toArray(new Class[1]),
          new CacheProxy<>(object, defaultCacheName)
        )
      );
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  private String method2String(Method method){
    return method.getName() + "::" + method.getParameterCount();
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Tuple2<Cached, KeyMaker> tuple = cachedMethods.get(method2String(method));
    if (tuple == null) {
      return method.invoke(object, args);
    }

    String cacheName = StringUtils.firstNonNullOrBlank(tuple.v1.name(), defaultCacheName);
    KeyMaker keyMaker = tuple.v2;
    Object key = keyMaker.make(object.getClass(), method, args);
    Cache<Object, Object> cache = CacheManager.getInstance().get(cacheName);

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
