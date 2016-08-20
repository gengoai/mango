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

package com.davidbracewell.reflection;

import com.davidbracewell.string.StringUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.concurrent.TimeUnit;

/**
 * @author David B. Bracewell
 */
class ClassDescriptorCache implements Serializable {
  private static final long serialVersionUID = 1L;

  private volatile static ClassDescriptorCache INSTANCE = null;

  private final LoadingCache<Class<?>, ClassDescriptor> classDescriptorCache = CacheBuilder.newBuilder()
    .concurrencyLevel(Runtime.getRuntime().availableProcessors())
    .maximumSize(1000)
    .expireAfterAccess(10, TimeUnit.MINUTES)
    .weakKeys()
    .build(new CacheLoader<Class<?>, ClassDescriptor>() {
      @Override
      public ClassDescriptor load(Class<?> key) throws Exception {
        return new ClassDescriptor(key);
      }
    });


  private final LoadingCache<String, Class<?>> classNameCache = CacheBuilder.newBuilder()
    .concurrencyLevel(Runtime.getRuntime().availableProcessors())
    .maximumSize(1000)
    .expireAfterAccess(10, TimeUnit.MINUTES)
    .weakValues()
    .build(new CacheLoader<String, Class<?>>() {
      @Override
      public Class<?> load(String name) throws Exception {
        if (StringUtils.isNullOrBlank(name)) {
          throw new ClassNotFoundException();
        }
        name = name.trim();

        boolean isArray = false;
        if (name.endsWith("[]")) {
          isArray = true;
          name = name.substring(0, name.length() - 2);
        } else if (name.startsWith("[L")) {
          isArray = true;
          name = name.substring(2);
        } else if (name.startsWith("[")) {
          isArray = true;
          name = name.substring(1);
        }

        switch (name) {
          case "int":
            return isArray ? int[].class : int.class;
          case "double":
            return isArray ? double[].class : double.class;
          case "float":
            return isArray ? float[].class : float.class;
          case "boolean":
            return isArray ? boolean[].class : boolean.class;
          case "short":
            return isArray ? short[].class : short.class;
          case "byte":
            return isArray ? byte[].class : byte.class;
          case "long":
            return isArray ? long[].class : long.class;
        }

        Class<?> clazz;
        try {
          clazz = Class.forName(name);
        } catch (Exception e) {
          try {
            clazz = Class.forName("java.lang." + name);
          } catch (Exception e2) {
            try {
              clazz = Class.forName("java.util." + name);
            } catch (Exception e3) {
              try {
                clazz = Class.forName("com.davidbracewell." + name);
              } catch (Exception e4) {
                throw e;
              }
            }
          }
        }

        return isArray ? Array.newInstance(clazz, 0).getClass() : clazz;
      }
    });

  public static ClassDescriptorCache getInstance() {
    if (INSTANCE == null) {
      synchronized (ClassDescriptorCache.class) {
        if (INSTANCE == null) {
          INSTANCE = new ClassDescriptorCache();
        }
      }
    }
    return INSTANCE;
  }

  @SneakyThrows
  public ClassDescriptor getClassDescriptor(@NonNull Class<?> clazz) {
      return classDescriptorCache.get(clazz);
  }

  public Class<?> getClassForName(@NonNull String string) throws Exception {
    return classNameCache.get(string);
  }


}//END OF ClassDescriptorCache
