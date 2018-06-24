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

import com.gengoai.io.resource.Resource;

import javax.xml.bind.JAXB;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>XML Serializer implementation.</p>
 *
 * @author David B. Bracewell
 */
public class XMLSerializer implements Serializer {

   @Override
   public void serialize(Object o, Resource resource) throws Exception {
      try (OutputStream outputStream = resource.outputStream()) {
         JAXB.marshal(o, outputStream);
      }
   }

   @Override
   public <T> T deserialize(Resource resource, Class<T> clazz) throws Exception {
      try (InputStream inputStream = resource.inputStream()) {
         return JAXB.unmarshal(inputStream, clazz);
      }
   }

}//END OF XMLSerializer
