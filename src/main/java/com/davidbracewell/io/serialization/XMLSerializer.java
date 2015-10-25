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

package com.davidbracewell.io.serialization;

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.io.resource.Resource;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author David B. Bracewell
 */
public class XMLSerializer implements Serializer {

  @Override
  public void serialize(Object o, Resource resource) throws Exception {
    try (OutputStream outputStream = resource.outputStream();
         XMLEncoder encoder = new XMLEncoder(outputStream)
    ) {
      encoder.writeObject(o);
    }
  }

  @Override
  public <T> T deserialize(Resource resource, Class<T> clazz) throws Exception {
    try (InputStream inputStream = resource.inputStream();
         XMLDecoder decoder = new XMLDecoder(inputStream);
    ) {
      return Cast.as(decoder.readObject(), clazz);
    }
  }

}//END OF XMLSerializer
