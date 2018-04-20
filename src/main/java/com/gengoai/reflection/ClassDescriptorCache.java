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

package com.gengoai.reflection;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * <p>A cache of {@link ClassDescriptor} to speed up performance.</p>
 *
 *
 * @author David B. Bracewell
 */
public class ClassDescriptorCache implements Serializable {
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
         return ReflectionUtils.getClassForName(name);
      }
    });

  /**
   * Gets instance.
   *
   * @return the instance
   */
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

  /**
   * Gets class descriptor.
   *
   * @param clazz the clazz
   * @return the class descriptor
   */
  @SneakyThrows
  public ClassDescriptor getClassDescriptor(@NonNull Class<?> clazz) {
      return classDescriptorCache.get(clazz);
  }

  /**
   * Gets class for name.
   *
   * @param string the string
   * @return the class for name
   * @throws Exception the exception
   */
  public Class<?> getClassForName(@NonNull String string) throws Exception {
    return classNameCache.get(string);
  }



}//END OF ClassDescriptorCache
