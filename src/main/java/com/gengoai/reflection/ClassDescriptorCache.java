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

import com.gengoai.cache.Cache;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.Serializable;

/**
 * <p>A cache of {@link ClassDescriptor} to speed up performance.</p>
 *
 * @author David B. Bracewell
 */
public class ClassDescriptorCache implements Serializable {
   private static final long serialVersionUID = 1L;

   private volatile static ClassDescriptorCache INSTANCE = null;

   private final Cache<Class<?>, ClassDescriptor> classDescriptorCache = Cache.create(1000, ClassDescriptor::new);

   private final Cache<String, Class<?>> classNameCache = Cache.create(1000, key -> {
      try {
         return ReflectionUtils.getClassForName(key);
      } catch (Exception e) {
         throw new RuntimeException(e);
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
