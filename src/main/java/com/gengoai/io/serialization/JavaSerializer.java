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

package com.gengoai.io.serialization;

import com.gengoai.conversion.Cast;
import com.gengoai.io.resource.Resource;
import lombok.NonNull;

import java.io.*;

/**
 * Serializer implementation that uses the standard Java serialization capabilities.
 *
 * @author David B. Bracewell
 */
public class JavaSerializer implements Serializer, Serializable {

   private static final long serialVersionUID = 4353403790890512522L;

   @Override
   public void serialize(@NonNull Object o, @NonNull Resource resource) throws Exception {
      try (OutputStream os = resource.outputStream(); ObjectOutputStream oos = new ObjectOutputStream(os)) {
         oos.writeObject(o);
         oos.flush();
      }
   }

   @Override
   public <T> T deserialize(@NonNull Resource resource, Class<T> clazz) throws Exception {
      try (InputStream is = resource.inputStream(); ObjectInputStream ois = new ObjectInputStream(is);) {
         return Cast.as(ois.readObject(), clazz);
      }
   }

}//END OF JavaSerializer
